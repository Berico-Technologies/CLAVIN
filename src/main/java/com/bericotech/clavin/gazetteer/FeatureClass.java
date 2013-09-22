package com.bericotech.clavin.gazetteer;

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
 * FeatureClass.java
 * 
 *###################################################################*/

/**
 * Feature categories used by GeoNames.
 * (see http://www.geonames.org/export/codes.html)
 *
 */
public enum FeatureClass {
    A("Administrative region", "country, state, region, etc."),
    P("Populated place", "city, village, etc."),
    V("Vegetation feature", "forest, heath, etc."),
    L("Locality or area", "park, area, etc."),
    U("Undersea feature", "reef, trench, etc."),
    R("Streets, highways, roads, or railroad", "road, railroad, etc."),
    T("Hypsographic feature", "mountain, hill, rock, etc."),
    H("Hydrographic feature", "stream, lake, etc."),
    S("Spot feature", "spot, building, farm, etc."),
    
    // manually added for feature codes & locations not assigned to a
    // feature class
    NULL("not available", "");
    
    // name of feature class
    public final String type;
    
    // description of feature class
    public final String description;

    /**
     * Constructor for {@link FeatureClass} enum type.
     * 
     * @param type          name of class
     * @param description   description of class
     */
    private FeatureClass(String type, String description) {
        this.type = type;
        this.description = description;
    }
}
