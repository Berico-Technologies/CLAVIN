package com.bericotech.clavin.index;

import static org.junit.Assert.*;

import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.junit.Test;

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
 * BinarySimilarityTest.java
 * 
 *###################################################################*/

/**
 * Checks output from class used to overwrite default Lucene scoring.
 * 
 */
public class BinarySimilarityTest {

	/**
	 * Ensures output is either 1 (for all inputs > 0), or 0.
	 */
	@Test
	public void testTF() {
		DefaultSimilarity sim = new BinarySimilarity();
		assertEquals("big positive number", 1, (int)Math.round(sim.tf(999)));
		assertEquals("slightly more than 1", 1, (int)Math.round(sim.tf(2)));
		assertEquals("just 1", 1, (int)Math.round(sim.tf(1)));
		assertEquals("slightly less than 1", 0, (int)Math.round(sim.tf(0)));
		assertEquals("big negative number", 0, (int)Math.round(sim.tf(-999)));
	}

}
