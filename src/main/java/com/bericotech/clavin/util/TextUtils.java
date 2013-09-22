package com.bericotech.clavin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

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
 * TextUtils.java
 * 
 *###################################################################*/

/**
 * Various utility methods for processing text.
 */
public class TextUtils {
    
    /**
     * Wrapper for calling Apache Commons IO toString(BufferedReader)
     * on a File, essentially providing a File.toString() method for
     * the contents of a text file.
     * 
     * @param file          File to be string-ified
     * @return              String representing contents of file
     * @throws IOException
     */
    public static String fileToString(File file) throws IOException {
        return IOUtils.toString(new BufferedReader(new FileReader(file)));
    }
}