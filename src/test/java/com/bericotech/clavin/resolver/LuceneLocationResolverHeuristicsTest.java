package com.bericotech.clavin.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.resolver.LuceneLocationResolver;
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
 * LuceneLocationResolverHeuristicsTest.java
 * 
 *###################################################################*/

/**
 * Tests the mapping of location names into
 * {@link ResolvedLocation} objects as performed by
 * {@link LocationResolver#resolveLocations(List, boolean)}.
 */
public class LuceneLocationResolverHeuristicsTest {
    
    public final static Logger logger = LoggerFactory.getLogger(LuceneLocationResolverHeuristicsTest.class);
    
    // objects required for running tests
    File indexDirectory;
    LuceneLocationResolver resolverNoHeuristics;
    LuceneLocationResolver resolverWithHeuristics;
    List<ResolvedLocation> resolvedLocations;
    
    // expected geonameID numbers for given location names
    int BOSTON_MA = 4930956;
    int HAVERHILL_MA = 4939085;
    int WORCESTER_MA = 4956184;
    int SPRINGFIELD_MA = 4951788;
    int LEOMINSTER_MA = 4941873;
    int CHICAGO_IL = 4887398;
    int ROCKFORD_IL = 4907959;
    int SPRINGFIELD_IL = 4250542;
    int DECATUR_IL = 4236895;
    int KANSAS_CITY_MO = 4393217;
    int SPRINGFIELD_MO = 4409896;
    int ST_LOUIS_MO = 6955119;
    int INDEPENDENCE_MO = 4391812;
    int LONDON_UK = 2643741;
    int MANCHESTER_UK = 2643123;
    int HAVERHILL_UK = 2647310;
    int WORCESTER_UK = 2633560;
    int RESTON_VA = 4781530;
    int STRAßENHAUS_DE = 2826158;
    int GUN_BARREL_CITY_TX = 4695535;
    int TORONTO_ON = 6167865;
    int OTTAWA_ON = 6094817;
    int HAMILTON_ON = 5969782;
    int KITCHENER_ON = 5992996;
    int LONDON_ON = 6058560;
    
    /**
     * Instantiate two {@link LuceneLocationResolver} objects, one without
     * context-based heuristic matching and other with it turned on.
     * 
     * @throws IOException
     * @throws ParseException 
     */
    @Before
    public void setUp() throws IOException, ParseException {
        indexDirectory = new File("./IndexDirectory");
        resolverNoHeuristics = new LuceneLocationResolver(indexDirectory, 1, 1);
        resolverWithHeuristics = new LuceneLocationResolver(indexDirectory, 5, 5);
    }
    
    /**
     * Ensure we select the correct {@link ResolvedLocation} objects
     * without using context-based heuristic matching.
     * 
     * Without heuristics, {@link LuceneLocationResolver} will default to
     * mapping location name Strings to the matching
     * {@link ResolvedLocation} object with the greatest population.
     * 
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testNoHeuristics() throws IOException, ParseException {
        String[] locations = {"Haverhill", "Worcester", "Springfield", "Kansas City"};
        
        resolvedLocations = resolverNoHeuristics.resolveLocations(LuceneLocationResolverTest.makeOccurrencesFromNames(locations), false);
        
        assertEquals("LocationResolver chose the wrong \"Haverhill\"", HAVERHILL_MA, resolvedLocations.get(0).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Worcester\"", WORCESTER_UK, resolvedLocations.get(1).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Springfield\"", SPRINGFIELD_MO, resolvedLocations.get(2).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Kansas City\"", KANSAS_CITY_MO, resolvedLocations.get(3).geoname.geonameID);
    }

    /**
     * Ensure we select the correct Springfield in a document about
     * Massachusetts using context-based heuristic matching.
     * 
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testHeuristicsMassachusetts() throws IOException, ParseException {
        String[] locations = {"Boston", "Haverhill", "Worcester", "Springfield", "Leominister"};
        
        resolvedLocations = resolverWithHeuristics.resolveLocations(LuceneLocationResolverTest.makeOccurrencesFromNames(locations), true);
        
        assertEquals("LocationResolver chose the wrong \"Boston\"", BOSTON_MA, resolvedLocations.get(0).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Haverhill\"", HAVERHILL_MA, resolvedLocations.get(1).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Worcester\"", WORCESTER_MA, resolvedLocations.get(2).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Springfield\"", SPRINGFIELD_MA, resolvedLocations.get(3).geoname.geonameID);
    }
    
    /**
     * Ensure we select the correct Springfield in a document about
     * Illinois using context-based heuristic matching.
     * 
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testHeuristicsIllinois() throws IOException, ParseException {
        String[] locations = {"Chicago", "Rockford", "Springfield", "Decatur"};
        
        resolvedLocations = resolverWithHeuristics.resolveLocations(LuceneLocationResolverTest.makeOccurrencesFromNames(locations), true);
        
        assertEquals("LocationResolver chose the wrong \"Chicago\"", CHICAGO_IL, resolvedLocations.get(0).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Rockford\"", ROCKFORD_IL, resolvedLocations.get(1).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Springfield\"", SPRINGFIELD_IL, resolvedLocations.get(2).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Decatur\"", DECATUR_IL, resolvedLocations.get(3).geoname.geonameID);
    }
    
    /**
     * Ensure we select the correct Springfield in a document about
     * Missouri using context-based heuristic matching.
     * 
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testHeuristicsMissouri() throws IOException, ParseException {       
        String[] locations = {"Kansas City", "Springfield", "St. Louis", "Independence"};
        
        resolvedLocations = resolverWithHeuristics.resolveLocations(LuceneLocationResolverTest.makeOccurrencesFromNames(locations), true);
        
        assertEquals("LocationResolver chose the wrong \"Kansas City\"", KANSAS_CITY_MO, resolvedLocations.get(0).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Springfield\"", SPRINGFIELD_MO, resolvedLocations.get(1).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"St. Louis\"", ST_LOUIS_MO, resolvedLocations.get(2).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Independence\"", INDEPENDENCE_MO, resolvedLocations.get(3).geoname.geonameID);
    }
    
    /**
     * Ensure we select the correct Haverhill in a document about
     * England using context-based heuristic matching.
     * 
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testHeuristicsEngland() throws IOException, ParseException {            
        String[] locations = {"London", "Manchester", "Haverhill"};
        
        resolvedLocations = resolverWithHeuristics.resolveLocations(LuceneLocationResolverTest.makeOccurrencesFromNames(locations), true);
        
        assertEquals("LocationResolver chose the wrong \"London\"", LONDON_UK, resolvedLocations.get(0).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Manchester\"", MANCHESTER_UK, resolvedLocations.get(1).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Haverhill\"", HAVERHILL_UK, resolvedLocations.get(2).geoname.geonameID);
    }
    
    /**
     * Ensure we select the correct London in a document about
     * Ontario using context-based heuristic matching.
     * 
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testHeuristicsOntario() throws IOException, ParseException {            
        String[] locations = {"Toronto", "Ottawa", "Hamilton", "Kitchener", "London"};
        
        resolvedLocations = resolverWithHeuristics.resolveLocations(LuceneLocationResolverTest.makeOccurrencesFromNames(locations), true);
        
        assertEquals("LocationResolver chose the wrong \"Toronto\"", TORONTO_ON, resolvedLocations.get(0).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Ottawa\"", OTTAWA_ON, resolvedLocations.get(1).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Hamilton\"", HAMILTON_ON, resolvedLocations.get(2).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"Kitchener\"", KITCHENER_ON, resolvedLocations.get(3).geoname.geonameID);
        assertEquals("LocationResolver chose the wrong \"London\"", LONDON_ON, resolvedLocations.get(4).geoname.geonameID);
    }
    
    /**
     * Tests some border cases involving the resolver.
     * 
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testBorderCases() throws IOException, ParseException {
        // ensure we get no matches for this crazy String
        String[] locations = {"jhadghaoidhg"};  
        
        resolvedLocations = resolverWithHeuristics.resolveLocations(LuceneLocationResolverTest.makeOccurrencesFromNames(locations), false);
        assertTrue("Heuristic LocationResolver fuzzy off, no match", resolvedLocations.isEmpty());
        
        resolvedLocations = resolverWithHeuristics.resolveLocations(LuceneLocationResolverTest.makeOccurrencesFromNames(locations), true);
        assertTrue("Heuristic LocationResolver fuzzy on, no match", resolvedLocations.isEmpty());
    }

}
