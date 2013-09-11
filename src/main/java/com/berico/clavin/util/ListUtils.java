package com.berico.clavin.util;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

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
 * ListUtils.java
 * 
 *###################################################################*/

/**
 * Utility methods for list processing.
 */
public class ListUtils {

	/**
	 * Splits a list into a set of sublists (preserving order) where
	 * the size of each sublist is bound by a given max size, and
	 * ensuring that no list has fewer than half the max size number
	 * of elements.
	 * 
	 * In other words, you won't get a little rinky-dink sublist at the
	 * end that only has one or two items from the original list.
	 * 
	 * Based on: http://www.chinhdo.com/20080515/chunking/
	 * 
	 * @param list			list to be chunkified
	 * @param maxChunkSize	how big you want the chunks to be
	 * @return				a chunkified list (i.e., list of sublists)
	 */
	public static <T> List<List<T>> chunkifyList(List<T> list, int maxChunkSize) {
		
		// sanity-check input param
		if (maxChunkSize < 1)
			throw new InvalidParameterException("maxChunkSize must be greater than zero");
		
		// initialize return object
		List<List<T>> chunkedLists = new ArrayList<List<T>>();
		
		// if the given list is smaller than the maxChunksize, there's
		// no need to break it up into chunks
		if (list.size() <= maxChunkSize) {
			chunkedLists.add(list);
			return chunkedLists;
		}
		
		// initialize counters
		int index = 0;
		int count;
		
		// loop through and grab chunks of maxChunkSize from the
		// original list, but stop early enough so that we don't wind
		// up with tiny runt chunks at the end
		while (index < list.size() - (maxChunkSize * 2)) {
			count = Math.min(index + maxChunkSize, list.size());
			chunkedLists.add(list.subList(index, count));
			index += maxChunkSize;
		}
		
		// take whatever's left, split it into two relatively-equal
		// chunks, and add these to the return object
		count = index + ((list.size() - index) / 2);
		chunkedLists.add(list.subList(index, count));
		chunkedLists.add(list.subList(count, list.size()));
		
		return chunkedLists;
	}
	
}
