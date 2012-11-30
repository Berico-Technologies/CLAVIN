package com.berico.clavin.resolver;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.berico.clavin.extractor.LocationOccurrence;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Test;

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
 * LocationResolverTest.java
 * 
 *###################################################################*/

/**
 * Ensures non-heuristic matching and fuzzy matching features are
 * working properly in {@link LocationResolver}.
 *
 */
public class LocationResolverTest {
	
	public final static Logger logger = Logger.getLogger(LocationResolverHeuristicsTest.class);
	
	// objects required for running tests
	File indexDirectory;
	LocationResolver resolverNoHeuristics;
	List<ResolvedLocation> resolvedLocations;
	
	// expected geonameID numbers for given location names
	int BOSTON_MA = 4930956;
	int RESTON_VA = 4781530;
	int STRAßENHAUS_DE = 2826158;
	int GUN_BARREL_CITY_TX = 4695535;

    //this convenience method turns an array of location name strings into a list of occurrences with fake positions.
    //(useful for tests that don't care about position in the document)
    public static List<LocationOccurrence> makeOccurrencesFromNames(String[] locationNames) {
        List<LocationOccurrence> locations = new ArrayList<LocationOccurrence>(locationNames.length);
        for(int i = 0; i < locationNames.length; ++i ) {
            locations.add(new LocationOccurrence(locationNames[i], i));
        }
        return locations;
    }

	/**
	 * Instantiate a {@link LocationResolver} without context-based
	 * heuristic matching and with fuzzy matching turned on.
	 * 
	 * @throws IOException
	 * @throws ParseException 
	 */
	@Before
	public void setUp() throws IOException, ParseException {
		// indexDirectory = new File("./src/test/resources/indices/GeoNamesSampleIndex");
		indexDirectory = new File("./IndexDirectory");
		resolverNoHeuristics = new LocationResolver(indexDirectory, 1, 1);
	}

	/**
	 * Ensure {@link LocationResolver#resolveLocations(List)} isn't
	 * choking on input.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testResolveLocations() throws IOException, ParseException {
		String[] locationNames = {"Reston", "reston", "RESTON", "Рестон", "Straßenhaus"};

		
		resolvedLocations = resolverNoHeuristics.resolveLocations(makeOccurrencesFromNames(locationNames), true);
		
		assertNotNull("Null results list received from LocationResolver", resolvedLocations);
		assertFalse("Empty results list received from LocationResolver", resolvedLocations.isEmpty());
		assertTrue("LocationResolver choked/quit after first location", resolvedLocations.size() > 1);
		
		assertEquals("LocationResolver failed exact String match", RESTON_VA, resolvedLocations.get(0).geoname.geonameID);
		assertEquals("LocationResolver failed on all lowercase", RESTON_VA, resolvedLocations.get(1).geoname.geonameID);
		assertEquals("LocationResolver failed on all uppercase", RESTON_VA, resolvedLocations.get(2).geoname.geonameID);
		assertEquals("LocationResolver failed on alternate name", RESTON_VA, resolvedLocations.get(3).geoname.geonameID);
		assertEquals("LocationResolver failed on UTF8 chars", STRAßENHAUS_DE, resolvedLocations.get(4).geoname.geonameID);
		
		// test empty input
		String[] noLocations = {};
		
		resolvedLocations = resolverNoHeuristics.resolveLocations(makeOccurrencesFromNames(noLocations), true);
		
		assertNotNull("Null results list received from LocationResolver", resolvedLocations);
		assertTrue("Non-empty results from LocationResolver on empty input", resolvedLocations.isEmpty());
		
		// test null input
		resolvedLocations = resolverNoHeuristics.resolveLocations(null, true);
		
		assertNotNull("Null results list received from LocationResolver", resolvedLocations);
		assertTrue("Non-empty results from LocationResolver on empty input", resolvedLocations.isEmpty());
	}
	
	/**
	 * Ensures Lucene isn't choking on reserved words or unescaped
	 * characters.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testSanitizedInput() throws IOException, ParseException {
		String[] locations = {"OR", "IN", "A + B", "A+B", "A +B", "A+ B", "A OR B", "A IN B", "A / B", "A \\ B",
				"Dallas/Fort Worth Airport", "New Delhi/Chennai", "Falkland ] Islands", "Baima ] County",
				"MUSES \" City Hospital", "North \" Carolina State"};
		
		resolvedLocations = resolverNoHeuristics.resolveLocations(makeOccurrencesFromNames(locations), true);
		
		// if no exceptions are thrown, the test is assumed to have succeeded
	}
	
	/**
	 * Ensure we select the correct {@link ResolvedLocation} objects
	 * when using fuzzy matching.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testFuzzyMatching() throws IOException, ParseException {
		String[] locations = {"Bostonn", "Reston12", "Bostn", "Straßenha", "Straßenhaus Airport", "Gun Barrel"};
		
		resolvedLocations = resolverNoHeuristics.resolveLocations(makeOccurrencesFromNames(locations), true);
		
		assertEquals("LocationResolver failed on extra char", BOSTON_MA, resolvedLocations.get(0).geoname.geonameID);
		assertEquals("LocationResolver failed on extra chars", RESTON_VA, resolvedLocations.get(1).geoname.geonameID);
		assertEquals("LocationResolver failed on missing char", BOSTON_MA, resolvedLocations.get(2).geoname.geonameID);
		assertEquals("LocationResolver failed on missing chars", STRAßENHAUS_DE, resolvedLocations.get(3).geoname.geonameID);
		assertEquals("LocationResolver failed on extra term", STRAßENHAUS_DE, resolvedLocations.get(4).geoname.geonameID);
		assertEquals("LocationResolver failed on missing term", GUN_BARREL_CITY_TX, resolvedLocations.get(5).geoname.geonameID);
	}

}
