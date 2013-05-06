package com.berico.clavin;

import java.io.File;
import java.util.List;

import com.berico.clavin.resolver.ResolvedLocation;
import com.berico.clavin.util.TextUtils;

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
 * WorkflowDemo.java
 * 
 *###################################################################*/

/**
 * Quick example showing how to use CLAVIN's capabilities.
 * 
 */
public class WorkflowDemo {

	/**
	 * Run this after installing & configuring CLAVIN to get a sense of
	 * how to use it.
	 * 
	 * @param args				not used
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		// Instantiate the CLAVIN GeoParser
		GeoParser parser = GeoParserFactory.getDefault("./IndexDirectory");
		
		// Unstructured text file about Somalia to be geoparsed
		File inputFile = new File("src/test/resources/sample-docs/Somalia-doc.txt");
		
		// Grab the contents of the text file as a String
		String inputString = TextUtils.fileToString(inputFile);
		
		// Parse location names in the text into geographic entities
		List<ResolvedLocation> resolvedLocations = parser.parse(inputString);
		
		// Display the ResolvedLocations found for the location names
		for (ResolvedLocation resolvedLocation : resolvedLocations)
			System.out.println(resolvedLocation);
		
		// And we're done...
		System.out.println("\n\"That's all folks!\"");
	}
}
