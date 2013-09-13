package com.bericotech.clavin.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static com.bericotech.clavin.util.ListUtils.chunkifyList;

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
 * ListUtilsTest.java
 * 
 *###################################################################*/

/**
 * Tests for list processing methods.
 * 
 */
public class ListUtilsTest {
	
	List<List<Integer>> chunkedLists;

	/**
	 * Verifies the sublists produced are exactly what we expect.
	 * 
	 * Warnings are suppressed only because Eclipse/Java can't properly
	 * figure out nested generics.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testChunkifyList() {
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		
		List<List<Integer>> lists3 = Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8), Arrays.asList(9, 10));
		List<List<Integer>> lists4 = Arrays.asList(Arrays.asList(1, 2, 3, 4), Arrays.asList(5, 6, 7), Arrays.asList(8, 9, 10));
		List<List<Integer>> lists5 = Arrays.asList(Arrays.asList(1, 2, 3, 4, 5), Arrays.asList(6, 7, 8, 9, 10));
		
		chunkedLists = chunkifyList(list, 3);
		assertEquals("", lists3, chunkedLists);
		
		chunkedLists = chunkifyList(list, 4);
		assertEquals("", lists4, chunkedLists);
		
		chunkedLists = chunkifyList(list, 5);
		assertEquals("", lists5, chunkedLists);
		
		chunkedLists = chunkifyList(list, 6);
		assertEquals("", lists5, chunkedLists);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void testZeroChunk() throws IOException {
		chunkifyList(null, 0);
	}

}
