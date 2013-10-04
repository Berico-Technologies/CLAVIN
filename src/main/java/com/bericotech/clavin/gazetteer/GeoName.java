package com.bericotech.clavin.gazetteer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
 * GeoName.java
 * 
 *###################################################################*/

/**
 * Data-rich representation of a named location, based on entries in
 * the GeoNames gazetteer.
 * 
 * TODO: link administrative subdivision code fields to the GeoName
 *       records they reference
 * 
 */
public class GeoName {
    
    // id of record in geonames database
    public final int geonameID;
    
    // name of geographical point (utf8)
    public final String name;
    
    // name of geographical point in plain ascii characters
    public final String asciiName;
    
    // list of alternate names for location
    public final List<String> alternateNames;
    
    // latitude in decimal degrees
    public final double latitude;
    
    // longitude in decimal degrees
    public final double longitude;
    
    // major feature category
    // (see http://www.geonames.org/export/codes.html)
    public final FeatureClass featureClass;
    
    // http://www.geonames.org/export/codes.html
    public final FeatureCode featureCode;
    
    // ISO-3166 2-letter country code
    public final CountryCode primaryCountryCode;

    // associated name with country code
    public String getPrimaryCountryName(){
        return  primaryCountryCode.name;
    }

    // list of alternate ISO-3166 2-letter country codes
    public final List<CountryCode> alternateCountryCodes;
    
    /*  TODO: refactor the 4 fields below to link to the GeoName
     *        object that they refer to
     */
    
    // Mostly FIPS codes. ISO codes are used for US, CH, BE and ME. UK
    // and Greece are using an additional level between country and
    // FIPS code.
    public final String admin1Code;
    
    // code for the second administrative division
    // (e.g., a county in the US)
    public final String admin2Code;
    
    // code for third level administrative division
    public final String admin3Code;
    
    // code for fourth level administrative division
    public final String admin4Code;
    
    // total number of inhabitants
    public final long population;
    
    // in meters
    public final int elevation;
    
    // digital elevation model, srtm3 or gtopo30, average elevation of
    // 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters,
    // integer. srtm processed by cgiar/ciat.
    public final int digitalElevationModel;
    
    // timezone for geographical point
    public final TimeZone timezone;
    
    // date of last modification in GeoNames database
    public final Date modificationDate;
    
    // sentinel value used in place of null when numeric value in
    // GeoNames record is not provided (see: geonameID, latitude,
    // longitude, population, elevation, digitalElevationModel)
    public static final int OUT_OF_BOUNDS = -9999999;
    
    /**
     * Sole constructor for {@link GeoName} class.
     * 
     * Encapsulates a gazetteer record from the GeoNames database.
     * 
     * @param geonameID                 unique identifier
     * @param name                      name of this location
     * @param asciiName                 plain text version of name
     * @param alternateNames            list of alternate names, if any
     * @param latitude                  lat coord
     * @param longitude                 lon coord
     * @param featureClass              general type of feature (e.g., "Populated place")
     * @param featureCode               specific type of feature (e.g., "capital of a political entity")
     * @param primaryCountryCode        ISO country code
     * @param alternateCountryCodes     list of alternate country codes, if any (i.e., disputed territories)
     * @param admin1Code                FIPS code for first-level administrative subdivision (e.g., state or province)
     * @param admin2Code                second-level administrative subdivision (e.g., county)
     * @param admin3Code                third-level administrative subdivision
     * @param admin4Code                fourth-level administrative subdivision
     * @param population                number of inhabitants
     * @param elevation                 elevation in meters
     * @param digitalElevationModel     another way to measure elevation
     * @param timezone                  timezone for this location
     * @param modificationDate          date of last modification for the GeoNames record
     */
    public GeoName(
            int geonameID,
            String name,
            String asciiName,
            List<String> alternateNames,
            Double latitude,
            Double longitude,
            FeatureClass featureClass,
            FeatureCode featureCode,
            CountryCode primaryCountryCode,
            List<CountryCode> alternateCountryCodes,
            String admin1Code,
            String admin2Code,
            String admin3Code,
            String admin4Code,
            Long population,
            Integer elevation,
            Integer digitalElevationModel,
            TimeZone timezone,
            Date modificationDate) {
        this.geonameID = geonameID;
        this.name = name;
        this.asciiName = asciiName;
        this.alternateNames = alternateNames;
        this.latitude = latitude;
        this.longitude = longitude;
        this.featureClass = featureClass;
        this.featureCode = featureCode;
        this.primaryCountryCode = primaryCountryCode;
        this.alternateCountryCodes = alternateCountryCodes;
        this.admin1Code = admin1Code;
        this.admin2Code = admin2Code;
        this.admin3Code = admin3Code;
        this.admin4Code = admin4Code;
        this.population = population;
        this.elevation = elevation;
        this.digitalElevationModel = digitalElevationModel;
        this.timezone = timezone;
        this.modificationDate = modificationDate;
    }
    
    /**
     * Builds a {@link GeoName} object based on a single gazetteer
     * record in the GeoNames geographical database.
     * 
     * @param inputLine     single line of tab-delimited text representing one record from the GeoNames gazetteer
     * @return              new GeoName object
     */
    public static GeoName parseFromGeoNamesRecord(String inputLine) {
        
        // GeoNames gazetteer entries are tab-delimited
        String[] tokens = inputLine.split("\t");
        
        // initialize each field with the corresponding token
        int geonameID = Integer.parseInt(tokens[0]);
        String name = tokens[1];
        String asciiName = tokens[2];
        
        List<String> alternateNames;
        if (tokens[3].length() > 0) {
            // better to pass empty array than array containing empty String ""
            alternateNames = Arrays.asList(tokens[3].split(","));
        } else alternateNames = new ArrayList<String>();
        
        double latitude;
        try {
            latitude = Double.parseDouble(tokens[4]);
        } catch (NumberFormatException e) {
            latitude = OUT_OF_BOUNDS;
        }
        
        double longitude;
        try {
            longitude = Double.parseDouble(tokens[5]);
        } catch (NumberFormatException e) {
            longitude = OUT_OF_BOUNDS;
        }
        
        FeatureClass featureClass;
        if (tokens[6].length() > 0) {
            featureClass = FeatureClass.valueOf(tokens[6]);
        } else featureClass = FeatureClass.NULL; // not available
        
        FeatureCode featureCode;
        if (tokens[7].length() > 0) {
            featureCode = FeatureCode.valueOf(tokens[7]);
        } else featureCode = FeatureCode.NULL; // not available
        
        CountryCode primaryCountryCode;
        if (tokens[8].length() > 0) {
            primaryCountryCode = CountryCode.valueOf(tokens[8]);
        } else primaryCountryCode = CountryCode.NULL; // No Man's Land
        
        List<CountryCode> alternateCountryCodes = new ArrayList<CountryCode>();
        if (tokens[9].length() > 0) {
            // don't pass list only containing empty String ""
            for (String code : tokens[9].split(",")) {
                if (code.length() > 0) // check for malformed data
                    alternateCountryCodes.add(CountryCode.valueOf(code));
            }
        }
        
        String admin1Code = tokens[10];
        String admin2Code = tokens[11];
        
        String admin3Code;
        String admin4Code;
        long population;
        int elevation;
        int digitalElevationModel;
        TimeZone timezone;
        Date modificationDate;
        
        // check for dirty data...
        if (tokens.length < 19) {
            // GeoNames record format is corrupted, don't trust any
            // data after this point
            admin3Code = "";
            admin4Code = "";
            population = OUT_OF_BOUNDS;
            elevation = OUT_OF_BOUNDS;
            digitalElevationModel = OUT_OF_BOUNDS;
            timezone = null;
            modificationDate = new Date(0);
        } else { // everything looks ok, soldiering on...
            admin3Code = tokens[12];
            admin4Code = tokens[13];
            try {
                population = Long.parseLong(tokens[14]);
            } catch (NumberFormatException e) {
                population = OUT_OF_BOUNDS;
            }
            try {
                elevation = Integer.parseInt(tokens[15]);
            } catch (NumberFormatException e) {
                elevation = OUT_OF_BOUNDS;
            }
            try {
                digitalElevationModel = Integer.parseInt(tokens[16]);
            } catch (NumberFormatException e) {
                digitalElevationModel = OUT_OF_BOUNDS;
            }
            timezone = TimeZone.getTimeZone(tokens[17]);
            try {
                modificationDate = new SimpleDateFormat("yyyy-MM-dd").parse(tokens[18]);
            } catch (ParseException e) {
                modificationDate = new Date(0);
            }
        }
        
        return new GeoName(geonameID, name, asciiName, alternateNames,
                latitude, longitude, featureClass, featureCode,
                primaryCountryCode, alternateCountryCodes, admin1Code,
                admin2Code, admin3Code, admin4Code, population,
                elevation, digitalElevationModel, timezone,
                modificationDate);
    }
    
    /**
     * For pretty-printing.
     * 
     */
    @Override
    public String toString() {
        return name + " (" + getPrimaryCountryName() + ", " + admin1Code + ")" + " [pop: " + population + "] <" + geonameID + ">";
    }
}
