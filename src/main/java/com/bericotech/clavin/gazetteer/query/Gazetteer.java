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
 * Gazetteer.java
 *
 *###################################################################*/

package com.bericotech.clavin.gazetteer.query;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.resolver.ResolvedLocation;
import java.util.List;

/**
 * A Gazetteer provides lookup methods for retrieving details about known
 * places found in the indexed Gazetteer data.
 */
public interface Gazetteer {
    /**
     * Execute a query against the gazetteer using the provided configuration,
     * returning the top matches as {@link ResolvedLocation}s.
     *
     * @param query              the configuration parameters for the query
     * @return                   the list of ResolvedLocations as potential matches
     * @throws ClavinException   if an error occurs
     */
    List<ResolvedLocation> getClosestLocations(final GazetteerQuery query) throws ClavinException;

    /**
     * Retrieves the GeoName with the provided ID.
     * @param geonameId           the ID of the requested GeoName
     * @return                    the requested GeoName or <code>null</code> if not found
     * @throws ClavinException    if an error occurs
     */
    GeoName getGeoName(final long geonameID) throws ClavinException;
}
