package com.bericotech.clavin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.extractor.LocationExtractor;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.resolver.LocationResolver;
import com.bericotech.clavin.resolver.ResolvedLocation;

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
 * GeoParser.java
 * 
 *###################################################################*/

/**
 * Performs geoparsing of documents; extracts location names from
 * unstructured text and resolves them against a gazetteer to produce
 * structured geo data.
 * 
 * Main API entry point for CLAVIN -- simply instantiate this class and
 * call the {@link GeoParser#parse} method on your text string.
 *
 */
public class GeoParser {
	
	private static final Logger logger = LoggerFactory.getLogger(GeoParser.class);

	// entity extractor to find location names in text
	private LocationExtractor extractor;
	
	// resolver to match location names against gazetteer records
	private LocationResolver resolver;
	
	// switch controlling use of fuzzy matching
	private final boolean fuzzy;
	
	public GeoParser(LocationExtractor extractor, LocationResolver resolver, boolean fuzzy){
		this.extractor = extractor;
		this.resolver = resolver;
		this.fuzzy = fuzzy;
	}
	
	/**
	 * Takes an unstructured text document (as a String), extracts the
	 * location names contained therein, and resolves them into
	 * geographic entities representing the best match for those
	 * location names.
	 * 
	 * @param inputText		unstructured text to be processed
	 * @return				list of geo entities resolved from text
	 * @throws Exception
	 */
	public List<ResolvedLocation> parse(String inputText) throws Exception {
		
		logger.trace("input: {}", inputText);
		
		// first, extract location names from the text
		List<LocationOccurrence> locationNames = extractor.extractLocationNames(inputText);
		
		logger.trace("extracted: {}", locationNames);
		
		// then, resolve the extracted location names against a
		// gazetteer to produce geographic entities representing the
		// locations mentioned in the original text
		List<ResolvedLocation> resolvedLocations = resolver.resolveLocations(locationNames, fuzzy);
		
		logger.trace("resolved: {}", resolvedLocations);
		
		// TODO: extract & resolve coords (lat/lon & MGRS) to nearest named location in gazetteer
		
		return resolvedLocations;
	}
	
}
