package com.bericotech.clavin.resolver.multipart;



import static org.junit.Assert.*;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import com.bericotech.clavin.resolver.ResolvedLocation;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

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
 * MultiLevelMultipartLocationResolverTest.java
 *
 *###################################################################*/

/**
 * Tests mapping of city and N-level administrative divisions to a
 * single location.
 */
@RunWith(Parameterized.class)
public class MultiLevelMultipartLocationResolverTest {
    // expected geonameID numbers for given location names
    private static final long UNITED_STATES = 6252001L;
        private static final long MASSACHUSETTS = 6254926L;
            private static final long BOSTON_MA = 4930956L;
            private static final long HAVERHILL_MA = 4939085L;
            private static final long WORCESTER_MA = 4956184L;
            private static final long SPRINGFIELD_MA = 4951788L;
        private static final long MISSOURI = 4398678L;
            private static final long SPRINGFIELD_MO = 4409896L;
        private static final long ILLINOIS = 4896861L;
            private static final long SPRINGFIELD_IL = 4250542L;
        private static final long VIRGINIA = 6254928L;
            private static final long FAIRFAX_VA = 4758023L;
            private static final long FAIRFAX_COUNTY_VA = 4758041L;
            private static final long RESTON_VA = 4781530L;
            private static final long SPRINGFIELD_VA = 4787117L;
        private static final long OREGON = 5744337L;
            private static final long SPRINGFIELD_OR = 5754005L;
        private static final long DELAWARE = 4142224L;
            private static final long BETHEL_DE_US = 4141443L;
    private static final long GERMANY = 2921044L;
        private static final long NR_WESTPHALIA = 2861876L; // state of North Rhine-Westphalia
            private static final long BETHEL_GER = 2949766L;
    private static final long UNITED_KINGDOM = 2635167L;
        private static final long ENGLAND = 6269131L;
            private static final long LONDON_UK_41 = 2643741L;
            private static final long LONDON_UK_43 = 2643743L;
            private static final long HAVERHILL_UK = 2647310L;
            private static final long WORCESTER_UK = 2633563L;
        private static final long OXFORDSHIRE = 2640726L;
            private static final long OXFORD_UK = 2640729L;
    private static final long CANADA = 6251999L;
        private static final long ONTARIO = 6093943L;
            private static final long LONDON_ON = 6058560L;
    private static final long PHILIPPINES = 1694008L;
        private static final long DAVAO = 7521309L;
        private static final long DAVAO_ORIENTAL = 1715342L;
            private static final long BOSTON_PH = 1723862L;
    private static final long SWITZERLAND = 2658434L;
        private static final long ZURICH_CANTON = 2657895L;
            private static final long ZURICH_CITY = 2657896L;
    private static final long AUSTRALIA = 2077456L;
        private static final long ASHMORE_AND_CARTIER_ISLANDS = 2077507L;
    private static final long NETHERLANDS_ANTILLES = 8505032L;
    private static final long CLIPPERTON_ISLAND = 4020092L;


    @Parameters(name="{index}: multipartResolve({0} {1})")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            { Arrays.asList("Springfield", "Massachusetts", "United States"), SPRINGFIELD_MA },
            { Arrays.asList("Springfield", "Illinois", "United States"), SPRINGFIELD_IL },
            { Arrays.asList("Springfield", "Missouri", "United States"), SPRINGFIELD_MO },
            { Arrays.asList("Springfield", "Virginia", "United States"), SPRINGFIELD_VA },
            { Arrays.asList("Springfield", "Oregon", "United States"), SPRINGFIELD_OR },
            { Arrays.asList("Reston", "Virginia", "United States"), RESTON_VA },
            { Arrays.asList("Reston", "Fairfax", "Virginia", "United States"), RESTON_VA },
            { Arrays.asList("Reston", "Fairfax County", "VA", "US"), RESTON_VA },
            { Arrays.asList("Reston", "Fairfax"), RESTON_VA },
            { Arrays.asList("Fairfax", "Virginia"), FAIRFAX_VA },
            { Arrays.asList("Fairfax County", "VA"), FAIRFAX_COUNTY_VA },
            { Arrays.asList("Reston", "Virginia", "CA"), null },
            { Arrays.asList("Boston", "Massachusetts", "United States"), BOSTON_MA },
            { Arrays.asList("Haverhill", "Massachusetts", "United States"), HAVERHILL_MA },
            { Arrays.asList("Worcester", "Massachusetts", "United States"), WORCESTER_MA },
            { Arrays.asList("Haverhill", "England", "United Kingdom"), HAVERHILL_UK },
            { Arrays.asList("Worcester", "England", "United Kingdom"), WORCESTER_UK },
            { Arrays.asList("Oxford", "England", "United Kingdom"), OXFORD_UK },
            { Arrays.asList("Oxford", "Oxfordshire", "United Kingdom"), OXFORD_UK },
            { Arrays.asList("London", "England", "United Kingdom"), LONDON_UK_43 },
            { Arrays.asList("London", "Ontario", "Canada"), LONDON_ON },
            { Arrays.asList("Boston", "Davao", "Philippines"), BOSTON_PH },
            { Arrays.asList("Boston", "Davao Oriental", "Philippines"), BOSTON_PH },
            { Arrays.asList("Bethel", "Delaware", "United States"), BETHEL_DE_US },
            { Arrays.asList("Bethel", "North Rhine-Westphalia", "Germany"), BETHEL_GER },
            { Arrays.asList("Bethel", "DE", "US"), BETHEL_DE_US },
            { Arrays.asList("Bethel", "NRW", "DE"), BETHEL_GER },
            { Arrays.asList("London", "ENG", "UK"), LONDON_UK_43 },
            { Arrays.asList("London", "ENG", "GB"), LONDON_UK_43 },
            { Arrays.asList("London", "ON", "CA"), LONDON_ON },
            { Arrays.asList("London", "ON", "CAN"), LONDON_ON },
            { Arrays.asList("Zurich", "ZH", "CH"), ZURICH_CITY },
            { Arrays.asList("", "ENG", "UK"), ENGLAND },
            { Arrays.asList("London", "", "UK"), LONDON_UK_43 },
            { Arrays.asList("London", "", ""), LONDON_UK_43 },
            { Arrays.asList("", "ENG", ""), ENGLAND },
            { Arrays.asList("", "", "UK"), UNITED_KINGDOM },
            { Arrays.asList("Ashmore and Cartier Islands"), ASHMORE_AND_CARTIER_ISLANDS },
            { Arrays.asList("Ashmore and Cartier Islands", "Australia"), ASHMORE_AND_CARTIER_ISLANDS },
            { Arrays.asList("Netherlands Antilles"), NETHERLANDS_ANTILLES },
            { Arrays.asList("Parish of Saint Thomas", "Ashmore and Cartier Islands"), null},
            { Arrays.asList("Clipperton Island"), CLIPPERTON_ISLAND },
            { Arrays.asList("Clipperton Island", "France"), CLIPPERTON_ISLAND },
            { Arrays.asList("", "", ""), null },
            { Collections.EMPTY_LIST, null }
        });
    }

    private static MultipartLocationResolver resolver;

    @BeforeClass
    public static void setUpClass() throws ClavinException {
        resolver = new MultipartLocationResolver(new LuceneGazetteer(new File("./IndexDirectory")));
    }

    private final String[] parts;
    private final Integer expectedId;

    public MultiLevelMultipartLocationResolverTest(List<String> parts, Integer expectedId) {
        this.parts = parts.toArray(new String[0]);
        this.expectedId = expectedId;
    }

    @Test
    public void testResolveMultipartLocation() throws ClavinException {
        ResolvedLocation loc = resolver.resolveLocation(false, parts);
        if (expectedId == null) {
            assertNull("expected null location", loc);
        } else {
            assertNotNull("expected non-null location", loc);
            GeoName geo = loc.getGeoname();
            assertEquals(String.format("Incorrect Location [%s]", geo), expectedId.intValue(), geo.getGeonameID());
        }
    }
}
