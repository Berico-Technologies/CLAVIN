package com.berico.clavin.resolver;

import static org.junit.Assert.*;

import org.apache.lucene.document.Document;
import org.junit.Test;

import com.berico.clavin.extractor.LocationOccurrence;
import com.berico.clavin.index.IndexDirectoryBuilder;

public class ResolvedLocationTest {

	@Test
	public void testEquals() {
		String geonamesEntry = "4781530	Reston	Reston	Reston,Рестон	38.96872	-77.3411	P	PPL	US		VA	059			58404	100	102	America/New_York	2011-05-14";
		String geonamesEntry2 = "478153	Reston	Reston	Reston,Рестон	38.96872	-77.3411	P	PPL	US		VA	059			58404	100	102	America/New_York	2011-05-14";
		LocationOccurrence locationA = new LocationOccurrence("A", 0);
		Document luceneDoc = IndexDirectoryBuilder.buildDoc("Nowhere", geonamesEntry, 999, (long)999);
		Document luceneDoc2 = IndexDirectoryBuilder.buildDoc("Nowhere", geonamesEntry2, 222, (long)999);
		ResolvedLocation resolvedLocation = new ResolvedLocation(luceneDoc, locationA, false);
		ResolvedLocation resolvedLocationDupe = new ResolvedLocation(luceneDoc, locationA, false);
		ResolvedLocation resolvedLocation2 = new ResolvedLocation(luceneDoc2, locationA, false);
		
		assertTrue("ResolvedLocation == self", resolvedLocation.equals(resolvedLocation));
		assertFalse("ResolvedLocation =! null", resolvedLocation.equals(null));
		assertFalse("ResolvedLocation =! different class", resolvedLocation.equals(new Integer(0)));
		assertTrue("ResolvedLocation == dupe", resolvedLocation.equals(resolvedLocationDupe));
		assertFalse("ResolvedLocation != different geonameID", resolvedLocation.equals(resolvedLocation2));
	}

}
