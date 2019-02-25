package com.bericotech.clavin.index;

import org.apache.lucene.analysis.util.CharTokenizer;

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
 * WhitespaceLowerCaseTokenizer.java
 * 
 *###################################################################*/

/**
 * LowerCaseTokenizer performs the function of WhitespaceTokenizer
 * and LowerCaseFilter together. It divides text at whitespace and
 * converts them to lower case. While it is functionally equivalent to
 * a combination of WhitespaceTokenizer and LowerCaseFilter, there is a
 * performance advantage to doing the two tasks at once, hence this
 * (redundant) implementation.
 * 
 */
public class WhitespaceLowerCaseTokenizer extends CharTokenizer {
    
    /**
     * Call the "super" constructor.
     *
     */
    public WhitespaceLowerCaseTokenizer() {
        super();
    }

    /** Collects only characters which do not satisfy
     * {@link Character#isWhitespace(int)}.
     * 
     * @param c     char being processed
     */
    @Override
    protected boolean isTokenChar(int c) {
        return !Character.isWhitespace(c);
    }

    /** Converts char to lower case
     * {@link Character#toLowerCase(int)}.
     * 
     * @param c     char being processed
     */
    @Override
    protected int normalize(int c) {
        return Character.toLowerCase(c);
    }
}
