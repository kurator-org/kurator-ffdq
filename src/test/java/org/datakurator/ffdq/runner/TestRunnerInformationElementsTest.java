/**
 * TestRunnerInformationElementsTest.java
 *
 * Copyright 2026 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datakurator.ffdq.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;

import org.datakurator.ffdq.model.InformationElement;
import org.junit.Test;

/**
 * The information elements TestRunner checks method parameters against: hasInformationElement, or the
 * information elements acted upon and consulted (as in the BDQ standard's bdqtest RDF).
 */
public class TestRunnerInformationElementsTest {

    private static final URI A = URI.create("http://rs.tdwg.org/dwc/terms/countryCode");
    private static final URI B = URI.create("http://rs.tdwg.org/dwc/terms/country");

    @Test
    public void testActedUponAndConsultedCombined() {
        InformationElement ie = TestRunner.informationElementsOf(null, new InformationElement(A), new InformationElement(Arrays.asList(A, B)));
        assertEquals(Arrays.asList(A, B), ie.getComposedOf());
    }

    @Test
    public void testNoneGivesEmpty() {
        assertTrue(TestRunner.informationElementsOf(null, null, null).getComposedOf().isEmpty());
    }
}
