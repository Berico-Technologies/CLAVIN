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
 * GazetteerQuery.java
 *
 *###################################################################*/

package com.bericotech.clavin.gazetteer.query;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.FeatureCode;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration parameters for querying a Gazetteer.  The
 * {@link QueryBuilder} contains convenience methods to make
 * query construction more readable.
 */
public class GazetteerQuery {
    /**
     * The location occurrence to search for.
     */
    private final LocationOccurrence occurrence;

    /**
     * The maximum number of results.
     */
    private final int maxResults;

    /**
     * Indicates how fuzzy matching should be applied.
     */
    private final FuzzyMode fuzzyMode;

    /**
     * Should historical locations be included?
     */
    private final boolean includeHistorical;

    /**
     * When <code>true</code>, queries that match multiple names for the
     * same location (e.g. "Virginia" and "Commonwealth of Virginia") will
     * only return the best match for that location.
     */
    private final boolean filterDupes;

    /**
     * Searches should be restricted to locations found in at least
     * one of this set of parents.
     */
    private final Set<Long> parentIds;

    /**
     * Searches should be restricted to locations matching one of
     * these feature codes.
     */
    private final Set<FeatureCode> featureCodes;

    /**
     * Create a new GazetteerQuery.
     * @param occurrence the location occurrence
     * @param maxResults the maximum number of results
     * @param fuzzyMode the fuzzy mode for this query
     * @param includeHistorical <code>true</code> to include historical locations
     * @param filterDupes <code>true</code> to return only the highest scoring match for each individual location
     * @param parentIds the set of parent IDs to restrict the search to; these will be OR'ed
     * @param featureCodes the set of feature codes to restrict the search to; these will be OR'ed
     */
    @SuppressWarnings("unchecked")
    public GazetteerQuery(final LocationOccurrence occurrence, final int maxResults, final FuzzyMode fuzzyMode,
            final boolean includeHistorical, final boolean filterDupes, final Set<Long> parentIds,
            final Set<FeatureCode> featureCodes) {
        this.occurrence = occurrence;
        this.maxResults = maxResults;
        this.fuzzyMode = fuzzyMode;
        this.includeHistorical = includeHistorical;
        this.filterDupes = filterDupes;
        this.parentIds = parentIds != null ? new HashSet<Long>(parentIds) : Collections.EMPTY_SET;
        this.featureCodes = featureCodes != null ? EnumSet.copyOf(featureCodes) : EnumSet.noneOf(FeatureCode.class);
    }

    /**
     * Get the LocationOccurrence to search on.
     * @return the LocationOccurrence to search for
     */
    public LocationOccurrence getOccurrence() {
        return occurrence;
    }

    /**
     * Get the maximum number of results that should be returned by this query.
     * @return the maximum number of search results
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * Indicates how fuzzy matching should be used.
     * @return the fuzzy matching mode
     */
    public FuzzyMode getFuzzyMode() {
        return fuzzyMode;
    }

    /**
     * Should historical locations be included in the search results?
     * @return <code>true</code> if historical locations should be included
     */
    public boolean isIncludeHistorical() {
        return includeHistorical;
    }

    /**
     * Should duplicate results be filtered so only the best match for each
     * location is returned?
     * @return <code>true</code> if duplicates should be filtered
     */
    public boolean isFilterDupes() {
        return filterDupes;
    }

    /**
     * Get the set of parents that should be used to restrict the search.
     * @return the IDs of the parents that should be used to restrict the search
     */
    public Set<Long> getParentIds() {
        return Collections.unmodifiableSet(parentIds);
    }

    /**
     * Get the set of FeatureCodes that should be used to restrict the search.
     * @return the set of FeatureCodes that should be used to restrict the search
     */
    public Set<FeatureCode> getFeatureCodes() {
        return Collections.unmodifiableSet(featureCodes);
    }
}
