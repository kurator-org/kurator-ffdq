/**
 * RdfMappingTest.java
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.datakurator.ffdq.model.ActedUpon;
import org.datakurator.ffdq.model.Criterion;
import org.datakurator.ffdq.model.Entity;
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.ResourceType;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.report.Result;
import org.datakurator.ffdq.model.report.ValidationResponse;
import org.datakurator.ffdq.model.solutions.DataQualityMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.model.util.Models;
import org.junit.Test;

/**
 * Tests of the explicit RDF4J mapping of the model classes ({@link RdfWriter}, {@link RdfReader}), which
 * replaced the RDFBeans annotations: datatypes (in particular dates and times), the RDF written for the
 * report classes (as their RDFBeans annotations wrote it), and reading objects back.
 */
public class RdfMappingTest {

    private static final String BDQFFDQ = "https://rs.tdwg.org/bdqffdq/terms/";
    private static final String PROV = "http://www.w3.org/ns/prov#";

    private static Literal literal(Model m, Resource s, String predicate) {
        Value v = Models.object(m.filter(s, RdfWriter.iri(predicate), null)).orElse(null);
        assertTrue("expected a literal for " + predicate + ", got " + v, v instanceof Literal);
        return (Literal) v;
    }

    private static Value object(Model m, Resource s, String predicate) {
        return Models.object(m.filter(s, RdfWriter.iri(predicate), null)).orElse(null);
    }

    @Test
    public void testDateAndTimeDatatypes() {
        Literal date = RdfWriter.literal(LocalDate.of(2024, 9, 18));
        assertEquals(XSD.DATE, date.getDatatype());
        assertEquals("2024-09-18", date.getLabel());

        Literal fromDate = RdfWriter.literal(Date.from(OffsetDateTime.of(2025, 3, 7, 14, 7, 0, 0, ZoneOffset.ofHours(-6)).toInstant()));
        assertEquals(XSD.DATETIME, fromDate.getDatatype());
        assertEquals("2025-03-07T20:07:00Z", fromDate.getLabel());

        Literal offset = RdfWriter.literal(OffsetDateTime.of(2025, 3, 7, 14, 7, 0, 0, ZoneOffset.ofHours(-6)));
        assertEquals(XSD.DATETIME, offset.getDatatype());
        assertEquals("2025-03-07T14:07:00-06:00", offset.getLabel());

        Literal local = RdfWriter.literal(LocalDateTime.of(2025, 3, 7, 14, 7, 30));
        assertEquals(XSD.DATETIME, local.getDatatype());
        assertEquals("2025-03-07T14:07:30", local.getLabel());
    }

    @Test
    public void testOtherDatatypes() {
        assertEquals(XSD.BOOLEAN, RdfWriter.literal(Boolean.FALSE).getDatatype());
        assertEquals(XSD.INT, RdfWriter.literal(Integer.valueOf(3)).getDatatype());
        RdfWriter w = new RdfWriter();
        assertEquals(XSD.STRING, ((Literal) w.toValue("text")).getDatatype());
        assertEquals(RdfWriter.iri("http://rs.tdwg.org/dwc/terms/countryCode"),
                w.toValue(URI.create("http://rs.tdwg.org/dwc/terms/countryCode")));
        assertNull(w.toValue(null));
    }

    @Test
    public void testIssuedDateRoundTripsAsXsdDate() {
        Validation v = new Validation(new Criterion("Standard", "Standard"), new ActedUpon(), new ResourceType("SingleRecord"));
        v.setId(Namespace.BDQTEST + "00000000-0000-0000-0000-000000000001-2024-09-18");
        v.setIssuedDate(LocalDate.of(2024, 9, 18));
        RdfWriter w = new RdfWriter();
        Resource s = v.writeRdf(w);
        Literal issued = literal(w.getModel(), s, "dcterms:issued");
        assertEquals(XSD.DATE, issued.getDatatype());
        assertEquals("2024-09-18", issued.getLabel());

        Validation read = Validation.readRdf(new RdfReader(w.getModel()), s);
        assertEquals(LocalDate.of(2024, 9, 18), read.getIssuedDate());
    }

    @Test
    public void testSubjectNotPrefixedTwice() {
        assertEquals("urn:uuid:abc", RdfWriter.subject("abc", "urn:uuid:").stringValue());
        assertEquals("urn:uuid:abc", RdfWriter.subject("urn:uuid:abc", "urn:uuid:").stringValue());
        assertEquals(Namespace.BDQDIM + "Completeness", RdfWriter.subject("Completeness", "bdqdim:").stringValue());
        assertEquals("urn:uuid:abc", new Mechanism("abc", "m").getId());
        assertEquals("urn:uuid:abc", new Mechanism("urn:uuid:abc", "m").getId());
    }

    /**
     * A ValidationResponse is written as its RDFBeans annotations wrote it.
     */
    @Test
    public void testValidationResponseWrittenAsAnnotated() {
        Specification spec = new Specification();
        spec.setId("urn:uuid:11111111-1111-1111-1111-111111111111");
        spec.setLabel("Specification for: VALIDATION_EXAMPLE");
        Mechanism mechanism = new Mechanism("22222222-2222-2222-2222-222222222222", "Example mechanism");
        Validation criterion = new Validation(new Criterion("Standard", "Standard"), new ActedUpon(), new ResourceType("SingleRecord"));
        criterion.setId(Namespace.BDQTEST + "33333333-3333-3333-3333-333333333333-2025-03-07");

        ResultState state = new ResultState("RUN_HAS_RESULT");
        state.setId("run_has_result");
        Entity entity = new Entity();
        entity.setId("urn:uuid:44444444-4444-4444-4444-444444444444");
        entity.setValue("COMPLIANT");
        Result result = new Result();
        result.setState(state);
        result.setEntity(entity);
        result.setComment("a comment");

        ValidationResponse response = new ValidationResponse();
        response.setCriterion(criterion);
        response.setDataResource(URI.create("urn:uuid:55555555-5555-5555-5555-555555555555"));
        response.setMechanism(mechanism);
        response.setSpecification(spec);
        response.setResult(result);

        RdfWriter w = new RdfWriter();
        Resource s = response.writeRdf(w);
        Model m = w.getModel();

        assertEquals(response.getId(), s.stringValue());
        assertTrue(m.contains(s, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "ValidationResponse")));
        assertTrue(m.contains(RdfWriter.iri(BDQFFDQ + "ValidationResponse"), RDFS.SUBCLASSOF, RdfWriter.iri(BDQFFDQ + "Response")));
        assertEquals(RdfWriter.iri("urn:uuid:55555555-5555-5555-5555-555555555555"), object(m, s, "bdqffdq:appliesTo"));
        assertEquals(RdfWriter.iri(spec.getId()), object(m, s, PROV + "hadPlan"));
        assertEquals(RdfWriter.iri("urn:uuid:22222222-2222-2222-2222-222222222222"), object(m, s, PROV + "wasAttributedTo"));
        assertEquals(RdfWriter.iri(criterion.getId()), object(m, s, "bdqffdq:criterionInContext"));
        Resource r = (Resource) object(m, s, PROV + "generated");
        assertEquals(result.getId(), r.stringValue());

        assertTrue(m.contains(r, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "Result")));
        assertEquals(XSD.BOOLEAN, literal(m, r, "bdqffdq:hasAmbiguity").getDatatype());
        assertEquals("a comment", literal(m, r, "rdfs:comment").getLabel());

        IRI stateIri = RdfWriter.iri(BDQFFDQ + "RUN_HAS_RESULT");
        assertEquals(stateIri, object(m, r, "bdqffdq:hasState"));
        assertTrue(m.contains(stateIri, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "ResponseStatus")));
        assertEquals("RUN_HAS_RESULT", literal(m, stateIri, "rdfs:label").getLabel());

        IRI entityIri = RdfWriter.iri(entity.getId());
        assertEquals(entityIri, object(m, r, "bdqffdq:hasValue"));
        assertTrue(m.contains(entityIri, RDF.TYPE, RdfWriter.iri(FfdqApiRdf.ENTITY)));
        assertEquals("COMPLIANT", literal(m, entityIri, PROV + "value").getLabel());

        assertTrue(m.contains(RdfWriter.iri("urn:uuid:22222222-2222-2222-2222-222222222222"), RDF.TYPE, RdfWriter.iri(BDQFFDQ + "Mechanism")));
    }

    /**
     * An object referred to more than once is written once, and read back as the same instance.
     */
    @Test
    public void testSharedObjectsWrittenAndReadOnce() {
        Specification spec = new Specification();
        spec.setId("urn:uuid:66666666-6666-6666-6666-666666666666");
        spec.setLabel("Shared");
        Validation v1 = new Validation(new Criterion("Standard", "Standard"), new ActedUpon(), new ResourceType("SingleRecord"));
        Validation v2 = new Validation(new Criterion("Standard", "Standard"), new ActedUpon(), new ResourceType("SingleRecord"));
        ValidationMethod m1 = new ValidationMethod(spec, v1);
        ValidationMethod m2 = new ValidationMethod(spec, v2);
        RdfWriter w = new RdfWriter();
        Resource s1 = m1.writeRdf(w);
        Resource s2 = m2.writeRdf(w);
        assertEquals(1, w.getModel().filter(RdfWriter.iri(spec.getId()), RdfWriter.iri("rdfs:label"), null).size());

        RdfReader r = new RdfReader(w.getModel());
        ValidationMethod read1 = (ValidationMethod) DataQualityMethod.readRdf(r, s1);
        ValidationMethod read2 = (ValidationMethod) DataQualityMethod.readRdf(r, s2);
        assertSame(read1.getSpecification(), read2.getSpecification());
        assertEquals("Shared", read1.getSpecification().getLabel());
    }

    /**
     * The ResultStates that are bdqffdq:ResponseStatus terms are written as those terms; any other is
     * written under the placeholder namespace, so it cannot be mistaken for a BDQ term.
     */
    @Test
    public void testResultStateIris() {
        for (ResultState state : new ResultState[] { ResultState.RUN_HAS_RESULT, ResultState.AMENDED, ResultState.NOT_AMENDED,
                ResultState.FILLED_IN, ResultState.INTERNAL_PREREQUISITES_NOT_MET, ResultState.EXTERNAL_PREREQUISITES_NOT_MET }) {
            assertEquals(BDQFFDQ + state.getLabel(), FfdqApiRdf.resultStateIri(state).stringValue());
        }
        assertEquals("http://example.com/ffdq/rs/not_run", FfdqApiRdf.resultStateIri(ResultState.NOT_RUN).stringValue());
    }

    /**
     * A ResultState reads back with the label and id ffdq-api gives it, from the bdqffdq term and from
     * the http://example.com/ffdq/rs/{id} IRIs of earlier reports.
     */
    @Test
    public void testResultStateReadsBack() {
        RdfWriter w = new RdfWriter();
        Resource current = (Resource) w.toValue(ResultState.RUN_HAS_RESULT);
        Resource old = RdfWriter.iri("http://example.com/ffdq/rs/amended");
        w.getModel().add(old, RDF.TYPE, RdfWriter.iri("http://rs.tdwg.org/bdqffdq/terms/ResponseStatus"));
        w.getModel().add(old, RDFS.LABEL, RdfWriter.literal("AMENDED"));
        RdfReader r = new RdfReader(w.getModel());

        ResultState read = FfdqApiRdf.readResultState(r, current);
        assertEquals(ResultState.RUN_HAS_RESULT, read);
        assertEquals("RUN_HAS_RESULT", read.getLabel());
        ResultState readOld = FfdqApiRdf.readResultState(r, old);
        assertEquals(ResultState.AMENDED, readOld);
        assertEquals("AMENDED", readOld.getLabel());
    }
}
