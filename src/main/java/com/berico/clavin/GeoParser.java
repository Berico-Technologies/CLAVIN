package com.berico.clavin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.extractor.CoordinateExtractor;
import com.berico.clavin.extractor.CoordinateOccurrence;
import com.berico.clavin.extractor.ExtractionContext;
import com.berico.clavin.extractor.LocationExtractor;
import com.berico.clavin.extractor.LocationOccurrence;
import com.berico.clavin.resolver.LocationResolver;
import com.berico.clavin.resolver.ResolutionContext;

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
	private LocationExtractor locationExtractor;
	
	// finds coordinates in text.
	private CoordinateExtractor coordinateExtractor;
	
	// resolver to match location names against gazetteer records
	private LocationResolver resolver;

    //configuration for the resolver
    private Options options;

	/**
	 * Initialize the GeoParse with its dependent interfaces.
	 * @param locationExtractor Extract Locations from Text
	 * @param coordinateExtractor Extract Coordinates from Text
	 * @param resolver Resolve Locations and Coordinates.
	 */
	public GeoParser(
			LocationExtractor locationExtractor, 
			CoordinateExtractor coordinateExtractor, 
			LocationResolver resolver){
		
		this(locationExtractor, coordinateExtractor, resolver, new Options());
	}

    /**
    	 * Initialize the GeoParse with its dependent interfaces.
    	 * @param locationExtractor Extract Locations from Text
    	 * @param coordinateExtractor Extract Coordinates from Text
    	 * @param resolver Resolve Locations and Coordinates.
         * @param options Configuration for the components of the resolver
    	 */
    	public GeoParser(
    			LocationExtractor locationExtractor,
    			CoordinateExtractor coordinateExtractor,
    			LocationResolver resolver,
                Options options){

    		this.locationExtractor = locationExtractor;
    		this.coordinateExtractor = coordinateExtractor;
    		this.resolver = resolver;
            this.options = options;
    	}

	/**
	 * Takes an unstructured text document (as a String), extracts the
	 * location names contained therein, and resolves them into
	 * geographic entities representing the best match for those
	 * location names.
	 * 
	 * @param inputText		unstructured text to be processed
	 * @return				Locations and Coordinates resolved to Places and where
	 * they occurred in the text.
	 * @throws Exception
	 */
	public ResolutionContext parse(String inputText) throws Exception {
		
		logger.info("Input Size: {}", inputText.length());
		
		// first, extract location names from the text
		List<LocationOccurrence> locationNames = locationExtractor.extractLocationNames(inputText);
		
		logger.info("Extracted Location Count: {}", locationNames.size());
		
		// next, extract coordinates from the text
		List<CoordinateOccurrence<?>> coordinates = coordinateExtractor.extractCoordinates(inputText);
		
		logger.info("Extracted Coordinates Count: {}", coordinates.size());
		
		// build an extraction context
		ExtractionContext extractionContext = new ExtractionContext(inputText, locationNames, coordinates);
		
		// then, resolve the extracted location names and coordinates against a
		// gazetteer to produce geographic entities representing the
		// locations mentioned in the original text
		ResolutionContext resolutionContext = resolver.resolveLocations(extractionContext, options);
		
		logger.info("Resolved {} locations and {} coordinates.", 
				resolutionContext.getLocations().size(),
				resolutionContext.getCoordinates().size());
		
		return resolutionContext;
	}

	/**
	 * Get the Location Extractor.
	 * @return Location Extractor.
	 */
	public LocationExtractor getLocationExtractor() {
		return locationExtractor;
	}

	/**
	 * Get the Coordinate Extractor.
	 * @return Coordinate Extractor.
	 */
	public CoordinateExtractor getCoordinateExtractor() {
		return coordinateExtractor;
	}

	/**
	 * Get the Location Resolver.
	 * @return Location Resolver.
	 */
	public LocationResolver getLocationResolver() {
		return resolver;
	}
}
