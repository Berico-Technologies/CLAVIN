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
 * SearchResult.java
 *
 *###################################################################*/

package com.bericotech.clavin.resolver.multipart;

import com.bericotech.clavin.resolver.ResolvedLocation;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class SearchResult {
    public final SearchLevel level;
    public final List<ResolvedLocation> locations;
    public final Set<Long> parentIds;
    public final Set<String> parentCodes;

    public SearchResult(SearchLevel level, List<ResolvedLocation> locations, Set<Long> parentIds, Set<String> parentCodes) {
        this.level = level;
        this.locations = locations;
        this.parentIds = parentIds;
        this.parentCodes = parentCodes;
    }

    public ResolvedLocation getBestLocation() {
        return locations.isEmpty() ? null : locations.get(0);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", level, getBestLocation());
    }

}
