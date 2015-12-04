package com.bericotech.clavin.resolver;

import static com.bericotech.clavin.resolver.ClavinLocationResolver.isDemonym;
import static org.junit.Assert.*;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * LuceneLocationResolverTest.java
 *
 *###################################################################*/

/**
 * Ensures non-heuristic matching and fuzzy matching features are
 * working properly in {@link ClavinLocationResolver}.
 *
 */
public class ClavinLocationResolverTest {
    public final static Logger logger = LoggerFactory.getLogger(ClavinLocationResolverTest.class);

    private static final int NO_HEURISTICS_MAX_HIT_DEPTH = 1;
    private static final int NO_HEURISTICS_MAX_CONTEXT_WINDOW = 1;

    // expected geonameID numbers for given location names
    private static final long BOSTON_MA = 4930956L;
    private static final long RESTON_VA = 4781530L;
    private static final long STRAßENHAUS_DE = 2826158L;
    private static final long GUN_BARREL_CITY_TX = 4695535L;

    // objects required for running tests
    private ClavinLocationResolver resolver;
    private List<ResolvedLocation> resolvedLocations;

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
     * Instantiate a {@link ClavinLocationResolver} without context-based
     * heuristic matching and with fuzzy matching turned on.
     */
    @Before
    public void setUp() throws ClavinException {
        resolver = new ClavinLocationResolver(new LuceneGazetteer(new File("./IndexDirectory")));
    }

    private List<ResolvedLocation> resolveNoHeuristics(final List<LocationOccurrence> locs, final boolean fuzzy)
            throws ClavinException {
        return resolver.resolveLocations(locs, NO_HEURISTICS_MAX_HIT_DEPTH, NO_HEURISTICS_MAX_CONTEXT_WINDOW, fuzzy);
    }

    /**
     * Ensure {@link ClavinLocationResolver#resolveLocations(List, boolean)} isn't
     * choking on input.
     */
    @Test
    public void testResolveLocations() throws ClavinException {
        String[] locationNames = {"Reston", "reston", "RESTON", "Рестон", "Straßenhaus"};


        resolvedLocations = resolveNoHeuristics(makeOccurrencesFromNames(locationNames), true);

        assertNotNull("Null results list received from LocationResolver", resolvedLocations);
        assertFalse("Empty results list received from LocationResolver", resolvedLocations.isEmpty());
        assertTrue("LocationResolver choked/quit after first location", resolvedLocations.size() > 1);

        assertEquals("LocationResolver failed exact String match", RESTON_VA, resolvedLocations.get(0).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on all lowercase", RESTON_VA, resolvedLocations.get(1).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on all uppercase", RESTON_VA, resolvedLocations.get(2).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on alternate name", RESTON_VA, resolvedLocations.get(3).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on UTF8 chars", STRAßENHAUS_DE, resolvedLocations.get(4).getGeoname().getGeonameID());

        // test empty input
        String[] noLocations = {};

        resolvedLocations = resolveNoHeuristics(makeOccurrencesFromNames(noLocations), true);

        assertNotNull("Null results list received from LocationResolver", resolvedLocations);
        assertTrue("Non-empty results from LocationResolver on empty input", resolvedLocations.isEmpty());

        // test null input
        resolvedLocations = resolveNoHeuristics(null, true);

        assertNotNull("Null results list received from LocationResolver", resolvedLocations);
        assertTrue("Non-empty results from LocationResolver on empty input", resolvedLocations.isEmpty());
    }

    /**
     * Ensures Lucene isn't choking on reserved words or unescaped
     * characters.
     */
    @Test
    public void testSanitizedInput() throws ClavinException {
        String[] locations = {"OR", "IN", "A + B", "A+B", "A +B", "A+ B", "A OR B", "A IN B", "A / B", "A \\ B",
                "Dallas/Fort Worth Airport", "New Delhi/Chennai", "Falkland ] Islands", "Baima ] County",
                "MUSES \" City Hospital", "North \" Carolina State"};

        resolvedLocations = resolveNoHeuristics(makeOccurrencesFromNames(locations), true);

        // if no exceptions are thrown, the test is assumed to have succeeded
    }

    /**
     * Ensure we select the correct {@link ResolvedLocation} objects
     * when using fuzzy matching.
     */
    @Test
    public void testFuzzyMatching() throws ClavinException {
        String[] locations = {"Bostonn", "Straßenhaus12", "Bostn", "Straßenha", "Straßenhaus Airport", "Gun Barrel"};

        resolvedLocations = resolveNoHeuristics(makeOccurrencesFromNames(locations), true);

        assertEquals("LocationResolver failed on extra char", BOSTON_MA, resolvedLocations.get(0).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on extra chars", STRAßENHAUS_DE, resolvedLocations.get(1).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on missing char", BOSTON_MA, resolvedLocations.get(2).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on missing chars", STRAßENHAUS_DE, resolvedLocations.get(3).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on extra term", STRAßENHAUS_DE, resolvedLocations.get(4).getGeoname().getGeonameID());
        assertEquals("LocationResolver failed on missing term", GUN_BARREL_CITY_TX, resolvedLocations.get(5).getGeoname().getGeonameID());
    }

    /**
     * Tests some border cases involving the resolver.
     */
    @Test
    public void testBorderCases() throws ClavinException {
        // ensure we get no matches for this crazy String
        String[] locations = {"jhadghaoidhg"};

        resolvedLocations = resolveNoHeuristics(makeOccurrencesFromNames(locations), false);
        assertTrue("LocationResolver fuzzy off, no match", resolvedLocations.isEmpty());

        resolvedLocations = resolveNoHeuristics(makeOccurrencesFromNames(locations), true);
        assertTrue("LocationResolver fuzzy on, no match", resolvedLocations.isEmpty());
    }

    /**
     * Tests functionality of demonym filter.
     */
    @Test
    public void testIsDemonym() {
        String[] locations = {"American", "Bangladeshi", "British", "America", "Bangladesh", "Britain"};
        List<LocationOccurrence> locationOccurrences = makeOccurrencesFromNames(locations);

        assertTrue("missed American as demonym", isDemonym(locationOccurrences.get(0)));
        assertTrue("missed Bangladeshi as demonym", isDemonym(locationOccurrences.get(1)));
        assertTrue("missed British as demonym", isDemonym(locationOccurrences.get(2)));
        assertFalse("mistook America as demonym", isDemonym(locationOccurrences.get(3)));
        assertFalse("mistook Bangladesh as demonym", isDemonym(locationOccurrences.get(4)));
        assertFalse("mistook Britain as demonym", isDemonym(locationOccurrences.get(5)));
    }
}
