package com.bericotech.clavin.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

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
 * TextUtilsTest.java
 * 
 *###################################################################*/

/**
 * Tests for text processing methods.
 * 
 */
public class TextUtilsTest {

	/**
	 * Ensures the contents extracted from a file are an exact match
	 * with the expected String.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFileToString() throws IOException {
		File inputFile = new File("src/test/resources/sample-docs/SampleText.txt");
		String expectedString = "Line1 word2\nLine2 word2 word3\nLine3";
		String fileString = TextUtils.fileToString(inputFile);
		assertEquals("wrong output from fileToString", expectedString, fileString);
	}

}
