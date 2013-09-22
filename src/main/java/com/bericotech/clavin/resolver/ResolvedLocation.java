package com.bericotech.clavin.resolver;

import com.bericotech.clavin.extractor.LocationOccurrence;
import org.apache.lucene.document.Document;

import com.bericotech.clavin.gazetteer.GeoName;

import static com.bericotech.clavin.util.DamerauLevenshtein.damerauLevenshteinDistanceCaseInsensitive;

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
 * ResolvedLocation.java
 * 
 *###################################################################*/

/**
 * Object produced by resolving a location name against gazetteer
 * records.
 * 
 * Encapsulates a {@link GeoName} object representing the best match
 * between a given location name and gazetter record, along with some
 * information about the geographic entity resolution process.
 *
 */
public class ResolvedLocation {
    // geographic entity resolved from location name
    public GeoName geoname;
    
    // original location name extracted from text
    public LocationOccurrence location;
    
    // name from gazetteer record that the inputName was matched against
    public String matchedName;
    
    // whether fuzzy matching was used
    public boolean fuzzy;
    
    // confidence score for resolution
    public float confidence;
    
    /**
     * Builds a {@link ResolvedLocation} from a document retrieved from
     * the Lucene index representing the geographic entity resolved
     * from a location name.
     * 
     * @param luceneDoc     document from Lucene index representing a gazetteer record
     */
    public ResolvedLocation(Document luceneDoc, LocationOccurrence location, boolean fuzzy) {
        // instantiate a GeoName object from the gazetteer record
        this.geoname = GeoName.parseFromGeoNamesRecord(luceneDoc.get("geoname"));
        
        this.location = location;
        
        // get the name in the Lucene document matched to the given
        // location name extracted from the text
        this.matchedName = luceneDoc.get("indexName");
        
        this.fuzzy = fuzzy;
        
        // for fuzzy matches, confidence is based on the edit distance
        // between the given location name and the matched name
        if (fuzzy)
            this.confidence = 1 / (damerauLevenshteinDistanceCaseInsensitive(location.name, matchedName) + (float)0.5);
        else this.confidence = 1; // exact String match
        /// TODO: fix this confidence score... it doesn't fully make sense
    }
    
    /**
     * Tests equivalence between {@link ResolvedLocation} objects.
     * 
     * @param obj   the other object being compared against
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        
        // only a ResolvedLocation can equal a ResolvedLocation
        if (this.getClass() != obj.getClass()) return false;
        
        // cast the other object into a ResolvedLocation, now that we
        // know that it is one
        ResolvedLocation other = (ResolvedLocation)obj;
        
        // as long as the geonameIDs are the same, we'll treat these
        // ResolvedLocations as equal since they point to the same
        // geographic entity (even if the circumstances of the entity
        // resolution process differed)
        return (this.geoname.geonameID == other.geoname.geonameID);
    }
    
    /**
     * For pretty-printing.
     * 
     */
    @Override
    public String toString() {
        return "Resolved \"" + location.name + "\" as: \"" + matchedName + "\" {" + geoname + "}, position:" + location.position + ", confidence: " + confidence + ", fuzzy: " + fuzzy;
    }
}
