package com.berico.clavin.extractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

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
 * LocationExtractor.java
 * 
 *###################################################################*/

/**
 * Extracts location names from unstructured text documents using a
 * named entity recognizer (Apache OpenNLP Name Finder).
 *
 */
public class LocationExtractor {
    
	// the actual named entity recognizer (NER) object
	private NameFinderME nameFinder;
	
	// used to tokenize plain text into the OpenNLP format
	private TokenizerME tokenizer;
	
	// resource files used by Apache OpenNLP Name Finder
	private static final String pathToNERModel = "/en-ner-location.bin";
	private static final String pathToTokenizerModel = "/en-token.bin";
	
	/**
	 * Builds a {@link LocationExtractor} by instantiating the OpenNLP
	 * Name Finder and Tokenizer.
	 * 
	 * @throws IOException 
	 */
	public LocationExtractor() throws IOException {
		nameFinder = new NameFinderME(new TokenNameFinderModel( LocationExtractor.class.getResourceAsStream(pathToNERModel)));
		tokenizer = new TokenizerME(new TokenizerModel(LocationExtractor.class.getResourceAsStream(pathToTokenizerModel)));
	}
	
	/**
	 * Extracts location names from unstructured text using the named
	 * entity recognizer (NER) feature provided by the Apache OpenNLP
	 * Name Finder.
	 * 
	 * @param plainText		Contents of text document
	 * @return				List of location name Strings
	 */
	public List<String> extractLocationNames(String plainText) {
		
		// tokenize the text into the required OpenNLP format
		String[] tokens = tokenizer.tokenize(plainText);
		
		// find the location names in the tokenized text
		Span nameSpans[] = nameFinder.find(tokens);
		
		// create the return object
		List<String> nerResults = new ArrayList<String>();
		
		// extract the location names found in the text and add them to
		// the return object
		for (Span span : nameSpans) {
			String locationName = "";
			for (int i = span.getStart(); i < span.getEnd(); i++)
				locationName = locationName + tokens[i] + " ";
			locationName = locationName.trim();
			nerResults.add(locationName);
		}
		
		// this is necessary to maintain consistent results across
		// multiple runs on the same data, which is what we want
		nameFinder.clearAdaptiveData();
		
		return nerResults;
	}

}
