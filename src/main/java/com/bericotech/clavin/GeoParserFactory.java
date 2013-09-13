package com.bericotech.clavin;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import com.bericotech.clavin.extractor.ApacheExtractor;
import com.bericotech.clavin.extractor.LocationExtractor;
import com.bericotech.clavin.resolver.LocationResolver;
import com.bericotech.clavin.resolver.lucene.LuceneLocationResolver;

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
 * GeoParserFactory.java
 * 
 *###################################################################*/

/**
 * Simple Factory for the creation of GeoParser instances.  The 'default'
 * instance being Apache OpenNLP as the Extractor and a local Lucene index
 * as the LocationResolver.
 */
public class GeoParserFactory {
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @return GeoParser
	 * @throws IOException If file isn't found or can't be read.
	 * @throws ParseException
	 */
	public static GeoParser getDefault(String pathToLuceneIndex) 
			throws IOException, ParseException{
		
		return getDefault(pathToLuceneIndex, 1, 1, false);
	}
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @param fuzzy Should fuzzy matching be used?
	 * @return GeoParser
	 * @throws IOException If file isn't found or can't be read.
	 * @throws ParseException
	 */
	public static GeoParser getDefault(
			String pathToLuceneIndex, boolean fuzzy) 
					throws IOException, ParseException{
		
		return getDefault(pathToLuceneIndex, 1, 1, fuzzy);
	}
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @param maxHitDepth Number of candidate matches to consider
	 * @param maxContentWindow How much context to consider when resolving
	 * @return GeoParser
	 * @throws IOException If file isn't found or can't be read.
	 * @throws ParseException
	 */
	public static GeoParser getDefault(
			String pathToLuceneIndex, int maxHitDepth, int maxContentWindow) 
					throws IOException, ParseException{
		
		return getDefault(pathToLuceneIndex, maxHitDepth, maxContentWindow, false);
	}
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @param maxHitDepth Number of candidate matches to consider
	 * @param maxContentWindow How much context to consider when resolving
	 * @param fuzzy Should fuzzy matching be used?
	 * @return GeoParser
	 * @throws IOException If file isn't found or can't be read.
	 * @throws ParseException
	 */
	public static GeoParser getDefault(
			String pathToLuceneIndex, int maxHitDepth, int maxContentWindow, boolean fuzzy) 
					throws IOException, ParseException{
		
		LocationExtractor extractor = new ApacheExtractor();
		
		return getDefault(pathToLuceneIndex, extractor, maxHitDepth, maxContentWindow, false);
	}
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @param extractor A specific implementation of LocationExtractor to be used
	 * @param maxHitDepth Number of candidate matches to consider
	 * @param maxContentWindow How much context to consider when resolving
	 * @param fuzzy Should fuzzy matching be used?
	 * @return GeoParser
	 * @throws IOException If file isn't found or can't be read.
	 * @throws ParseException
	 */
	public static GeoParser getDefault(
			String pathToLuceneIndex, LocationExtractor extractor, int maxHitDepth, int maxContentWindow, boolean fuzzy) 
					throws IOException, ParseException{
				
		LocationResolver resolver = new LuceneLocationResolver(
				new File(pathToLuceneIndex), maxHitDepth, maxContentWindow);
		
		return new GeoParser(extractor, resolver, fuzzy);
	}
}
