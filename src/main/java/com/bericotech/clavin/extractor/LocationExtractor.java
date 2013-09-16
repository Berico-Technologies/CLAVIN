package com.bericotech.clavin.extractor;

import java.util.List;

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
 * LocationExtractor.java
 * 
 *###################################################################*/

/**
 * Simple interface for location name extraction capabilities to be
 * provided by third-party named entity recognition tools.
 *
 */
public interface LocationExtractor {

	/**
	 * Extracts a list of location names found in unstructured text.
	 * 
	 * @param plainText		source of location names to be extracted
	 * @return 				list of location occurrences
	 */
	public List<LocationOccurrence> extractLocationNames(String plainText);
}
