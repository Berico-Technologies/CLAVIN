package com.bericotech.clavin.resolver;

import static org.junit.Assert.*;

import org.apache.lucene.document.Document;
import org.junit.Test;

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.index.IndexDirectoryBuilder;

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
 * ResolvedLocationTest.java
 * 
 *###################################################################*/

/**
 * Basic tests for the {@link ResolvedLocation} class, which
 * encapsulates a {@link GeoName} object representing the best match
 * between a given location name and gazetter record, along with some
 * information about the geographic entity resolution process.
 *
 */
public class ResolvedLocationTest {

    /**
     * Ensures proper performance of the overridden equals() method.
     */
    @Test
    public void testEquals() {
        // two identical sample gazetteer records from GeoNames.org
        String geonamesEntry = "4781530\tReston\tReston\tReston,Рестон\t38.96872\t-77.3411\tP\tPPL\tUS\tVA\t059\t58404\t100\t102\tAmerica/New_York\t2011-05-14";
        String geonamesEntry2 = "478153\tReston\tReston\tReston,Рестон\t38.96872\t-77.3411\tP\tPPL\tUS\tVA\t059\t58404\t100\t102\tAmerica/New_York\t2011-05-14";
        
        // create corresponding Lucene Documents for gazetteer records
        Document luceneDoc = IndexDirectoryBuilder.buildDoc("Nowhere", geonamesEntry, 999, (long)999);
        Document luceneDoc2 = IndexDirectoryBuilder.buildDoc("Nowhere", geonamesEntry2, 222, (long)999);
        
        // a bogus LocationOccurrence object for testing
        LocationOccurrence locationA = new LocationOccurrence("A", 0);
            
        // two ResolvedLocation objects created from same Lucene Doc, etc.
        ResolvedLocation resolvedLocation = new ResolvedLocation(luceneDoc, locationA, false);
        ResolvedLocation resolvedLocationDupe = new ResolvedLocation(luceneDoc, locationA, false);
        
        // an identical ResolvedLocation object created from the second Lucene doc
        ResolvedLocation resolvedLocation2 = new ResolvedLocation(luceneDoc2, locationA, false);
        
        assertTrue("ResolvedLocation == self", resolvedLocation.equals(resolvedLocation));
        assertFalse("ResolvedLocation =! null", resolvedLocation.equals(null));
        assertFalse("ResolvedLocation =! different class", resolvedLocation.equals(new Integer(0)));
        assertTrue("ResolvedLocation == dupe", resolvedLocation.equals(resolvedLocationDupe));
        assertFalse("ResolvedLocation != different geonameID", resolvedLocation.equals(resolvedLocation2));
    }

}
