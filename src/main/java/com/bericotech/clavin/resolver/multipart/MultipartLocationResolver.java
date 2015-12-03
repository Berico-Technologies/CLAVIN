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
 * MultipartLocationResolver.java
 *
 *###################################################################*/

package com.bericotech.clavin.resolver.multipart;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.query.FuzzyMode;
import com.bericotech.clavin.gazetteer.query.Gazetteer;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.gazetteer.query.QueryBuilder;
import com.bericotech.clavin.resolver.ResolvedLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves multipart location names from structured data into GeoName objects.
 *
 * Takes multipart location names, such as what's often found in structured data
 * like a spreadsheet or database table (e.g., [Reston][Virginia][United States]),
 * and resolves them into the appropriate geographic entities by identifying the
 * most logical match in a gazetteer, trying to enforce some kind of notional
 * hierarchy of place names (e.g., city --> state/province/etc. --> country).
 */
public class MultipartLocationResolver {
    /**
     * The logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger(MultipartLocationResolver.class);

    /**
     * The hit depth used during searches.
     */
    private static final int MAX_RESULTS = 200;

    /**
     * The gazetteer for searches.
     */
    private final Gazetteer gazetteer;

    /**
     * The scorer for multi-value searches.
     */
    private final Scorer scorer;

    public MultipartLocationResolver(final Gazetteer gaz) {
        this.gazetteer = gaz;
        scorer = new DefaultScorer();
    }

    /**
     * Resolves a multipart location name, such as what's often found
     * in structured data like a spreadsheet or database table (e.g.,
     * [Reston][Virginia][United States]), into a {@link ResolvedMultipartLocation}
     * containing {@link com.bericotech.clavin.gazetteer.GeoName} objects.
     *
     * @param location           multipart location name to be resolved
     * @param fuzzy              switch for turning on/off fuzzy matching
     * @return                   resolved multipart location name
     * @throws ClavinException   if an error occurs while resolving locations
     */
    public ResolvedMultipartLocation resolveMultipartLocation(MultipartLocationName location, boolean fuzzy)
            throws ClavinException {
        // find all component locations in the gazetteer
        QueryBuilder queryBuilder = new QueryBuilder()
                // translate CLAVIN 1.x 'fuzzy' parameter into NO_EXACT or OFF; it isn't
                // necessary, or desirable to support FILL for the multi-part resolution algorithm
                .fuzzyMode(fuzzy ? FuzzyMode.NO_EXACT : FuzzyMode.OFF)
                .includeHistorical(true)
                .maxResults(MAX_RESULTS);

        // country query should only include country-like feature codes
        queryBuilder.location(location.getCountry()).addCountryCodes();
        List<ResolvedLocation> countries = gazetteer.getClosestLocations(queryBuilder.build());
        // remove all "countries" that are not considered top-level administrative divisions; this
        // filters out territories that do not contain descendant GeoNames
        Iterator<ResolvedLocation> iter = countries.iterator();
        while (iter.hasNext()) {
            if (!iter.next().getGeoname().isTopLevelAdminDivision()) {
                iter.remove();
            }
        }

        Set<CountryCode> foundCountries = EnumSet.noneOf(CountryCode.class);
        // state query should only include admin-level feature codes with ancestors
        // in the list of located countries
        queryBuilder.location(location.getState()).clearFeatureCodes().addAdminCodes();
        for (ResolvedLocation country : countries) {
            queryBuilder.addParentIds(country.getGeoname().getGeonameID());
            foundCountries.add(country.getGeoname().getPrimaryCountryCode());
        }
        List<ResolvedLocation> states = gazetteer.getClosestLocations(queryBuilder.build());

        // city query should only include city-level feature codes; ancestry is restricted
        // to the discovered states or, if no states were found, the discovered countries or,
        // if neither states nor countries were found, no ancestry restrictions are added and
        // the most populated city will be selected
        queryBuilder.location(location.getCity()).clearFeatureCodes().addCityCodes();
        if (!states.isEmpty()) {
            Set<CountryCode> stateCodes = EnumSet.noneOf(CountryCode.class);
            // only clear the parent ID restrictions if states were found; otherwise
            // we will continue our search based on the existing country restrictions, if any
            queryBuilder.clearParentIds();
            for (ResolvedLocation state : states) {
                // only include the first administrative division found for each target
                // country
                if (!stateCodes.contains(state.getGeoname().getPrimaryCountryCode())) {
                    queryBuilder.addParentIds(state.getGeoname().getGeonameID());
                    stateCodes.add(state.getGeoname().getPrimaryCountryCode());
                }
                // since we are only including one "state" per country, short-circuit
                // the loop if we have added one for each unique country code returned
                // by the countries search
                if (!foundCountries.isEmpty() && foundCountries.equals(stateCodes)) {
                    break;
                }
            }
        }
        List<ResolvedLocation> cities = gazetteer.getClosestLocations(queryBuilder.build());

        // initialize return objects components
        ResolvedLocation finalCity = null;
        ResolvedLocation finalState = null;
        ResolvedLocation finalCountry = null;

        // assume the most populous valid city is the correct one return
        // note: this should be a reasonably safe assumption since we've attempted to enforce the
        // notional hierarchy of given place names (e.g., city --> state/province/etc. --> country)
        // and have therefore weeded out all other matches that don't fit this hierarchy
        if (!cities.isEmpty()) {
            finalCity = cities.get(0);
        }

        if (!states.isEmpty()) {
            // if we couldn't find a valid city, just take the most populous valid state/province/etc.
            if (finalCity == null) {
                finalState = states.get(0);
            } else {
                for (ResolvedLocation state : states) {
                    // select the first state that is an ancestor of the selected city
                    if (finalCity.getGeoname().isDescendantOf(state.getGeoname())) {
                        finalState = state;
                        break;
                    }
                }
            }
        }

        if (!countries.isEmpty()) {
            // use the selected city if available and the selected state if not to identify the selected country
            ResolvedLocation best = finalCity != null ? finalCity : finalState;
            // if neither city nor state was resolved, take the most populous valid country
            if (best == null) {
                finalCountry = countries.get(0);
            } else {
                for (ResolvedLocation country : countries) {
                    // select the first country that is an ancestor of the selected city or state
                    if (best.getGeoname().isDescendantOf(country.getGeoname())) {
                        finalCountry = country;
                        break;
                    }
                }
            }
        }

        return new ResolvedMultipartLocation(finalCity, finalState, finalCountry);
    }

    /**
     * Attempts to resolve a location provided as a comma-separated string of political divisions from
     * narrowest to broadest. The gazetteer current supports ancestry from the country level through four
     * administrative divisions so any more-specific divisions will be ignored once a city (lowest available
     * level of resolution) is found. Results will only be returned if all unignored location components are
     * matched.
     * @param loc the comma-separated location name (e.g. "City, County, State, Country")
     * @param fuzzy <code>true</code> to use fuzzy matching if an exact match for any location could not be found
     * @return the resolved location
     * @throws ClavinException if an error occurs while searching
     */
    public ResolvedLocation resolveLocation(final String loc, final boolean fuzzy) throws ClavinException {
        return resolveLocation(fuzzy, loc.split(","));
    }

    /**
     * Resolves a location provided as a series of political divisions from narrowest to broadest. The gazetteer
     * current supports ancestry from the country level through four administrative divisions so any more-specific
     * divisions will be ignored once a city (lowest available level of resolution) is found. Results will only
     * be returned if all unignored location components are matched.
     * @param fuzzy <code>true</code> to use fuzzy matching if an exact match for any location could not be found
     * @param locationParts the names of the locations to match, ordered from most to least specific
     *                      (e.g. [ "City", "County", "State", "Country" ])
     * @return the resolved location
     * @throws ClavinException if an error occurs while searching
     */
    @SuppressWarnings("unchecked")
    public ResolvedLocation resolveLocation(final boolean fuzzy, final String... locationParts)
            throws ClavinException {
        final List<String> terms = new ArrayList<String>(locationParts.length+1);
        // terms will be a list of broadest to narrowest; e.g. United States, Virginia, Fairfax County, Reston
        for (String part : locationParts) {
            if (part != null && !part.trim().equals("")) {
                terms.add(0, part);
            }
        }
        // short circuit if no input was provided
        if (terms.isEmpty()) {
            return null;
        }

        Set<MatchedLocation> candidates = new HashSet<MatchedLocation>();
        Deque<SearchResult> matches = new LinkedList<SearchResult>();
        QueryBuilder query = new QueryBuilder()
                .maxResults(MAX_RESULTS)
                // translate CLAVIN 1.x 'fuzzy' parameter into NO_EXACT or OFF; it isn't
                // necessary, or desirable to support FILL for the multi-part resolution algorithm
                .fuzzyMode(fuzzy ? FuzzyMode.NO_EXACT : FuzzyMode.OFF)
                .includeHistorical(true);
        findCandidates(candidates, terms, SearchLevel.COUNTRY, matches, query);

        // Using post-processing sort instead of SortedSet implementation (TreeSet) because
        // TreeSet uses compareTo instead of equals/hashCode to eliminate duplicates and
        // incorrectly excludes elements that evaluate to the same sort score
        List<MatchedLocation> candidateList = new ArrayList<MatchedLocation>(candidates);
        Collections.sort(candidateList, new Comparator<MatchedLocation>() {
            @Override
            public int compare(final MatchedLocation loc1, final MatchedLocation loc2) {
                double score1 = scorer.score(terms, loc1);
                double score2 = scorer.score(terms, loc2);
                // sort candidates in descending order by score
                return Double.compare(score2, score1);
            }
        });
        if (LOG.isDebugEnabled()) {
            LOG.debug("Found {} candidates", candidateList.size());
            for (MatchedLocation candidate : candidateList) {
                LOG.debug(String.format("[%.3f] %s", scorer.score(terms, candidate), candidate.toString()));
            }
        }
        MatchedLocation bestMatch = candidateList.isEmpty() ? null : candidateList.get(0);
        ResolvedLocation location = null;
        if (bestMatch != null && (bestMatch.isFullySpecified() || bestMatch.getMatchCount() == terms.size())) {
            location = bestMatch.getMostSpecificMatch().getLocation();
        }
        return location;
    }

    @SuppressWarnings("unchecked")
    private void findCandidates(final Set<MatchedLocation> candidates, final List<String> terms, final SearchLevel level,
            final Deque<SearchResult> matches, final QueryBuilder query) throws ClavinException {
        // if there are no more terms or level is null, add a candidate to the list
        // if there are any prior matches
        if (terms.isEmpty() || level == null) {
            if (!matches.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Adding candidate for matches:");
                    for (SearchResult res : matches) {
                        LOG.debug(res.toString());
                    }
                }
                candidates.add(new MatchedLocation(matches));
            }
            return;
        }

        String term = terms.get(0);
        List<String> nextTerms = terms.size() > 1 ? terms.subList(1, terms.size()) : Collections.EMPTY_LIST;
        SearchResult lastMatch = matches.peek();
        level.apply(query).location(term).clearParentIds();
        if (lastMatch != null) {
            query.parentIds(lastMatch.parentIds);
        }
        List<ResolvedLocation> results = gazetteer.getClosestLocations(query.build());
        // no results for this term at this level; search for this term at the
        // next level, then search for subsequent terms at this level
        if (results.isEmpty()) {
            findCandidates(candidates, terms, level.narrow(), matches, query);
            findCandidates(candidates, nextTerms, level, matches, query);
        } else {
            // we found results, process them to configure the filters for the next
            // level of the search and add them to the matches stack
            Set<Long> parentIds = new HashSet<Long>();
            Set<String> parentCodes = new HashSet<String>();
            Set<String> foundParents = new HashSet<String>();
            // only include the first (best) result for each distinct parent in the filter set
            for (ResolvedLocation loc : results) {
                GeoName geo = loc.getGeoname();
                String pCode = lastMatch != null ? lastMatch.level.getCode(geo) : null;
                // if there were no parent filters or we have not found a child for this parent
                // code, add this location to the filter set
                if (lastMatch == null || !foundParents.contains(pCode)) {
                    parentIds.add(geo.getGeonameID());
                    parentCodes.add(level.getCode(geo));
                    foundParents.add(pCode);
                }
                // if there was a previous filter set, short-circuit once we have
                // a child from each parent
                if (lastMatch != null && foundParents.equals(lastMatch.parentCodes)) {
                    break;
                }
            }
            matches.push(new SearchResult(level, results, parentIds, parentCodes));
            // continue search for additional terms after adding these results to the
            // match stack
            findCandidates(candidates, nextTerms, level.narrow(), matches, query);
            // pop this match off the stack, then search for this term at the next level
            matches.pop();
            findCandidates(candidates, terms, level.narrow(), matches, query);
        }
    }
}
