package com.bericotech.clavin;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

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
 * GeoParserFactoryTest.java
 * 
 *###################################################################*/

/**
 * Checks output of  {@link GeoParserFactory}.
 *
 */
public class GeoParserFactoryTest {

    /**
     * Ensures GeoParserFactory is correctly instantiating the
     * {@link GeoParser} class.
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testGetDefault() throws IOException, ParseException {
        assertEquals("factory index", GeoParser.class, GeoParserFactory.getDefault("./IndexDirectory").getClass());
        assertEquals("factory fuzzy", GeoParser.class, GeoParserFactory.getDefault("./IndexDirectory", true).getClass());
        assertEquals("factory parameters", GeoParser.class, GeoParserFactory.getDefault("./IndexDirectory", 1, 1).getClass());
    }

}
