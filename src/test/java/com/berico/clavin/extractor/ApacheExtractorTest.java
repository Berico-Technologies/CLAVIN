package com.berico.clavin.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.berico.clavin.util.TextUtils;

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
 * ApacheExtractorTest.java
 * 
 *###################################################################*/

/**
 * Checks output produced by named entity recognizer (NER), supplied
 * by Apache OpenNLP Name Finder.
 * 
 */
public class ApacheExtractorTest {

	/**
	 * Ensures we're getting good responses from the
	 * {@link ApacheExtractor}, and that we can properly tag multiple
	 * documents with the same instance.
	 * @throws IOException 
	 */
	@Test
	public void testExtractLocationNames() throws IOException {
		ApacheExtractor extractor = new ApacheExtractor();
		File inputFile = new File("src/test/resources/sample-docs/Somalia-doc.txt");
		String inputString = TextUtils.fileToString(inputFile);
		List<LocationOccurrence> locationNames1 = extractor.extractLocationNames(inputString);
		
		assertNotNull("Null location name list received from extractor.", locationNames1);
		assertFalse("Empty location name list received from extractor.", locationNames1.isEmpty());
		assertTrue("Extractor choked/quit after first LOCATION.", locationNames1.size() > 1);
		
		List<LocationOccurrence> locationNames2 = extractor.extractLocationNames(inputString);
		assertEquals("Different extractor results for subsequent identical document.", locationNames1, locationNames2);
	}
	
}