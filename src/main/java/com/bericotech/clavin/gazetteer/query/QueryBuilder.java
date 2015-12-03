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
 * QueryBuilder.java
 *
 *###################################################################*/

package com.bericotech.clavin.gazetteer.query;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.FeatureCode;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * A Builder class to assist in creating GazetteerQuery configurations.
 * QueryBuilders can be re-used to create varied queries but are not
 * thread-safe.
 */
public class QueryBuilder {
    private static final int DEFAULT_MAX_RESULTS = 10;
    private static final FuzzyMode DEFAULT_FUZZY_MODE = FuzzyMode.OFF;
    private static final boolean DEFAULT_INCLUDE_HISTORICAL = true;
    private static final boolean DEFAULT_FILTER_DUPES = false;

    private static final Set<FeatureCode> COUNTRY_CODES = Collections.unmodifiableSet(EnumSet.of(
            FeatureCode.PCL,
            FeatureCode.PCLD,
            FeatureCode.PCLF,
            FeatureCode.PCLH,
            FeatureCode.PCLI,
            FeatureCode.PCLIX,
            FeatureCode.PCLS,
            FeatureCode.TERRI
    ));

    private static final Set<FeatureCode> ANCESTRY_ADMIN_CODES = Collections.unmodifiableSet(EnumSet.of(
            FeatureCode.ADM1,
            FeatureCode.ADM1H,
            FeatureCode.ADM2,
            FeatureCode.ADM2H,
            FeatureCode.ADM3,
            FeatureCode.ADM3H,
            FeatureCode.ADM4,
            FeatureCode.ADM4H
    ));

    private static final Set<FeatureCode> ADMIN_CODES = Collections.unmodifiableSet(EnumSet.of(
            FeatureCode.ADM1,
            FeatureCode.ADM1H,
            FeatureCode.ADM2,
            FeatureCode.ADM2H,
            FeatureCode.ADM3,
            FeatureCode.ADM3H,
            FeatureCode.ADM4,
            FeatureCode.ADM4H,
            FeatureCode.ADM5,
            FeatureCode.ADMD,
            FeatureCode.ADMDH,
            FeatureCode.TERR,
            FeatureCode.PRSH
    ));

    private static final Set<FeatureCode> CITY_CODES = Collections.unmodifiableSet(EnumSet.of(
            FeatureCode.PPL,
            FeatureCode.PPLA,
            FeatureCode.PPLA2,
            FeatureCode.PPLA3,
            FeatureCode.PPLA4,
            FeatureCode.PPLC,
            FeatureCode.PPLCH,
            FeatureCode.PPLF,
            FeatureCode.PPLG,
            FeatureCode.PPLH,
            FeatureCode.PPLL,
            FeatureCode.PPLQ,
            FeatureCode.PPLR,
            FeatureCode.PPLS,
            FeatureCode.PPLW,
            FeatureCode.PPLX,
            FeatureCode.STLMT
    ));

    private LocationOccurrence location;
    private int maxResults = DEFAULT_MAX_RESULTS;
    private FuzzyMode fuzzyMode = DEFAULT_FUZZY_MODE;
    private boolean includeHistorical = DEFAULT_INCLUDE_HISTORICAL;
    private boolean filterDupes = DEFAULT_FILTER_DUPES;
    private Set<Long> parentIds = new HashSet<Long>();
    private Set<FeatureCode> featureCodes = EnumSet.noneOf(FeatureCode.class);

    /**
     * Constructs a query from the current configuration of this Builder.
     * @return a {@link GazetteerQuery} configuration object
     */
    public GazetteerQuery build() {
        return new GazetteerQuery(location, maxResults, fuzzyMode, includeHistorical, filterDupes, parentIds, featureCodes);
    }

    /**
     * Get the currently configured location to query for.
     * @return the location to query for
     */
    public LocationOccurrence location() {
        return location;
    }

    /**
     * Set the location to query for.
     * @param loc the location to query for.
     * @return this
     */
    public QueryBuilder location(final LocationOccurrence loc) {
        location = loc;
        return this;
    }

    /**
     * Convenience method to create a LocationOccurrence at position 0 for
     * the given location name.
     * @param locName the name of the location to query for
     * @return this
     */
    public QueryBuilder location(final String locName) {
        location = new LocationOccurrence(locName, 0);
        return this;
    }

    /**
     * Get the current maximum number of results.
     * @return the maximum number of results
     */
    public int maxResults() {
        return maxResults;
    }

    /**
     * Set the maximum number of search results.
     * @param max the maximum number of results
     * @return this
     */
    public QueryBuilder maxResults(final int max) {
        maxResults = max;
        return this;
    }

    /**
     * Get the current fuzzy matching mode.
     * @return the fuzzy matching mode
     */
    public FuzzyMode fuzzyMode() {
        return fuzzyMode;
    }

    /**
     * Configure the fuzzy matching mode.
     * @param mode the fuzzy matching mode
     * @return this
     */
    public QueryBuilder fuzzyMode(final FuzzyMode mode) {
        fuzzyMode = mode;
        return this;
    }

    /**
     * Does this builder include historical locations?
     * @return <code>true</code> if this builder is configured to return historical locations
     */
    public boolean includeHistorical() {
        return includeHistorical;
    }

    /**
     * Indicate whether or not this builder includes historical locations in the search results.
     * @param incHist <code>true</code> to include historical locations in the results
     * @return this
     */
    public QueryBuilder includeHistorical(final boolean incHist) {
        includeHistorical = incHist;
        return this;
    }

    /**
     * Does this builder filter duplicate results?
     * @return <code>true</code> if this builder is configured to filter duplicates
     */
    public boolean filterDupes() {
        return filterDupes;
    }

    /**
     * Indicate whether or not this builder should filter duplicate results.
     * @param filter <code>true</code> to filter duplicate results
     * @return this
     */
    public QueryBuilder filterDupes(final boolean filter) {
        filterDupes = filter;
        return this;
    }

    /**
     * Get the set of parent IDs that will be used to constrain the query results.  This set
     * may not be modifiable and modifications, if allowed, will not affect the generated
     * queries.
     * @return the current set of parent IDs used to constrain the query results
     */
    public Set<Long> parentIds() {
        return Collections.unmodifiableSet(parentIds);
    }

    /**
     * Set the parent IDs that will be used to constraint the query results; replacing any
     * previously configured IDs. Use the addParentIds() and removeParentIds() methods to
     * modify the existing configuration.
     * @param ids the new set of parent IDs used to constrain the query results
     * @return this
     */
    public QueryBuilder parentIds(final Set<Long> ids) {
        parentIds = new HashSet<Long>();
        if (ids != null) {
            parentIds.addAll(ids);
        }
        return this;
    }

    /**
     * Add the provided parent ID to the set of query constraints.
     * @param id the parent ID to add
     * @return this
     */
    public QueryBuilder addParentIds(final Long id) {
        parentIds.add(id);
        return this;
    }

    /**
     * Add the provided parent IDs to the set of query constraints.
     * @param id1 the first parent ID to add
     * @param ids the subsequent parent IDs to add
     * @return this
     */
    public QueryBuilder addParentIds(final Long id1, final Long... ids) {
        parentIds.add(id1);
        parentIds.addAll(Arrays.asList(ids));
        return this;
    }

    /**
     * Add the provided parent IDs to the set of query constraints.
     * @param ids the parent IDs to add
     * @return this
     */
    public QueryBuilder addParentIds(final Collection<Long> ids) {
        if (ids != null) {
            parentIds.addAll(ids);
        }
        return this;
    }

    /**
     * Remove the provided parent ID from the set of query constraints.
     * @param id the parent ID to remove
     * @return this
     */
    public QueryBuilder removeParentIds(final Integer id) {
        parentIds.remove(id);
        return this;
    }

    /**
     * Remove the provided parent IDs from the set of query constraints.
     * @param id1 the first parent ID to remove
     * @param ids the subsequent parent IDs to remove
     * @return this
     */
    public QueryBuilder removeParentIds(final Integer id1, final Integer... ids) {
        parentIds.remove(id1);
        parentIds.removeAll(Arrays.asList(ids));
        return this;
    }

    /**
     * Remove the provided parent IDs from the set of query constraints.
     * @param ids the parent IDs to remove
     * @return this
     */
    public QueryBuilder removeParentIds(final Collection<Integer> ids) {
        if (ids != null) {
            parentIds.removeAll(ids);
        }
        return this;
    }

    /**
     * Convenience method to remove all current parent ID restrictions.
     * @return this
     */
    public QueryBuilder clearParentIds() {
        parentIds = new HashSet<Long>();
        return this;
    }

    /**
     * Get the set of {@link FeatureCode}s that will be used to constrain the query results.  This set
     * may not be modifiable and modifications, if allowed, will not affect the generated
     * queries.
     * @return the current set of {@link FeatureCode}s used to constrain the query results
     */
    public Set<FeatureCode> featureCodes() {
        return Collections.unmodifiableSet(featureCodes);
    }

    /**
     * Set the {@link FeatureCode}s that will be used to constraint the query results; replacing any
     * previously configured IDs. Use the addFeatureCodes() and removeFeatureCodes() methods to
     * modify the existing configuration.
     * @param codes the new set of {@link FeatureCode}s used to constrain the query results
     * @return this
     */
    public QueryBuilder featureCodes(final Set<FeatureCode> codes) {
        featureCodes = EnumSet.noneOf(FeatureCode.class);
        if (codes != null) {
            featureCodes.addAll(codes);
        }
        return this;
    }

    /**
     * Add the provided {@link FeatureCode} to the set of query constraints.
     * @param code the {@link FeatureCode} to add
     * @return this
     */
    public QueryBuilder addFeatureCodes(final FeatureCode code) {
        featureCodes.add(code);
        return this;
    }

    /**
     * Add the provided {@link FeatureCode}s to the set of query constraints.
     * @param code1 the first {@link FeatureCode} to add
     * @param codes the subsequent {@link FeatureCode}s to add
     * @return this
     */
    public QueryBuilder addFeatureCodes(final FeatureCode code1, final FeatureCode... codes) {
        featureCodes.add(code1);
        featureCodes.addAll(Arrays.asList(codes));
        return this;
    }

    /**
     * Add the provided {@link FeatureCode}s to the set of query constraints.
     * @param codes the {@link FeatureCode}s to add
     * @return this
     */
    public QueryBuilder addFeatureCodes(final Collection<FeatureCode> codes) {
        if (codes != null) {
            featureCodes.addAll(codes);
        }
        return this;
    }

    /**
     * Remove the provided {@link FeatureCode} from the set of query constraints.
     * @param code the {@link FeatureCode} to remove
     * @return this
     */
    public QueryBuilder removeFeatureCodes(final FeatureCode code) {
        featureCodes.remove(code);
        return this;
    }

    /**
     * Remove the provided {@link FeatureCode} from the set of query constraints.
     * @param code1 the first {@link FeatureCode} to remove
     * @param codes the subsequent {@link FeatureCode}s to remove
     * @return this
     */
    public QueryBuilder removeFeatureCodes(final FeatureCode code1, final FeatureCode... codes) {
        featureCodes.remove(code1);
        featureCodes.removeAll(Arrays.asList(codes));
        return this;
    }

    /**
     * Remove the provided {@link FeatureCode}s from the set of query constraints.
     * @param codes the {@link FeatureCode}s to remove
     * @return this
     */
    public QueryBuilder removeFeatureCodes(final Collection<FeatureCode> codes) {
        if (codes != null) {
            featureCodes.removeAll(codes);
        }
        return this;
    }

    /**
     * Convenience method to clear any existing {@link FeatureCode} restrictions.
     * @return this
     */
    public QueryBuilder clearFeatureCodes() {
        featureCodes = EnumSet.noneOf(FeatureCode.class);
        return this;
    }

    /**
     * Convenience method to add the {@link FeatureCode}s representing countries
     * and similar top-level political entities to the restriction list.  This
     * method modifies the existing set of codes.
     * @return this
     */
    public QueryBuilder addCountryCodes() {
        return addFeatureCodes(COUNTRY_CODES);
    }

    /**
     * Convenience method to remove the {@link FeatureCode}s representing countries
     * and similar top-level political entities from the restriction list.  This
     * method modifies the existing set of codes.
     * @return this
     */
    public QueryBuilder removeCountryCodes() {
        return removeFeatureCodes(COUNTRY_CODES);
    }

    /**
     * Convenience method to add the {@link FeatureCode}s representing administrative
     * divisions to the restriction list. This includes all ADM* codes, territories (TERR)
     * and parishes (PRSH). This method modifies the existing set of codes.
     * @return this
     */
    public QueryBuilder addAdminCodes() {
        return addFeatureCodes(ADMIN_CODES);
    }

    /**
     * Convenience method to remove the {@link FeatureCode}s representing administrative
     * divisions from the restriction list. This includes all ADM* codes, territories (TERR)
     * and parishes (PRSH). This method modifies the existing set of codes.
     * @return this
     */
    public QueryBuilder removeAdminCodes() {
        return removeFeatureCodes(ADMIN_CODES);
    }

    /**
     * Convenience method to add the {@link FeatureCode}s representing administrative
     * divisions that can be part of the ancestry tree to the restriction list.  This
     * only includes administrative divisions 1-4 (ADM[1-4]). This method modifies the
     * existing set of codes.
     * @return this
     */
    public QueryBuilder addAncestryAdminCodes() {
        return addFeatureCodes(ANCESTRY_ADMIN_CODES);
    }

    /**
     * Convenience method to remove the {@link FeatureCode}s representing administrative
     * divisions that can be part of the ancestry tree from the restriction list.  This
     * only includes administrative divisions 1-4 (ADM[1-4]). This method modifies the
     * existing set of codes.
     * @return this
     */
    public QueryBuilder removeAncestryAdminCodes() {
        return removeFeatureCodes(ANCESTRY_ADMIN_CODES);
    }

    /**
     * Convenience method to add the {@link FeatureCode}s representing cities and other
     * populated places to the restriction list. This method modifies the existing set
     * of codes.
     * @return this
     */
    public QueryBuilder addCityCodes() {
        return addFeatureCodes(CITY_CODES);
    }

    /**
     * Convenience method to remove the {@link FeatureCode}s representing cities and other
     * populated places from the restriction list. This method modifies the existing set
     * of codes.
     * @return this
     */
    public QueryBuilder removeCityCodes() {
        return removeFeatureCodes(CITY_CODES);
    }

    @Override
    public String toString() {
        return String.format("loc: %s, maxResults: %s, fuzzyMode: %s, historical? %s, filterDupes? %s, parents: %s, codes: %s",
                location, maxResults, fuzzyMode, includeHistorical, filterDupes, parentIds, featureCodes);
    }
}
