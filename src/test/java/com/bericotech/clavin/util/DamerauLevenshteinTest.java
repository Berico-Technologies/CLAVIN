package com.bericotech.clavin.util;

import static com.bericotech.clavin.util.DamerauLevenshtein.damerauLevenshteinDistance;
import static com.bericotech.clavin.util.DamerauLevenshtein.damerauLevenshteinDistanceCaseInsensitive;
import static com.bericotech.clavin.util.DamerauLevenshtein.isEditDistance1;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bericotech.clavin.GeoParser;

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
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * ====================================================================
 * 
 * DamerauLevenshteinTest.java
 * 
 *###################################################################*/

/**
 * Some tests designed to put the Damerau-Levenshtein utilities through
 * their paces.
 * 
 */
public class DamerauLevenshteinTest {
    
    private static final Logger logger = LoggerFactory.getLogger(GeoParser.class);
    
    /**
     * Simple tests to make sure we're getting the correct edit distance
     * for all the different edit operations, and a "smoke test" to
     * ensure we're not crashing on unexpected input.
     */
    @Test
    public void testDamerauLevenshteinDistance() {
        String a = null; String b = null;
        assertEquals("both null", 0, damerauLevenshteinDistance(a, b));
        
        a = null; b = "y";
        assertEquals("first null", 1, damerauLevenshteinDistance(a, b));
        
        a = "x"; b = null;
        assertEquals("second null", 1, damerauLevenshteinDistance(a, b));
        
        a = ""; b = "";
        assertEquals("both empty", 0, damerauLevenshteinDistance(a, b));
        
        a = ""; b = "y";
        assertEquals("first empty", 1, damerauLevenshteinDistance(a, b));
        
        a = "x"; b = "";
        assertEquals("second empty", 1, damerauLevenshteinDistance(a, b));
        
        a = "x"; b = "x";
        assertEquals("same", 0, damerauLevenshteinDistance(a, b));
        
        a = "x"; b = "y";
        assertEquals("message", 1, damerauLevenshteinDistance(a, b));
        
        a = "xy"; b = "x";
        assertEquals("substitution", 1, damerauLevenshteinDistance(a, b));
        
        a = "x"; b = "xy";
        assertEquals("deletion", 1, damerauLevenshteinDistance(a, b));
        
        a = "xy"; b = "yx";
        assertEquals("transposition", 1, damerauLevenshteinDistance(a, b));
        
        a = "xyz"; b = "yzx";
        assertEquals("adjacent transpositions", 2, damerauLevenshteinDistance(a, b));
        
        a = "xyz"; b = "zx";
        assertEquals("editing a substring more than once", 2, damerauLevenshteinDistance(a, b));
        
        // generate 100k pairs of random strings & make sure we can handle them
        Random random = new Random();
        for (int i = 0; i < 100000; i++) {
            a = new BigInteger(130, random).toString(32);
            b = new BigInteger(130, random).toString(32);
            assertTrue("smoke test: " + a + " vs " + b, damerauLevenshteinDistance(a, b) > -1);
        }
    }
    
    /**
     * Ensure the case-insensitive version of DamerauLevenshteinDistance() maintains
     * "triangle equality" between strings that are identical except for case.
     */
    @Test
    public void testDamerauLevenshteinDistanceCaseInsensitive() {
        String a = "Berico Technologies"; String b = "BERICO TECHNOLOGIES"; String c = "bErIcO tEcHnOlOgIeS";
        assertEquals("unwanted case sensitivity", 0, damerauLevenshteinDistanceCaseInsensitive(a, b));
        assertEquals("unwanted case sensitivity", 0, damerauLevenshteinDistanceCaseInsensitive(a, c));
        assertEquals("unwanted case sensitivity", 0, damerauLevenshteinDistanceCaseInsensitive(b, c));
    }
    
    /**
     * Some simple tests to ensure we're getting correct output for
     * various combinations of edit operations, plus a "smoke test" to
     * ensure we're keeping consistent with damerauLevenshteinDistance().
     */
    @Test
    public void testIsEditDistance1() {
        String a = null; String b = null;
        assertTrue("both null", isEditDistance1(a, b));
        
        a = null; b = "y";
        assertTrue("first null", isEditDistance1(a, b));
        
        a = "x"; b = null;
        assertTrue("second null", isEditDistance1(a, b));
        
        a = ""; b = "";
        assertTrue("both empty", isEditDistance1(a, b));
        
        a = ""; b = "y";
        assertTrue("first empty", isEditDistance1(a, b));
        
        a = "x"; b = "";
        assertTrue("second empty", isEditDistance1(a, b));
        
        a = null; b = "xyz";
        assertFalse("first null, second long", isEditDistance1(a, b));
        
        a = "xyz"; b = null;
        assertFalse("second null, first long", isEditDistance1(a, b));
        
        a = "x"; b = "x";
        assertTrue("short same", isEditDistance1(a, b));
        
        a = "Berico Technologies"; b = "Berico Technologies";
        assertTrue("long same", isEditDistance1(a, b));
        
        a = "x"; b = "y";
        assertTrue("simple substitution", isEditDistance1(a, b));
        
        a = "xzxcvbnm"; b = "yzxcvbnm";
        assertTrue("beginning substitution", isEditDistance1(a, b));
        
        a = "zxcvbnmxzxcvbnm"; b = "zxcvbnmyzxcvbnm";
        assertTrue("middle substitution", isEditDistance1(a, b));
        
        a = "zxcvbnmx"; b = "zxcvbnmy";
        assertTrue("ending substitution", isEditDistance1(a, b));
        
        a = "x"; b = "xy";
        assertTrue("simple addition", isEditDistance1(a, b));

        a = "xzxcvbnm"; b = "xyzxcvbnm";
        assertTrue("beginning addition", isEditDistance1(a, b));

        a = "zxcvbnmxzxcvbnm"; b = "zxcvbnmxyzxcvbnm";
        assertTrue("middle addition", isEditDistance1(a, b));
        
        a = "zxcvbnmx"; b = "zxcvbnmxy";
        assertTrue("ending addition", isEditDistance1(a, b));
        
        a = "xy"; b = "x";
        assertTrue("simple deletion", isEditDistance1(a, b));
        
        a = "xyzxcvbnm"; b = "xzxcvbnm";
        assertTrue("beginning deletion", isEditDistance1(a, b));
        
        a = "zxcvbnmxyzxcvbnm"; b = "zxcvbnmxzxcvbnm";
        assertTrue("middle deletion", isEditDistance1(a, b));
        
        a = "zxcvbnmxy"; b = "zxcvbnmx";
        assertTrue("ending deletion", isEditDistance1(a, b));
        
        a = "xy"; b = "yx";
        assertTrue("simple transposition", isEditDistance1(a, b));
        
        a = "xyzxcvbnm"; b = "yxzxcvbnm";
        assertTrue("beginning transposition", isEditDistance1(a, b));
        
        a = "zxcvbnmxyzxcvbnm"; b = "zxcvbnmyxzxcvbnm";
        assertTrue("middle transposition", isEditDistance1(a, b));
        
        a = "zxcvbnmxy"; b = "zxcvbnmyx";
        assertTrue("ending transposition", isEditDistance1(a, b));
        
        a = "xyz"; b = "yzx";
        assertFalse("simple adjacent transpositions", isEditDistance1(a, b));
        
        a = "xyzzxcvbnm"; b = "yzxzxcvbnm";
        assertFalse("beginning adjacent transpositions", isEditDistance1(a, b));
        
        a = "zxcvbnmxyzzxcvbnm"; b = "zxcvbnmyzxzxcvbnm";
        assertFalse("middle adjacent transpositions", isEditDistance1(a, b));
        
        a = "zxcvbnmxyz"; b = "zxcvbnmyzx";
        assertFalse("ending adjacent transpositions", isEditDistance1(a, b));
        
        a = "xyz"; b = "zx";
        assertFalse("simple editing a substring more than once", isEditDistance1(a, b));
        
        a = "xyzzxcvbnm"; b = "zxzxcvbnm";
        assertFalse("beginning editing a substring more than once", isEditDistance1(a, b));
        
        a = "zxcvbnmxyzzxcvbnm"; b = "zxcvbnmzxzxcvbnm";
        assertFalse("middle editing a substring more than once", isEditDistance1(a, b));
        
        a = "zxcvbnmxyz"; b = "zxcvbnmzx";
        assertFalse("ending editing a substring more than once", isEditDistance1(a, b));
        
        a = "xy"; b = "ab";
        assertFalse("simple edit distance is 2", isEditDistance1(a, b));
        
        a = "xyzxcvbnm"; b = "abzxcvbnm";
        assertFalse("beginning edit distance is 2", isEditDistance1(a, b));
        
        a = "zxcvbnmxyzxcvbnm"; b = "zxcvbnmabzxcvbnm";
        assertFalse("middle edit distance is 2", isEditDistance1(a, b));
        
        a = "zxcvbnmxy"; b = "zxcvbnmab";
        assertFalse("ending edit distance is 2", isEditDistance1(a, b));
        
        a = "xzxcvbnmy"; b = "azxcvbnmb";
        assertFalse("discontinuous edit distance is 2", isEditDistance1(a, b));
        
        a = "aaaaabbbbbx"; b = "aaaabbbbby";
        assertFalse("subtraction then substitution", isEditDistance1(a, b));
        
        a = "aaaaabbbbb"; b = "aaaababbbby";
        assertFalse("transposition then addition", isEditDistance1(a, b));
        
        // generate 100k random strings, randomly mutate them, & ensure
        // we're keeping consistent with damerauLevenshteinDistance()
        Random random = new Random();
        StringMutator mutator = new StringMutator();
        for (int i = 0; i < 100000; i++) {
            a = new BigInteger(130, random).toString(32);
            b = mutator.mutateString(a, 10);
            if (damerauLevenshteinDistance(a, b) < 2)
                assertTrue("consistent with true DL", isEditDistance1(a, b));
            else assertFalse("consistent with true DL", isEditDistance1(a, b));
        }   
    }
    
    /**
     * Maximize test coverage by checking toString() method of inner
     * Null class.
     */
    @Test
    public void testNullToString() {
        Null myNull = new Null();
        assertTrue("Null class toString() not \"Null\"", myNull.toString().equals("Null"));
    }
    
    /**
     * Facilitates DNA-like mutation of strings; used only for testing
     * implementation of Damerau-Levenshtein algorithm.
     */
    private class StringMutator {
        
        Random r;
        
        public StringMutator() {
            r = new Random();
        }
        
        /**
         * (Pseudo-)randomly mutates strings.
         * 
         * Goes through a given string one index position at a time, and
         * modifies the chars at that position by randomly performing
         * either a transposition, substitution, insertion, or deletion
         * operation. The odds that any given char will be modified is
         * 1 / the mutationFactor parameter. For example, if mutationFactor
         * = 1, every character will potentially be modified, while if
         * mutationFactor = 10, 1 in 10 characters will be modified.
         * 
         * @param a                 String to be mutated
         * @param mutationFactor    Likelihood that any char will be changed
         * @return                  a "mutated" String
         */
        public String mutateString(String a, int mutationFactor) {
            String b = a; // start with a perfect copy
            for (int j = 1; j < b.length() - 1; j++) { // all but last char
                if (r.nextInt(mutationFactor) == 0) {
                    switch (r.nextInt(4)) {
                        case 0: b = b.substring(0, j) + b.substring(j + 1, j + 2) + b.substring(j, j + 1) + b.substring(j + 2); break; // transposition
                        case 1: b = b.substring(0, j) + randChar() + b.substring(j + 1); break; // substitution
                        case 2: b = b.substring(0, j) + randChar() + b.substring(j); break; // insertion
                        case 3: b = b.substring(0, j) + b.substring(j + 1); break; // deletion
                    }
                }
            }
            if (r.nextInt(mutationFactor) == 0) { // last char
                switch (r.nextInt(4)) {
                    case 0: break; // can't do transposition here
                    case 1: b = b.substring(0, b.length() - 1) + "_"; break; // substitution
                    case 2: b = b.substring(0, b.length()) + "_"; break; // insertion
                    case 3: b = b.substring(0, b.length() - 1); break; // deletion
                }
            }
            return b;
        }
        
        /**
         * Generates pseudo-random ASCII characters.
         * 
         * @return  a pseudo-random ASCII character
         */
        private char randChar() {
            return Long.toString(Math.abs(r.nextLong()), 36).charAt(0);
        }
    }

}
