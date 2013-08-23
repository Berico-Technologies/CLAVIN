package com.berico.clavin;

import java.util.ArrayList;
import java.util.List;

import com.berico.clavin.extractor.LocationExtractor;
import com.berico.clavin.extractor.coords.DdHemiLetterPatternParsingStrategy;
import com.berico.clavin.extractor.coords.DdPatternParsingStrategy;
import com.berico.clavin.extractor.coords.DmsPatternParsingStrategy;
import com.berico.clavin.extractor.coords.RegexCoordinateExtractor;
import com.berico.clavin.extractor.coords.RegexCoordinateParsingStrategy;
import com.berico.clavin.extractor.opennlp.ApacheExtractor;
import com.berico.clavin.resolver.LocationResolver;
import com.berico.clavin.resolver.impl.CoordinateCandidateSelectionStrategy;
import com.berico.clavin.resolver.impl.CoordinateIndex;
import com.berico.clavin.resolver.impl.DefaultLocationResolver;
import com.berico.clavin.resolver.impl.LocationCandidateSelectionStrategy;
import com.berico.clavin.resolver.impl.LocationNameIndex;
import com.berico.clavin.resolver.impl.ResolutionResultsReductionStrategy;
import com.berico.clavin.resolver.impl.lucene.LuceneComponents;
import com.berico.clavin.resolver.impl.lucene.LuceneComponentsFactory;
import com.berico.clavin.resolver.impl.lucene.LuceneCoordinateIndex;
import com.berico.clavin.resolver.impl.lucene.LuceneLocationNameIndex;
import com.berico.clavin.resolver.impl.strategies.IdentityReductionStrategy;
import com.berico.clavin.resolver.impl.strategies.WeightedCoordinateScoringStrategy;
import com.berico.clavin.resolver.impl.strategies.coordinates.ResolvedCoordinateWeigher;
import com.berico.clavin.resolver.impl.strategies.coordinates.SharedLocationNameWeigher;
import com.berico.clavin.resolver.impl.strategies.coordinates.VectorDistanceWeigher;
import com.berico.clavin.resolver.impl.strategies.locations.ContextualOptimizationStrategy;

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
 * 
 * If you are going to use CLAVIN in production, please don't use this!
 */
public class GeoParserFactory {
	
	public static int MAX_HIT_DEPTH = 1;
	public static int MAX_CONTENT_WINDOW = 1;
	
	/**
	 * The default LocationExtractor for CLAVIN.
	 */
	public static LocationExtractor DefaultLocationExtractor;
	
	/**
	 * Convenience method for DI/IOC containers to set the default extractor.
	 * @param extractor New Default Extractor.
	 */
	public void setLocationExtractor(LocationExtractor extractor){
		
		DefaultLocationExtractor = extractor;
	}
	
	/**
	 * Default REGEX Coordinate Parsing Strategies to use.
	 */
	public static List<RegexCoordinateParsingStrategy<?>> 
		DefaultCoordinateParsingStrategies = 
			new ArrayList<RegexCoordinateParsingStrategy<?>>();
	
	/**
	 * Convenience method for DI/IOC containers to add strategies to the static 
	 * strategy list.
	 * @param s Strategy to add.
	 */
	public void setRegexCoordinateParsingStrategy(RegexCoordinateParsingStrategy<?> s){
		DefaultCoordinateParsingStrategies.add(s);
	}
	
	/**
	 * Default Coordinate Weighers to use during resolution.
	 */
	public static List<ResolvedCoordinateWeigher> 
		DefaultCoordinateWeighers = new ArrayList<ResolvedCoordinateWeigher>();
	
	/**
	 * Convenience method for DI/IOC containers to add weighers to the static 
	 * weigher list.
	 * @param weigher Resolved Coordinate Weigher to add.
	 */
	public void setResolvedCoordinateWeigher(ResolvedCoordinateWeigher weigher){
		
		DefaultCoordinateWeighers.add(weigher);
	}
	
	/**
	 * Initialize the defaults.
	 */
	static {
		// Add the default coordinate parsing strategies.
		DefaultCoordinateParsingStrategies.add(new DmsPatternParsingStrategy());
		DefaultCoordinateParsingStrategies.add(new DdPatternParsingStrategy());
		DefaultCoordinateParsingStrategies.add(new DdHemiLetterPatternParsingStrategy());
		
		// Add the default resolved coordinate weighers.
		DefaultCoordinateWeighers.add(new SharedLocationNameWeigher());
		DefaultCoordinateWeighers.add(new VectorDistanceWeigher());
	}
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @return GeoParser
	 * @throws Exception Most likely a IOException due to inaccessible Lucene index.
	 */
	public static GeoParser getDefault(String pathToLuceneIndex) throws Exception {
		
		return getDefault(pathToLuceneIndex, MAX_HIT_DEPTH, MAX_CONTENT_WINDOW, true);
	}
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @param fuzzy Should fuzzy matching be used?
	 * @return GeoParser
	 * @throws Exception Most likely a IOException due to inaccessible Lucene index.
	 */
	public static GeoParser getDefault(
			String pathToLuceneIndex, boolean fuzzy) throws Exception {
		
		return getDefault(pathToLuceneIndex, MAX_HIT_DEPTH, MAX_CONTENT_WINDOW, fuzzy);
	}
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @param maxHitDepth Number of candidate matches to consider
	 * @param maxContentWindow How much context to consider when resolving
	 * @return GeoParser
	 * @throws Exception Most likely a IOException due to inaccessible Lucene index.
	 */
	public static GeoParser getDefault(
			String pathToLuceneIndex, int maxHitDepth, int maxContentWindow)
					throws Exception {
		
		return getDefault(pathToLuceneIndex, maxHitDepth, maxContentWindow, false);
	}
	
	/**
	 * Get the default GeoParser.
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @param maxHitDepth Number of candidate matches to consider
	 * @param maxContentWindow How much context to consider when resolving
	 * @param fuzzy Should fuzzy matching be used?
	 * @return GeoParser
	 * @throws Exception Most likely a IOException due to inaccessible Lucene index.
	 */
	public static GeoParser getDefault(
			String pathToLuceneIndex, int maxHitDepth, int maxContentWindow, boolean fuzzy) 
					 throws Exception {
		
		Options options = new Options();
		
		LuceneLocationNameIndex.configureLimit(options, maxHitDepth);
		LuceneLocationNameIndex.configureUseFuzzy(options, fuzzy);
		ContextualOptimizationStrategy.configureMaxContextWindow(options, maxContentWindow);
		
		return getDefault(pathToLuceneIndex, options);
	}
	
	/**
	 * Get the default GeoParser
	 * @param pathToLuceneIndex Path to the local Lucene index.
	 * @param options Configuration for dependent services.
	 * @return GeoParser
	 * @throws Exception Most likely a IOException due to inaccessible Lucene index.
	 */
	public static GeoParser getDefault(
			String pathToLuceneIndex, Options options) throws Exception {
		
		// I know this is a beast, but you have to end up paying the pieper sometime
		// when you use dependency injection!
		
		// Instantiate the LocationExtractor
		LocationExtractor locationExtractor = lazyGetLocationDefaultExtractor();
		
		// Instantiate the RegexCoordinateExtractor
		RegexCoordinateExtractor coordinateExtractor = 
				new RegexCoordinateExtractor(DefaultCoordinateParsingStrategies);
		
		// Instantiate the Index and other Lucene components.
		LuceneComponentsFactory factory = new LuceneComponentsFactory(pathToLuceneIndex);
		
		factory.initializeSearcher();
		
		LuceneComponents lucene = factory.getComponents();
		
		// Instantiate the Indexes.
		LocationNameIndex locationNameIndex = new LuceneLocationNameIndex(lucene);
		
		CoordinateIndex coordinateIndex = new LuceneCoordinateIndex(lucene);
		
		// Instantiate the resolution strategies
		CoordinateCandidateSelectionStrategy coordinateSelectionStrategy = 
			new WeightedCoordinateScoringStrategy(DefaultCoordinateWeighers);
		
		LocationCandidateSelectionStrategy locationSelectionStrategy = 
				new ContextualOptimizationStrategy();
		
		// Instantiate the reducer
		ResolutionResultsReductionStrategy reductionStrategy = 
				new IdentityReductionStrategy();
		
		// Instantiate the LocationResolver
		LocationResolver resolver = new DefaultLocationResolver(
				locationNameIndex, 
				coordinateIndex, 
				locationSelectionStrategy, 
				coordinateSelectionStrategy, 
				reductionStrategy);
		
		// return the GeoParser.
		return new GeoParser(locationExtractor, coordinateExtractor, resolver, options);
	}
	
	/**
	 * Lazily instantiate and return the DefaultLocationExtractor.  This will prevent
	 * the automatic instantiation of Apache OpenNLP, allowing you to override the 
	 * Default with your own implementation.
	 * @return Default LocationExtractor.
	 * @throws Exception
	 */
	private static LocationExtractor lazyGetLocationDefaultExtractor() throws Exception {
		
		if (DefaultLocationExtractor == null){

			DefaultLocationExtractor = new ApacheExtractor();	
		}
		
		return DefaultLocationExtractor;
	}
}
