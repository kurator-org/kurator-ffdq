/**
 * FFDQModelQueriesTest.java
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
package org.datakurator.ffdq.rdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.context.Amendment;
import org.datakurator.ffdq.model.context.DataQualityNeed;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.DataQualityMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;

/**
 * Tests of the FFDQModel queries TestRunner uses to find Tests and their Methods, both in the form of the
 * BDQ standard's bdqtest RDF (a Test under its own IRI, with dcterms:hasVersion, and Methods referring to
 * it) and in the form kurator-ffdq writes for libraries (a version of a Test, with dcterms:isVersionOf,
 * and Tests defined only in a library, identified by urn:uuid).
 */
public class FFDQModelQueriesTest {

    private static final String PREFIXES =
            "@prefix bdqffdq: <https://rs.tdwg.org/bdqffdq/terms/> .\n"
            + "@prefix bdqtest: <https://rs.tdwg.org/bdqtest/terms/> .\n"
            + "@prefix bdqcrit: <https://rs.tdwg.org/bdqcrit/terms/> .\n"
            + "@prefix bdqdim: <https://rs.tdwg.org/bdqdim/terms/> .\n"
            + "@prefix bdqenh: <https://rs.tdwg.org/bdqenh/terms/> .\n"
            + "@prefix dcterms: <http://purl.org/dc/terms/> .\n"
            + "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n"
            + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n"
            + "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n";

    /** A Validation and an Amendment in the form of the BDQ standard's bdqtest RDF (dist/bdqtest.ttl). */
    private static final String BDQTEST_FORM = PREFIXES
            + "bdqtest:0493bcfb-652e-4d17-815b-b0cce0742fbe a bdqffdq:Validation ;\n"
            + "  rdfs:label \"VALIDATION_COUNTRYCODE_STANDARD\" ;\n"
            + "  dcterms:hasVersion <https://rs.tdwg.org/bdqtest/terms/version/0493bcfb-652e-4d17-815b-b0cce0742fbe-2025-03-07> ;\n"
            + "  dcterms:modified \"2025-03-07\"^^xsd:date ;\n"
            + "  owl:sameAs <urn:uuid:0493bcfb-652e-4d17-815b-b0cce0742fbe> ;\n"
            + "  bdqffdq:hasActedUponInformationElement <https://rs.tdwg.org/bdqtest/informationelement/c3620a97> ;\n"
            + "  bdqffdq:hasCriterion bdqcrit:Standard ;\n"
            + "  bdqffdq:hasDataQualityDimension bdqdim:Conformance ;\n"
            + "  bdqffdq:hasResourceType bdqffdq:SingleRecord .\n"
            + "<https://rs.tdwg.org/bdqtest/informationelement/c3620a97> a bdqffdq:ActedUpon ;\n"
            + "  rdfs:label \"Information Element ActedUpon dwc:countryCode\" ;\n"
            + "  bdqffdq:composedOf <http://rs.tdwg.org/dwc/terms/countryCode> .\n"
            + "bdqcrit:Standard a bdqffdq:Criterion ; rdfs:label \"Standard\" .\n"
            + "bdqffdq:SingleRecord a bdqffdq:ResourceType ; rdfs:label \"SingleRecord\" .\n"
            + "<https://rs.tdwg.org/bdqtest/method/02f5a440> a bdqffdq:ValidationMethod ;\n"
            + "  rdfs:label \"ValidationMethod: VALIDATION_COUNTRYCODE_STANDARD\" ;\n"
            + "  bdqffdq:forValidation bdqtest:0493bcfb-652e-4d17-815b-b0cce0742fbe ;\n"
            + "  bdqffdq:hasSpecification <https://rs.tdwg.org/bdqtest/specification/01b96157> .\n"
            + "<https://rs.tdwg.org/bdqtest/specification/01b96157> a bdqffdq:Specification ;\n"
            + "  rdfs:label \"Specification for: VALIDATION_COUNTRYCODE_STANDARD\" .\n"
            + "bdqtest:7e0c0418-fe16-4a39-98bd-80e19d95b9d1 a bdqffdq:Amendment ;\n"
            + "  rdfs:label \"AMENDMENT_COUNTRYCODE_STANDARDIZED\" ;\n"
            + "  dcterms:hasVersion <https://rs.tdwg.org/bdqtest/terms/version/7e0c0418-fe16-4a39-98bd-80e19d95b9d1-2025-03-07> ;\n"
            + "  bdqffdq:hasEnhancement bdqenh:Standardized ;\n"
            + "  bdqffdq:hasResourceType bdqffdq:SingleRecord .\n"
            + "<https://rs.tdwg.org/bdqtest/method/aaaa> a bdqffdq:AmendmentMethod ;\n"
            + "  bdqffdq:forAmendment bdqtest:7e0c0418-fe16-4a39-98bd-80e19d95b9d1 ;\n"
            + "  bdqffdq:hasSpecification <https://rs.tdwg.org/bdqtest/specification/bbbb> .\n"
            + "<https://rs.tdwg.org/bdqtest/specification/bbbb> a bdqffdq:Specification .\n";

    /** A Validation in the form kurator-ffdq writes for libraries: a version of the Test, and a local Test. */
    private static final String LIBRARY_FORM = PREFIXES
            + "bdqtest:66269bdd-9271-4e76-b25c-7ab81eebe1d8-2025-03-07 a bdqffdq:Validation ;\n"
            + "  dcterms:isVersionOf bdqtest:66269bdd-9271-4e76-b25c-7ab81eebe1d8 ;\n"
            + "  bdqffdq:hasActedUponInformationElement <urn:uuid:e4f1fc86> ;\n"
            + "  bdqffdq:hasResourceType bdqffdq:SingleRecord ;\n"
            + "  bdqffdq:hasCriterion bdqcrit:Standard ;\n"
            + "  rdfs:label \"VALIDATION_DATEIDENTIFIED_STANDARD\" ;\n"
            + "  dcterms:issued \"2025-03-07\"^^xsd:date .\n"
            + "<urn:uuid:e4f1fc86> a bdqffdq:ActedUpon ; bdqffdq:composedOf <http://rs.tdwg.org/dwc/terms/dateIdentified> .\n"
            + "<urn:uuid:4c6a5522> a bdqffdq:ValidationMethod ;\n"
            + "  bdqffdq:forValidation bdqtest:66269bdd-9271-4e76-b25c-7ab81eebe1d8-2025-03-07 ;\n"
            + "  bdqffdq:hasSpecification <urn:uuid:89f8b2ea> .\n"
            + "<urn:uuid:89f8b2ea> a bdqffdq:Specification ; rdfs:label \"Specification for: VALIDATION_DATEIDENTIFIED_STANDARD\" .\n"
            + "<urn:uuid:391ca46d-3842-4a18-970c-0434cbc17f07> a bdqffdq:Validation ;\n"
            + "  rdfs:label \"VALIDATION_LOCAL\" ;\n"
            + "  bdqffdq:hasResourceType bdqffdq:SingleRecord .\n"
            + "<urn:uuid:5b7e9a0e> a bdqffdq:ValidationMethod ;\n"
            + "  bdqffdq:forValidation <urn:uuid:391ca46d-3842-4a18-970c-0434cbc17f07> ;\n"
            + "  bdqffdq:hasSpecification <urn:uuid:d5b3c3e1> .\n"
            + "<urn:uuid:d5b3c3e1> a bdqffdq:Specification .\n";

    private static FFDQModel load(String turtle) throws Exception {
        FFDQModel model = new FFDQModel();
        model.load(new ByteArrayInputStream(turtle.getBytes(StandardCharsets.UTF_8)), RDFFormat.TURTLE);
        return model;
    }

    @Test
    public void testReadBdqtestForm() throws Exception {
        FFDQModel model = load(BDQTEST_FORM);

        Map<String, DataQualityNeed> tests = model.findAllSingleRecordTests("ver");
        assertEquals(2, tests.size());
        DataQualityNeed validation = tests.get(Namespace.BDQTEST + "0493bcfb-652e-4d17-815b-b0cce0742fbe");
        assertTrue(validation instanceof Validation);
        assertTrue(tests.get(Namespace.BDQTEST + "7e0c0418-fe16-4a39-98bd-80e19d95b9d1") instanceof Amendment);
        assertTrue(model.findAllSingleRecordTests("uuid").containsKey("0493bcfb-652e-4d17-815b-b0cce0742fbe"));

        // by the Test IRI (as TestRunner gives it), by urn:uuid, by the bare uuid, and by the Specification
        for (String key : new String[] { Namespace.BDQTEST + "0493bcfb-652e-4d17-815b-b0cce0742fbe",
                "urn:uuid:0493bcfb-652e-4d17-815b-b0cce0742fbe", "0493bcfb-652e-4d17-815b-b0cce0742fbe",
                "https://rs.tdwg.org/bdqtest/specification/01b96157" }) {
            DataQualityMethod method = model.findMethodForSpecification(key);
            assertTrue(key, method instanceof ValidationMethod);
            Validation v = ((ValidationMethod) method).getValidation();
            assertEquals("VALIDATION_COUNTRYCODE_STANDARD", v.getLabel());
            assertEquals("Standard", v.getCriterion().getLabel());
            assertEquals("SingleRecord", v.getResourceType().getLabel());
            assertEquals(URI.create("http://rs.tdwg.org/dwc/terms/countryCode"), v.getActedUpon().getComposedOf().get(0));
            assertEquals("Specification for: VALIDATION_COUNTRYCODE_STANDARD", method.getSpecification().getLabel());
        }
        assertTrue(model.findMethodForSpecification("7e0c0418-fe16-4a39-98bd-80e19d95b9d1") instanceof AmendmentMethod);
        assertNull(model.findMethodForSpecification("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    public void testReadLibraryForm() throws Exception {
        FFDQModel model = load(LIBRARY_FORM);

        Map<String, DataQualityNeed> tests = model.findAllSingleRecordTests("ver");
        assertEquals(2, tests.size());
        Validation version = (Validation) tests.get(Namespace.BDQTEST + "66269bdd-9271-4e76-b25c-7ab81eebe1d8");
        assertNotNull(version);
        assertEquals(Namespace.BDQTEST + "66269bdd-9271-4e76-b25c-7ab81eebe1d8-2025-03-07", version.getId());
        assertEquals(LocalDate.of(2025, 3, 7), version.getIssuedDate());
        // a Test identified only by urn:uuid is keyed by its Test IRI
        assertTrue(tests.containsKey(Namespace.BDQTEST + "391ca46d-3842-4a18-970c-0434cbc17f07"));

        DataQualityMethod method = model.findMethodForSpecification(Namespace.BDQTEST + "66269bdd-9271-4e76-b25c-7ab81eebe1d8");
        assertTrue(method instanceof ValidationMethod);
        assertEquals("VALIDATION_DATEIDENTIFIED_STANDARD", ((ValidationMethod) method).getValidation().getLabel());
        assertEquals("urn:uuid:89f8b2ea", method.getSpecification().getId());

        DataQualityMethod local = model.findMethodForSpecification(Namespace.BDQTEST + "391ca46d-3842-4a18-970c-0434cbc17f07");
        assertTrue(local instanceof ValidationMethod);
        assertEquals("VALIDATION_LOCAL", ((ValidationMethod) local).getValidation().getLabel());
    }

    @Test
    public void testFindOneMissingReturnsNull() throws Exception {
        FFDQModel model = load(LIBRARY_FORM);
        assertNull(model.findOne("urn:uuid:99999999-9999-9999-9999-999999999999", Mechanism.class));
        assertFalse(model.findAllSpecifications().isEmpty());
    }
}
