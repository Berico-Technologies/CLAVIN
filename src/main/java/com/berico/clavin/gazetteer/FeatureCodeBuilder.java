package com.berico.clavin.gazetteer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

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
 * FeatureCodeBuilder.java
 * 
 *###################################################################*/

/**
 * Generates {@link FeatureClass} enum definitions from GeoNames
 * featureCodes_en.txt file.
 *
 * TODO: clean this up and make it part of the install/build process
 *
 */
public class FeatureCodeBuilder {

	/**
	 * Reads-in featureCodes_en.txt file, spits-out
	 * {@link FeatureClass} enum definitions.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void generateFeatureCodes() throws FileNotFoundException, IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("./featureCodes_en.txt"), Charset.forName("UTF-8")));
		String line;
		while ((line = in.readLine()) != null) {
			String[] tokens = line.split("\t");
			if (tokens[0].equals("null")) {
				System.out.println("NULL(FeatureClass.NULL, \"not available\", \"\");");
			} else {
				String[] codes = tokens[0].split("\\.");
				if (tokens.length == 3) {
					System.out.println(codes[1] + "(FeatureClass." + codes[0] + ", \"" + tokens[1] + "\", \"" + tokens[2] + "\"),");
				} else {
					System.out.println(codes[1] + "(FeatureClass." + codes[0] + ", \"" + tokens[1] + "\", \"\"),");
				}
			}
		}
		in.close();
	}

}
