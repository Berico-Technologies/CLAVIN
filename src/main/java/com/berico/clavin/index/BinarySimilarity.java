package com.berico.clavin.index;

import org.apache.lucene.search.similarities.DefaultSimilarity;

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
 * BinarySimilarity.java
 * 
 *###################################################################*/

/**
 * Turns TF (term frequency) into a binary (yes/no) proposition in
 * calculating Lucene relevance score.
 * 
 */
public class BinarySimilarity extends DefaultSimilarity {
	
	/**
	 * Simple default constructor for {@link BinarySimilarity}.
	 */
    public BinarySimilarity() {}
    
    /**
     * Ignores multiple appearance of the query term in the index
     * document field, effectively making TF (term frequency) a
     * yes/no proposition (i.e., zero is still zero, but you don't
     * get extra points for a query term being found multiple times in
     * an index document field).
     * 
     * @param freq		floating-point number being converted to 1.0 or 0.0
     */
    @Override
    public float tf(float freq) {
        if (freq > 0)
        	return 1.0f;
        else return freq;
    }

}
