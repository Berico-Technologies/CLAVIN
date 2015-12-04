package com.bericotech.clavin;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

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
 * GeoParserTest.java
 * 
 *###################################################################*/

/**
 * Checks output produced by {@link GeoParser}, which is more of an
 * integration test than a unit test.
 *
 */
public class GeoParserTest {
    
    // expected geonameID numbers for given location names
    long UNITED_STATES = 6252001L;
    long VERMONT = 5242283L;
    long MASSACHUSETTS = 6254926L;
    
    /**
     * Ensures we're getting good output from the end-to-end GeoParser
     * process.
     * @throws Exception
     */
    @Test
    public void testParse() throws Exception {
        // instantiate the CLAVIN GeoParser
        GeoParser parser = GeoParserFactory.getDefault("./IndexDirectory");
        
        // sample text to be geoparsed
        String inputText = "Calvin Coolidge was the 30th president " +
                "of the United States. He was born in Vermont and " +
                "died in Massachusetts.";
        
        // parse location names in the text into geographic entities
        List<ResolvedLocation> resolvedLocations = parser.parse(inputText);
        
        // check the output
        assertEquals("Wrong number of ResolvedLocations", 3, resolvedLocations.size());
        assertEquals("Incorrect ResolvedLocation", UNITED_STATES, resolvedLocations.get(0).getGeoname().getGeonameID());
        assertEquals("Incorrect ResolvedLocation", VERMONT, resolvedLocations.get(1).getGeoname().getGeonameID());
        assertEquals("Incorrect ResolvedLocation", MASSACHUSETTS, resolvedLocations.get(2).getGeoname().getGeonameID());
        assertEquals("Incorrect position of LocationOccurance", inputText.indexOf("United States"), resolvedLocations.get(0).getLocation().getPosition());
        assertEquals("Incorrect position of LocationOccurance", inputText.indexOf("Vermont"), resolvedLocations.get(1).getLocation().getPosition());
        assertEquals("Incorrect position of LocationOccurance", inputText.indexOf("Massachusetts"), resolvedLocations.get(2).getLocation().getPosition());

    }

}
