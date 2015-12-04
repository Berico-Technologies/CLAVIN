package com.bericotech.clavin.resolver.multipart;



import static org.junit.Assert.*;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import com.bericotech.clavin.resolver.ResolvedLocation;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
 * MultipartLocationResolverTest.java
 *
 *###################################################################*/

/**
 * Tests the mapping of location names into
 * {@link com.bericotech.clavin.resolver.ResolvedLocation} objects as performed by
 * {@link com.bericotech.clavin.resolver.multipart.MultipartLocationResolver#resolveMultipartLocation(MultipartLocationName, boolean)}.
 */
@RunWith(Parameterized.class)
public class MultipartLocationResolverTest {
    // expected geonameID numbers for given location names
    private static final long UNITED_STATES = 2147483648L;
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


    @Parameters(name="{index}: multipartResolve({0} {1} {2})")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            { "Springfield", "Massachusetts", "United States", new Long[] { SPRINGFIELD_MA }, MASSACHUSETTS, UNITED_STATES },
            { "Springfield", "Illinois", "United States", new Long[] { SPRINGFIELD_IL }, ILLINOIS, UNITED_STATES },
            { "Springfield", "Missouri", "United States", new Long[] { SPRINGFIELD_MO }, MISSOURI, UNITED_STATES },
            { "Springfield", "Virginia", "United States", new Long[] { SPRINGFIELD_VA }, VIRGINIA, UNITED_STATES },
            { "Springfield", "Oregon", "United States", new Long[] { SPRINGFIELD_OR }, OREGON, UNITED_STATES },
            { "Reston", "Virginia", "United States", new Long[] { RESTON_VA }, VIRGINIA, UNITED_STATES },
            { "Reston", "Fairfax County", "United States", new Long[] { RESTON_VA }, FAIRFAX_COUNTY_VA, UNITED_STATES },
            { "Reston", "Fairfax", "US", new Long[] { RESTON_VA }, FAIRFAX_COUNTY_VA, UNITED_STATES },
            { "Boston", "Massachusetts", "United States", new Long[] { BOSTON_MA }, MASSACHUSETTS, UNITED_STATES },
            { "Haverhill", "Massachusetts", "United States", new Long[] { HAVERHILL_MA }, MASSACHUSETTS, UNITED_STATES },
            { "Worcester", "Massachusetts", "United States", new Long[] { WORCESTER_MA }, MASSACHUSETTS, UNITED_STATES },
            { "Haverhill", "England", "United Kingdom", new Long[] { HAVERHILL_UK }, ENGLAND, UNITED_KINGDOM },
            { "Worcester", "England", "United Kingdom", new Long[] { WORCESTER_UK }, ENGLAND, UNITED_KINGDOM },
            { "Oxford", "England", "United Kingdom", new Long[] { OXFORD_UK }, ENGLAND, UNITED_KINGDOM },
            { "Oxford", "Oxfordshire", "United Kingdom", new Long[] { OXFORD_UK }, OXFORDSHIRE, UNITED_KINGDOM },
            { "London", "England", "United Kingdom", new Long[] { LONDON_UK_43 }, ENGLAND, UNITED_KINGDOM },
            { "London", "Ontario", "Canada", new Long[] { LONDON_ON }, ONTARIO, CANADA },
            { "Boston", "Davao", "Philippines", new Long[] { BOSTON_PH }, DAVAO, PHILIPPINES },
            { "Boston", "Davao Oriental", "Philippines", new Long[] { BOSTON_PH }, DAVAO_ORIENTAL, PHILIPPINES },
            { "Bethel", "Delaware", "United States", new Long[] { BETHEL_DE_US }, DELAWARE, UNITED_STATES },
            { "Bethel", "North Rhine-Westphalia", "Germany", new Long[] { BETHEL_GER }, NR_WESTPHALIA, GERMANY },
            { "Bethel", "DE", "US", new Long[] { BETHEL_DE_US }, DELAWARE, UNITED_STATES },
            { "Bethel", "NRW", "DE", new Long[] { BETHEL_GER }, NR_WESTPHALIA, GERMANY },
            { "London", "ENG", "UK", new Long[] { LONDON_UK_43 }, ENGLAND, UNITED_KINGDOM },
            { "London", "ENG", "GB", new Long[] { LONDON_UK_43 }, ENGLAND, UNITED_KINGDOM },
            { "London", "ON", "CA", new Long[] { LONDON_ON }, ONTARIO, CANADA },
            { "London", "ON", "CAN", new Long[] { LONDON_ON }, ONTARIO, CANADA },
            { "Zurich", "ZH", "CH", new Long[] { ZURICH_CITY }, ZURICH_CANTON, SWITZERLAND },
            { "", "ENG", "UK", null, ENGLAND, UNITED_KINGDOM },
            { "London", "", "UK", new Long[] { LONDON_UK_43 }, null, UNITED_KINGDOM },
            { "London", "", "", new Long[] { LONDON_UK_43 }, null, null },
            { "", "ENG", "", null, ENGLAND, null },
            { "", "", "UK", null, null, UNITED_KINGDOM },
            { "", "", "", null, null, null },
            { null, null, null, null, null, null }
        });
    }

    private static MultipartLocationResolver resolver;

    @BeforeClass
    public static void setUpClass() throws ClavinException {
        resolver = new MultipartLocationResolver(new LuceneGazetteer(new File("./IndexDirectory")));
    }

    private final String city;
    private final String state;
    private final String country;
    private final Set<Long> cityIds;
    private final Long stateId;
    private final Long countryId;

    public MultipartLocationResolverTest(String city, String state, String country,
            Long[] ctyIds, Long stateId, Long countryId) {
        this.city = city;
        this.state = state;
        this.country = country;
        this.cityIds = new HashSet<Long>();
        if (ctyIds != null && ctyIds.length > 0) {
            this.cityIds.addAll(Arrays.asList(ctyIds));
        }
        this.stateId = stateId;
        this.countryId = countryId;
    }

    @Test
    public void testResolveMultipartLocation() throws ClavinException {
        MultipartLocationName mpLoc = new MultipartLocationName(city, state, country);
        ResolvedMultipartLocation rLoc = resolver.resolveMultipartLocation(mpLoc, false);
        verifyCity(rLoc.getCity());
        verifyLocation("state", stateId, rLoc.getState());
        verifyLocation("country", countryId, rLoc.getCountry());
    }

    private void verifyCity(final ResolvedLocation loc) {
        if (cityIds.isEmpty()) {
            assertNull("expected null city", loc);
        } else {
            assertNotNull("expected non-null city", loc);
            GeoName geo = loc.getGeoname();
            assertTrue(String.format("Incorrect city [%s]; expected one of %s", geo, cityIds), cityIds.contains(geo.getGeonameID()));
        }
    }

    private void verifyLocation(final String label, final Long expected, final ResolvedLocation loc) {
        if (expected == null) {
            assertNull(String.format("expected null %s", label), loc);
        } else {
            assertNotNull(String.format("expected non-null %s", label), loc);
            GeoName geo = loc.getGeoname();
            assertEquals(String.format("Incorrect %s [%s]", label, geo), expected.intValue(), geo.getGeonameID());
        }
    }
}
