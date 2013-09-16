package com.bericotech.clavin.util;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

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
 * DamerauLevenshtein.java
 * 
 *###################################################################*/

/**
 * Utility functions for calculating edit distance between strings,
 * consistent with the Damerau-Levenshtein metric.
 * 
 * Transposition, substitution, insertion, and deletion operations are
 * all considered to be one edit each. Multiple substring edits (e.g.,
 * adjacent transpositions) are supported, unlike in "optimal string
 * alignment distance" where no substring may be edited more than once.
 */
public class DamerauLevenshtein {
	
	// sentinel value for the end of contents in an "infinite" array
	final static Null endMarker = new Null();

	/**
	 * Computes the true Damerau–Levenshtein edit distance
	 * (with adjacent transpositions) between two given strings.<br><br>
	 * 
	 * Based on <a href="http://en.wikipedia.org/wiki/Damerau–Levenshtein_distance">C# code from Wikipedia</a>.
	 * 
	 * @param str1	First string being compared
	 * @param str2	Second string being compared
	 * @return		Edit distance between strings
	 */
	public static int damerauLevenshteinDistance(String str1, String str2) {
		
		// return fast if one or both strings is empty or null
		if ((str1 == null) || str1.isEmpty()) {
			if ((str2 == null) || str2.isEmpty()) {
				return 0;
			} else {
				return str2.length();
			}
		} else if ((str2 == null) || str2.isEmpty()) {
			return str1.length();
		}
		
		// split strings into string arrays
		String[] stringArray1 = str1.split("");
		String[] stringArray2 = str2.split("");
		
		// initialize matrix values
		int[][] matrix = new int[stringArray1.length + 2][stringArray2.length + 2];
		int bound = stringArray1.length + stringArray2.length;
		matrix[0][0] = bound;
		for (int i = 0; i <= stringArray1.length; i++) {
			matrix[i + 1][1] = i;
			matrix[i + 1][0] = bound;
		}
		for (int j = 0; j <= stringArray2.length; j++) {
			matrix[1][j + 1] = j;
			matrix[0][j + 1] = bound;
		}
		
		// initialize dictionary
		SortedMap<String, Integer> dictionary = new TreeMap<String, Integer>();
		for (String letter : (str1 + str2).split("")) {
			if (!dictionary.containsKey(letter)) {
				dictionary.put(letter, 0);
			}
		}
		
		// compute edit distance between strings
		for (int i = 1; i <= stringArray1.length; i++) {
			int index = 0;
			for (int j = 1; j <= stringArray2.length; j++) {
				int i1 = dictionary.get(stringArray2[j - 1]);
				int j1 = index;
				if (stringArray1[i - 1].equals(stringArray2[j - 1])) {
					matrix[i + 1][j + 1] = matrix[i][j];
					index = j;
				} else {
					matrix[i + 1][j + 1] = Math.min(matrix[i][j], Math.min(matrix[i + 1][j], matrix[i][j + 1])) + 1;
				}
				
				matrix[i + 1][j + 1] = Math.min(matrix[i + 1][j + 1], matrix[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
			}
			
			dictionary.put(stringArray1[i - 1], i);
		}
		
		return matrix[stringArray1.length + 1][stringArray2.length + 1];
	}
	
	/**
	 * Convenience method for calling {@link #damerauLevenshteinDistance(String str1, String str2)}
	 * when you don't care about case sensitivity.
	 * 
	 * @param str1	First string being compared
	 * @param str2	Second string being compared
	 * @return		Case-insensitive edit distance between strings
	 */
	public static int damerauLevenshteinDistanceCaseInsensitive(String str1, String str2) {
		return damerauLevenshteinDistance(str1.toLowerCase(), str2.toLowerCase());
	}
	
	/**
	 * Fast method for determining whether the Damerau-Levenshtein edit
	 * distance between two strings is less than 2.
	 * 
	 * Returns as quick as possibly by stopping once multiple edits are
	 * found. Significantly faster than {@link #damerauLevenshteinDistance(String str1, String str2)}
	 * which explores every path between every string to get the exact
	 * edit distance. Despite the speed boost, we maintain consistency
	 * with {@link #damerauLevenshteinDistance(String str1, String str2)}.
	 * 
	 * @param str1	First string being compared
	 * @param str2	Second string being compared
	 * @return		True if DL edit distance < 2, false otherwise
	 */
	public static boolean isEditDistance1(String str1, String str2) {
		// one or both strings is empty or null
		if ((str1 == null) || str1.isEmpty()) {
			if ((str2 == null) || str2.isEmpty()) {
				return true;
			} else {
				return (str2.length() <= 1);
			}
		} else if ((str2 == null) || str2.isEmpty()) {
			return (str1.length() <= 1);
		}
		
		// difference between string lengths ensures edit distance > bound
		if (Math.abs(str1.length() - str2.length()) > 1) return false;
		
		// initialize counters
		int offset1 = 0;
		int offset2 = 0;
		int i = 0;
		
		InfiniteCharArray chars1 = new InfiniteCharArray(str1.toCharArray());
		InfiniteCharArray chars2 = new InfiniteCharArray(str2.toCharArray());
		
		while (!chars1.get(i + offset1).equals(endMarker) || !chars2.get(i + offset2).equals(endMarker)) {
			if (!chars1.get(i + offset1).equals(chars2.get(i + offset2))) { // character mismatch
				if ((chars1.get(i + offset1).equals(chars2.get(i + offset2 + 1))) &&
						(chars1.get(i + offset1 + 1).equals(chars2.get(i + offset2))) &&
						(chars1.remainder(i + offset1 + 2).equals(chars2.remainder(i + offset2 + 2)))) { // transposition
					i = i + 2; // move past the transposition
				} else if (chars1.remainder(i + offset1).equals(chars2.remainder(i + offset2 + 1))) { // insertion
					offset2++; // realign
				} else if (chars1.remainder(i + offset1 + 1).equals(chars2.remainder(i + offset2))) { // deletion
					offset1++; // realign
				} else if (chars1.remainder(i + offset1 + 1).equals(chars2.remainder(i + offset2 + 1))) { // substitution
					i++; // 
				} else return false; // multiple edits
			}
			
			i++;
		}
		
		return true;
	}
}

/**
 * Convenience class allowing us to avoid running into
 * ArrayIndexOutOfBoundsExceptions (thanks, Java!)
 *
 */
class InfiniteCharArray {
	
	// the array being encapsulated
	private char[] array;
	
	/**
	 * Sole constructor.
	 * 
	 * @param array		the array to be encapsulated
	 */
	protected InfiniteCharArray(char[] array) {
		this.array = array;
	}
	
	/**
	 * If we try to retrieve what lies beyond the end of values, return
	 * a {@link Null} object instead of throwing an
	 * ArrayIndexOutOfBoundsException. Otherwise, return the value at
	 * at the given index.
	 * 
	 * @param index		the position in the array for which we seek a value
	 * @return			the value at that index or a Null object if we're "out of bounds"
	 */
	protected Object get(int index) {
		if (index < this.array.length) {
			return this.array[index];
		} else {
			return new Null();
		}
	}
	
	/**
	 * Get the contents of the char array to the right of the given
	 * index, and return it as a String.
	 * 
	 * @param index		left bound of the string we're pulling from the char array
	 * @return			a string representing everything to the right of the index
	 */
	protected String remainder(int index) {
		if (index > this.array.length)
			return "";
		else return new String(Arrays.copyOfRange(this.array, index, this.array.length));
	}
}

/**
 * This is what gets returned instead of an ArrayIndexOutOfBoundsException
 * when we use an {@link InfiniteCharArray}.
 *
 */
class Null {
	
	/**
	 * Call the "super" constructor.
	 */
	protected Null() {
		super();
	}
	
	/**
	 * For pretty printing.
	 */
	@Override
	public String toString() {
		return "Null";
	}
	
	/**
	 * All instances of this class are effectively the same, so treat
	 * them as equal.
	 */
	@Override
	public boolean equals(Object anObject) {
		if (anObject.getClass().equals(this.getClass())) {
			return true;
		} else return false;
	}
}
