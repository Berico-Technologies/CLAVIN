package com.berico.clavin.index;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.berico.clavin.gazetteer.GeoName;

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
 * IndexDirectoryBuilder.java
 * 
 *###################################################################*/

/**
 * Builds a Lucene index of geographic entries based on
 * the GeoNames gazetteer.
 * 
 * This program is run one-time before CLAVIN can be used.
 * 
 */
public class IndexDirectoryBuilder {
	
	public final static Logger logger = LoggerFactory.getLogger(IndexDirectoryBuilder.class);
	
	// the GeoNames gazetteer file to be loaded
	static String pathToGazetteer = "./allCountries.txt";

	/**
	 * Turns a GeoNames gazetteer file into a Lucene index, and adds
	 * some supplementary gazetteer records at the end.
	 * 
	 * @param args				not used
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		logger.info("Indexing... please wait.");
		
		// Create a new index file on disk, allowing Lucene to choose
		// the best FSDirectory implementation given the environment.
		// TODO: delete this directory first, if it exists
		FSDirectory index = FSDirectory.open(new File("./IndexDirectory"));
		
		// indexing by lower-casing & tokenizing on whitespace
		Analyzer indexAnalyzer = new WhitespaceLowerCaseAnalyzer();
		
		// create the object that will actually build the Lucene index
		IndexWriter indexWriter = new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_40, indexAnalyzer));
		
		// open the gazetteer files to be loaded
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File(pathToGazetteer)), "UTF-8"));
		BufferedReader r2 = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./src/main/resources/SupplementaryGazetteer.txt")), "UTF-8"));
		
		String line;
		
		// let's see how long this takes...
		Date start = new Date();
		
		// load GeoNames gazetteer into Lucene index
		while ((line = r.readLine()) != null)
			try {
			addToIndex(indexWriter, line);
			} catch (IllegalArgumentException e) {
				logger.info("Skipping... Error on line:" + line);
			
			}
		// add supplementary gazetteer records to index
		while ((line = r2.readLine()) != null)
			addToIndex(indexWriter, line);
		
		// that wasn't so long, was it?
		Date stop = new Date();
		
		logger.info("[DONE]");
		logger.info(indexWriter.maxDoc() + " geonames added to index.");
		logger.info("Merging indices... please wait.");
		
		indexWriter.close();
		index.close();
		r.close();
		r2.close();
		
		logger.info("[DONE]");
		
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		long elapsed_MILLIS = stop.getTime() - start.getTime();
		logger.info("Process started: " + df.format(start) + ", ended: " + df.format(stop)
				+ "; elapsed time: " + MILLISECONDS.toSeconds(elapsed_MILLIS) + " seconds.");
	}
	
	/**
	 * Adds entries to the Lucene index for each unique name associated
	 * with a {@link GeoName} object.
	 * 
	 * @param indexWriter	the object that actually builds the Lucene index
	 * @param geonameEntry	single record from GeoNames gazetteer
	 * @throws IOException
	 */
  	private static void addToIndex(IndexWriter indexWriter, String geonameEntry) throws IOException {
  		
  		// create a GeoName object from a single gazetteer record
  		GeoName geoname = GeoName.parseFromGeoNamesRecord(geonameEntry);
  		
  		// add the primary (UTF-8) name for this location
  		if (geoname.name.length() > 0)
		    indexWriter.addDocument(buildDoc(geoname.name, geonameEntry, geoname.geonameID, geoname.population));
  		
  		// add the ASCII name if it's different from the primary name
  		if (geoname.asciiName.length() > 0 && !geoname.asciiName.equals(geoname.name))
  			indexWriter.addDocument(buildDoc(geoname.asciiName, geonameEntry, geoname.geonameID, geoname.population));
  		
  		// add alternate names (if any) if they differ from the primary
  		// and alternate names
  		for (String altName : geoname.alternateNames)
  			if (altName.length() > 0 && !altName.equals(geoname.name) && !altName.equals(geoname.name))
  				indexWriter.addDocument(buildDoc(altName, geonameEntry, geoname.geonameID, geoname.population));
  	}
  	
  	/**
  	 * Builds a Lucene document to be added to the index based on a
  	 * specified name for the location and the corresponding
  	 * {@link GeoName} object.
  	 * 
  	 * @param name			name to serve as index key
  	 * @param geonameEntry	string from GeoNames gazetteer
  	 * @param geonameID		unique identifier (for quick look-up)
  	 * @param population	number of inhabitants (used for scoring)
  	 * @return
  	 */
  	private static Document buildDoc(String name, String geonameEntry, int geonameID, Long population) {
  		
  		// in case you're wondering, yes, this is a non-standard use of
  		// the Lucene Document construct
	    Document doc = new Document();
	    
	    // this is essentially the key we'll try to match location
	    // names against
	    doc.add(new TextField("indexName", name, Field.Store.YES));
	    
	    // this is the payload we'll return when matching location
	    // names to gazetteer records
	    doc.add(new StoredField("geoname", geonameEntry));
	    
	    // TODO: use geonameID to link administrative subdivisions to
	    //		 each other
	    doc.add(new IntField("geonameID", geonameID, Field.Store.YES));
	    
	    // we'll initially sort match results based on population
	    doc.add(new LongField("population", population, Field.Store.YES));
	    
	    logger.debug("Adding to index: " + name);
	    
	    return doc;
  	}

}
