package com.berico.clavin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.berico.clavin.extractor.LocationOccurrence;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

import com.berico.clavin.extractor.ApacheExtractor;
import com.berico.clavin.extractor.LocationExtractor;
import com.berico.clavin.resolver.LocationResolver;
import com.berico.clavin.resolver.ResolvedLocation;

/*#####################################################################
 * 
 * CLAVIN (Cartographic Location And Vicinity INdexer)
 * ---------------------------------------------------
 * 
 * Copyright (C) 2012 Berico Technologies
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
	
	private final static Logger logger = Logger.getLogger(GeoParser.class);

	// entity extractor to find location names in text
	private LocationExtractor extractor;
	
	// resolver to match location names against gazetteer records
	private LocationResolver resolver;
	
	// location of Lucene index built from gazetteer
	private String pathToLuceneIndex = null;
	
	// switch controlling use of fuzzy matching
	private final boolean fuzzy;
	
	/**
	 * Default constructor for {@link GeoParser}.
	 * 
	 * Instantiates a {@link LocationResolver} with default options.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public GeoParser( String pathToIndex) throws IOException, ParseException {
		// default options for resolving location names (essentially
		// looks for exact matches having the highest population)
		this(pathToIndex, 1, 1, false);
	}
	
	/**
	 * Builds a {@link GeoParser} that uses the {@link ApacheExtractor}
	 * for extracting location names from text.
	 * 
	 * Instantiates a {@link LocationResolver} with the specified
	 * options.
	 * 
	 * @param maxHitDepth		number of candidate matches to consider
	 * @param maxContextWindow	how much context to consider when resolving
	 * @param fuzzy				switch controlling use of fuzzy matching
	 * @throws IOException
	 * @throws ParseException
	 */
	public GeoParser(String pathToIndex, int maxHitDepth, int maxContextWindow, boolean fuzzy) throws IOException, ParseException {
		// instantiates an {@link ApacheExtractor} and passes it along
		// to the primary {@link GeoParser} constructor
		this(pathToIndex, new ApacheExtractor(), maxHitDepth, maxContextWindow, fuzzy);
	}
	
	/**
	 * Builds a {@link GeoParser} from an existing
	 * {@link LocationExtractor}.
	 * 
	 * Instantiates a {@link LocationResolver} with the specified
	 * options.
	 * 
	 * Use this constructor directly if you want to plug-in a different
	 * entity extractor (one that implements the
	 * {@link LocationExtractor} interface) other than the default
	 * {@link ApacheExtractor}.
	 * 
	 * @param extractor			existing {@link LocationExtractor}
	 * @param maxHitDepth		number of candidate matches to consider
	 * @param maxContextWindow	how much context to consider when resolving
	 * @param fuzzy				switch controlling use of fuzzy matching
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public GeoParser(String pathToIndex, LocationExtractor extractor, int maxHitDepth, int maxContextWindow, boolean fuzzy) throws IOException, ParseException {
		logger.debug("Initializing GeoParser; please wait...");
		this.pathToLuceneIndex = pathToIndex;
		this.extractor = extractor;
		this.fuzzy = fuzzy;
		
		// instantiate resolver with given parameters
		resolver = new LocationResolver(new File(pathToLuceneIndex), maxHitDepth, maxContextWindow);
		
		logger.debug("GeoParser intialization complete; ready for action!");
	}
	
	/**
	 * Takes an unstructured text document (as a String), extracts the
	 * location names contained therein, and resolves them into
	 * geographic entities representing the best match for those
	 * location names.
	 * 
	 * @param inputText		unstructured text to be processed
	 * @return				list of geo entities resolved from text
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public List<ResolvedLocation> parse(String inputText) throws IOException, ParseException {
		// first, extract location names from the text
		List<LocationOccurrence> locationNames = extractor.extractLocationNames(inputText);
		
		// then, resolve the extracted location names against a
		// gazetteer to produce geographic entities representing the
		// locations mentioned in the original text
		List<ResolvedLocation> resolvedLocations = resolver.resolveLocations(locationNames, fuzzy);
		
		// TODO: extract & resolve coords (lat/lon & MGRS) to nearest named location in gazetteer
		
		return resolvedLocations;
	}
	
}
