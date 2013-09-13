package com.bericotech.clavin.resolver;

import java.util.List;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.GeoName;

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
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * ====================================================================
 * 
 * LocationResolver.java
 * 
 *###################################################################*/

/**
 * Resolves location names into GeoName objects.
 * 
 * Takes location names extracted from unstructured text documents by
 * {@link LocationExtractor} and resolves them into the appropriate
 * geographic entities (as intended by the document's author based on
 * context) by finding the best match in a gazetteer.
 * 
 */
public interface LocationResolver {
	
	/**
     * Resolves the supplied list of location names into
     * {@link ResolvedLocation}s containing {@link GeoName} objects.
     * 
     * @param locations 		list of location names to be resolved
     * @param fuzzy			switch for turning on/off fuzzy matching
     * @return 				list of {@link ResolvedLocation} objects
     * @throws Exception 
     **/
	public List<ResolvedLocation> resolveLocations(
			List<LocationOccurrence> locations, boolean fuzzy) throws Exception;
	
}
