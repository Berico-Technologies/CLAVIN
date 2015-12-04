/*#####################################################################
 *
 * CLAVIN (Cartographic Location And Vicinity INdexer)
 * ---------------------------------------------------
 *
 * Copyright (C) 2012-2013 Berico Technologies
 * http://clavin.bericotechnologies.com
 *
 * ====================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * ====================================================================
 *
 * LuceneGazetteer.java
 *
 *###################################################################*/

package com.bericotech.clavin.gazetteer.query;

import static com.bericotech.clavin.index.IndexField.*;
import static org.apache.lucene.queryparser.classic.QueryParserBase.escape;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.FeatureCode;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.index.BinarySimilarity;
import com.bericotech.clavin.index.IndexField;
import com.bericotech.clavin.index.WhitespaceLowerCaseAnalyzer;
import com.bericotech.clavin.resolver.ResolvedLocation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.analyzing.AnalyzingQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of Gazetteer that uses Lucene to rapidly search
 * known locations.
 */
public class LuceneGazetteer implements Gazetteer {
    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LuceneGazetteer.class);

    /**
     * Index employs simple lower-casing & tokenizing on whitespace.
     */
    private static final Analyzer INDEX_ANALYZER = new WhitespaceLowerCaseAnalyzer();

    /**
     * Custom Lucene sorting based on Lucene match score and the
     * population of the GeoNames gazetteer entry represented by the
     * matched index document.
     */
    private static final Sort POPULATION_SORT = new Sort(new SortField[] {
        SortField.FIELD_SCORE,
        // new SortField(POPULATION.key(), SortField.Type.LONG, true)
        new SortField(SORT_POP.key(), SortField.Type.LONG, true)
    });

    /**
     * The default number of results to return.
     */
    private static final int DEFAULT_MAX_RESULTS = 5;

    /**
     * The set of all FeatureCodes.
     */
    private static final Set<FeatureCode> ALL_CODES = Collections.unmodifiableSet(EnumSet.allOf(FeatureCode.class));

    /**
     * The format string for exact match queries.
     */
    private static final String EXACT_MATCH_FMT = "\"%s\"";

    /**
     * The format string for fuzzy queries.
     */
    private static final String FUZZY_FMT = "%s~";

    // Lucene index built from GeoNames gazetteer
    private final FSDirectory index;
    private final IndexSearcher indexSearcher;

    /**
     * Builds a {@link LuceneGazetteer} by loading a pre-built Lucene
     * index from disk and setting configuration parameters for
     * resolving location names to GeoName objects.
     *
     * @param indexDir              Lucene index directory to be loaded
     * @throws ClavinException      if an error occurs opening the index
     */
    public LuceneGazetteer(final File indexDir) throws ClavinException {
        try {
        // load the Lucene index directory from disk
        index = FSDirectory.open(indexDir);

        indexSearcher = new IndexSearcher(DirectoryReader.open(index));

        // override default TF/IDF score to ignore multiple appearances
        indexSearcher.setSimilarity(new BinarySimilarity());

        // run an initial throw-away query just to "prime the pump" for
        // the cache, so we can accurately measure performance speed
        // per: http://wiki.apache.org/lucene-java/ImproveSearchingSpeed
        indexSearcher.search(new AnalyzingQueryParser(Version.LUCENE_4_9, INDEX_NAME.key(),
                INDEX_ANALYZER).parse("Reston"), null, DEFAULT_MAX_RESULTS, POPULATION_SORT);
        } catch (ParseException pe) {
            throw new ClavinException("Error executing priming query.", pe);
        } catch (IOException ioe) {
            throw new ClavinException("Error opening gazetteer index.", ioe);
        }
    }

    /**
     * Execute a query against the Lucene gazetteer index using the provided configuration,
     * returning the top matches as {@link ResolvedLocation}s.
     *
     * @param query              the configuration parameters for the query
     * @return                   the list of ResolvedLocations as potential matches
     * @throws ClavinException   if an error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ResolvedLocation> getClosestLocations(final GazetteerQuery query) throws ClavinException {
        // sanitize the query input
        String sanitizedLocationName = sanitizeQueryText(query);

        // if there is no location to query, return no results
        if ("".equals(sanitizedLocationName)) {
            return Collections.EMPTY_LIST;
        }

        LocationOccurrence location = query.getOccurrence();
        int maxResults = query.getMaxResults() > 0 ? query.getMaxResults() : DEFAULT_MAX_RESULTS;
        Filter filter = buildFilter(query);
        List<ResolvedLocation> matches;
        try {
            // attempt to find an exact match for the query
            matches = executeQuery(location, sanitizedLocationName, filter, maxResults, false, query.isFilterDupes(), null);
            if (LOG.isDebugEnabled()) {
                for (ResolvedLocation loc : matches) {
                    LOG.debug("{}", loc);
                }
            }
            // check to see if we should run a fuzzy query based on the configured FuzzyMode
            if (query.getFuzzyMode().useFuzzyMatching(maxResults, matches.size())) {
                // provide any exact matches if we are running a fuzzy query so they can be considered for deduplication
                // and result count
                matches = executeQuery(location, sanitizedLocationName, filter, maxResults, true, query.isFilterDupes(), matches);
                if (LOG.isDebugEnabled()) {
                    for (ResolvedLocation loc : matches) {
                        LOG.debug("{}[fuzzy]", loc);
                    }
                }
            }
            if (matches.isEmpty()) {
                LOG.debug("No match found for: '{}'", location.getText());
            }
        } catch (ParseException pe) {
            throw new ClavinException(String.format("Error parsing query for: '%s'}", location.getText()), pe);
        } catch (IOException ioe) {
            throw new ClavinException(String.format("Error executing query for: '%s'}", location.getText()), ioe);
        }
        return matches;
    }

    /**
     * Executes a query against the Lucene index, processing the results and returning
     * at most maxResults ResolvedLocations with ancestry resolved.
     * @param location the location occurrence
     * @param sanitizedName the sanitized name of the search location
     * @param filter the filter used to restrict the search results
     * @param maxResults the maximum number of results
     * @param fuzzy is this a fuzzy query
     * @param dedupe should duplicate locations be filtered from the results
     * @param previousResults the results of a previous query that should be used for duplicate filtering and appended to until
     *                        no additional matches are found or maxResults has been reached; the input list will not be modified
     *                        and may be <code>null</code>
     * @return the ResolvedLocations with ancestry resolved matching the query
     * @throws ParseException if an error occurs generating the query
     * @throws IOException if an error occurs executing the query
     */
    private List<ResolvedLocation> executeQuery(final LocationOccurrence location, final String sanitizedName, final Filter filter,
            final int maxResults, final boolean fuzzy, final boolean dedupe, final List<ResolvedLocation> previousResults)
            throws ParseException, IOException {
        Query query = new AnalyzingQueryParser(Version.LUCENE_4_9, INDEX_NAME.key(), INDEX_ANALYZER)
                .parse(String.format(fuzzy ? FUZZY_FMT : EXACT_MATCH_FMT, sanitizedName));

        List<ResolvedLocation> matches = new ArrayList<ResolvedLocation>(maxResults);

        Map<Long, Set<GeoName>> parentMap = new HashMap<Long, Set<GeoName>>();

        // reuse GeoName instances so all ancestry is correctly resolved if multiple names for
        // the same GeoName match the query
        Map<Long, GeoName> geonameMap = new HashMap<Long, GeoName>();
        // if we are filling previous results, add them to the match list and the geoname map
        // so they can be used for deduplication or re-used if additional matches are found
        if (previousResults != null) {
            matches.addAll(previousResults);
            for (ResolvedLocation loc : previousResults) {
                geonameMap.put(loc.getGeoname().getGeonameID(), loc.getGeoname());
            }
        }

        // short circuit if we were provided enough previous results to satisfy maxResults;
        // we do this here because the query loop condition is evaluated after the query
        // is executed and results are processed to support de-duplication
        if (matches.size() >= maxResults) {
            return matches;
        }

        // track the last discovered hit so we can re-execute the query if we are
        // deduping and need to fill results
        ScoreDoc lastDoc = null;
        do {
            // collect all the hits up to maxResults, and sort them based
            // on Lucene match score and population for the associated
            // GeoNames record
            TopDocs results = indexSearcher.searchAfter(lastDoc, query, filter, maxResults, POPULATION_SORT);
            // set lastDoc to null so we don't infinite loop if results is empty
            lastDoc = null;
            // populate results if matches were discovered
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                lastDoc = scoreDoc;
                Document doc = indexSearcher.doc(scoreDoc.doc);
                // reuse GeoName instances so all ancestry is correctly resolved if multiple names for
                // the same GeoName match the query
                long geonameID = GEONAME_ID.getValue(doc);
                GeoName geoname = geonameMap.get(geonameID);
                if (geoname == null) {
                    geoname = GeoName.parseFromGeoNamesRecord((String) GEONAME.getValue(doc), (String) PREFERRED_NAME.getValue(doc));
                    geonameMap.put(geonameID, geoname);
                } else if (dedupe) {
                    // if we have already seen this GeoName and we are removing duplicates, skip to the next doc
                    continue;
                }
                String matchedName = INDEX_NAME.getValue(doc);
                if (!geoname.isAncestryResolved()) {
                    IndexableField parentIdField = doc.getField(IndexField.PARENT_ID.key());
                    Long parentId = parentIdField != null && parentIdField.numericValue() != null ?
                            parentIdField.numericValue().longValue() : null;
                    if (parentId != null) {
                        Set<GeoName> geos = parentMap.get(parentId);
                        if (geos == null) {
                            geos = new HashSet<GeoName>();
                            parentMap.put(parentId, geos);
                        }
                        geos.add(geoname);
                    }
                }
                matches.add(new ResolvedLocation(location, geoname, matchedName, fuzzy));
                // stop processing results if we have reached maxResults matches
                if (matches.size() >= maxResults) {
                    break;
                }
            }
        } while (dedupe && lastDoc != null && matches.size() < maxResults);
        // if any results need ancestry resolution, resolve parents
        if (!parentMap.isEmpty()) {
            resolveParents(parentMap);
        }

        return matches;
    }

    /**
     * Sanitizes the text of the LocationOccurrence in the query parameters for
     * use in a Lucene query, returning an empty string if no text is found.
     * @param query the query configuration
     * @return the santitized query text or the empty string if there is no query text
     */
    private String sanitizeQueryText(final GazetteerQuery query) {
        String sanitized = "";
        if (query != null && query.getOccurrence() != null) {
            String text = query.getOccurrence().getText();
            if (text != null) {
                sanitized = escape(text.trim().toLowerCase());
            }
        }
        return sanitized;
    }

    /**
     * Builds a Lucene search filter based on the provided parameters.
     * @param params the query configuration parameters
     * @return a Lucene search filter that will restrict the returned documents to the criteria provided or <code>null</code>
     *         if no filtering is necessary
     */
    private Filter buildFilter(final GazetteerQuery params) {
        List<Query> queryParts = new ArrayList<Query>();

        // create the historical locations restriction if we are not including historical locations
        if (!params.isIncludeHistorical()) {
            int val = IndexField.getBooleanIndexValue(false);
            queryParts.add(NumericRangeQuery.newIntRange(HISTORICAL.key(), val, val, true, true));
        }

        // create the parent ID restrictions if we were provided at least one parent ID
        Set<Long> parentIds = params.getParentIds();
        if (!parentIds.isEmpty()) {
            BooleanQuery parentQuery = new BooleanQuery();
            // locations must descend from at least one of the specified parents (OR)
            for (Long id : parentIds) {
                parentQuery.add(NumericRangeQuery.newLongRange(ANCESTOR_IDS.key(), id, id, true, true), Occur.SHOULD);
            }
            queryParts.add(parentQuery);
        }

        // create the feature code restrictions if we were provided some, but not all, feature codes
        Set<FeatureCode> codes = params.getFeatureCodes();
        if (!(codes.isEmpty() || ALL_CODES.equals(codes))) {
            BooleanQuery codeQuery = new BooleanQuery();
            // locations must be one of the specified feature codes (OR)
            for (FeatureCode code : codes) {
                codeQuery.add(new TermQuery(new Term(FEATURE_CODE.key(), code.name())), Occur.SHOULD);
            }
            queryParts.add(codeQuery);
        }

        Filter filter = null;
        if (!queryParts.isEmpty()) {
            BooleanQuery bq = new BooleanQuery();
            for (Query part : queryParts) {
                bq.add(part, Occur.MUST);
            }
            filter = new QueryWrapperFilter(bq);
        }
        return filter;
    }

    /**
     * Retrieves and sets the parents of the provided children.
     * @param childMap the map of parent geonameID to the set of children that belong to it
     * @throws IOException if an error occurs during parent resolution
     */
    private void resolveParents(final Map<Long, Set<GeoName>> childMap) throws IOException {
        Map<Long, GeoName> parentMap = new HashMap<Long, GeoName>();
        Map<Long, Set<GeoName>> grandParentMap = new HashMap<Long, Set<GeoName>>();
        for (Long parentId : childMap.keySet()) {
            // Lucene query used to look for exact match on the "geonameID" field
            Query q = NumericRangeQuery.newLongRange(GEONAME_ID.key(), parentId, parentId, true, true);
            TopDocs results = indexSearcher.search(q, null, 1, POPULATION_SORT);
            if (results.scoreDocs.length > 0) {
                Document doc = indexSearcher.doc(results.scoreDocs[0].doc);
                GeoName parent = GeoName.parseFromGeoNamesRecord(doc.get(GEONAME.key()), doc.get(PREFERRED_NAME.key()));
                parentMap.put(parent.getGeonameID(), parent);
                if (!parent.isAncestryResolved()) {
                    Long grandParentId = PARENT_ID.getValue(doc);
                    if (grandParentId != null) {
                        Set<GeoName> geos = grandParentMap.get(grandParentId);
                        if (geos == null) {
                            geos = new HashSet<GeoName>();
                            grandParentMap.put(grandParentId, geos);
                        }
                        geos.add(parent);
                    }
                }
            } else {
                LOG.error("Unable to find parent GeoName [{}]", parentId);
            }
        }

        // find all parents of the parents
        if (!grandParentMap.isEmpty()) {
            resolveParents(grandParentMap);
        }

        // set parents of children
        for (Long parentId : childMap.keySet()) {
            GeoName parent = parentMap.get(parentId);
            if (parent == null) {
                LOG.info("Unable to find parent with ID [{}]", parentId);
                continue;
            }
            for (GeoName child : childMap.get(parentId)) {
                child.setParent(parent);
            }
        }
    }

    /**
     * Retrieves the GeoName with the provided ID.
     * @param geonameId          the ID of the requested GeoName
     * @return                   the requested GeoName or <code>null</code> if not found
     * @throws ClavinException   if an error occurs
     */
    @Override
    public GeoName getGeoName(final long geonameId) throws ClavinException {
        try {
            GeoName geoName = null;
            // Lucene query used to look for exact match on the "geonameID" field
            Query q = NumericRangeQuery.newLongRange(GEONAME_ID.key(), geonameId, geonameId, true, true);
            // retrieve only one matching document
            TopDocs results = indexSearcher.search(q, 1);
            if (results.scoreDocs.length > 0) {
                Document doc = indexSearcher.doc(results.scoreDocs[0].doc);
                geoName = GeoName.parseFromGeoNamesRecord(doc.get(GEONAME.key()), doc.get(PREFERRED_NAME.key()));
                if (!geoName.isAncestryResolved()) {
                    Long parentId = PARENT_ID.getValue(doc);
                    if (parentId != null) {
                        Map<Long, Set<GeoName>> childMap = new HashMap<Long, Set<GeoName>>();
                        childMap.put(parentId, Collections.singleton(geoName));
                        resolveParents(childMap);
                    }
                }
            } else {
                LOG.debug("No geoname found for ID: {}", geonameId);
            }
            return geoName;
        } catch (IOException e) {
            String msg = String.format("Error retrieving geoname with ID : %d", geonameId);
            LOG.error(msg, e);
            throw new ClavinException(msg, e);
        }
    }

    private static class QueryPart {
        public final Query query;
        public final Occur occur;

        public QueryPart(Query query, Occur occur) {
            this.query = query;
            this.occur = occur;
        }
    }
}
