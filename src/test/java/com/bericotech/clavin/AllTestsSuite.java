package com.bericotech.clavin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

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
 * AllTestsSuite.java
 * 
 *###################################################################*/

/**
 * Convenience class for running all CLAVIN JUnit tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    com.bericotech.clavin.GeoParserFactoryTest.class,
    com.bericotech.clavin.extractor.ApacheExtractorTest.class,
    com.bericotech.clavin.extractor.LocationOccurrenceTest.class,
    com.bericotech.clavin.gazetteer.GeoNameTest.class,
    com.bericotech.clavin.index.BinarySimilarityTest.class,
    com.bericotech.clavin.resolver.ResolvedLocationTest.class,
    com.bericotech.clavin.resolver.lucene.LuceneLocationResolverTest.class,
    com.bericotech.clavin.resolver.lucene.LuceneLocationResolverHeuristicsTest.class,
    com.bericotech.clavin.util.DamerauLevenshteinTest.class,
    com.bericotech.clavin.util.ListUtilsTest.class,
    com.bericotech.clavin.util.TextUtilsTest.class,
    // this one comes last as it's more of an integration test
    com.bericotech.clavin.GeoParserTest.class
})
public class AllTestsSuite {
    // THIS CLASS INTENTIONALLY LEFT BLANK
}
