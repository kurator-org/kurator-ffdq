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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.datakurator.ffdq.model.context.Amendment;
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
     * A ValidationResponse is written as the BDQ standard has it: status, result, comment, and qualifiers
     * on the Response; an Implementation that produces it, uses its Specification, and is implemented by
     * its Mechanism; a Method relating the Specification to the Test; and a PROV-O Activity.
     */
    @Test
    public void testValidationResponseWrittenAsBdqffdq() {
        Specification spec = new Specification();
        spec.setId("urn:uuid:11111111-1111-1111-1111-111111111111");
        spec.setLabel("Specification for: VALIDATION_EXAMPLE");
        Mechanism mechanism = new Mechanism("22222222-2222-2222-2222-222222222222", "Example mechanism");
        Validation criterion = new Validation(new Criterion("Standard", "Standard"), new ActedUpon(), new ResourceType("SingleRecord"));
        criterion.setId(Namespace.BDQTEST + "33333333-3333-3333-3333-333333333333");

        Entity entity = new Entity();
        entity.setValue("COMPLIANT");
        Result result = new Result();
        result.setState(ResultState.RUN_HAS_RESULT);
        result.setEntity(entity);
        result.setComment("a comment");
        result.setQualifiers(java.util.Collections.singletonList(
                new org.datakurator.ffdq.model.ResponseQualifier("AMBIGUOUS", "1963-03-08/1963-08-03", "day and month may be transposed")));

        ValidationResponse response = new ValidationResponse();
        response.setCriterion(criterion);
        response.setDataResource(URI.create("urn:uuid:55555555-5555-5555-5555-555555555555"));
        response.setMechanism(mechanism);
        response.setSpecification(spec);
        response.setResult(result);
        response.setStartedAtTime(java.time.Instant.parse("2026-11-16T12:00:00Z"));
        response.setEndedAtTime(java.time.Instant.parse("2026-11-16T12:00:01Z"));

        RdfWriter w = new RdfWriter();
        Resource s = response.writeRdf(w);
        Model m = w.getModel();
        IRI specIri = RdfWriter.iri(spec.getId());
        IRI mechIri = RdfWriter.iri("urn:uuid:22222222-2222-2222-2222-222222222222");
        IRI record = RdfWriter.iri("urn:uuid:55555555-5555-5555-5555-555555555555");

        // the Response
        assertTrue(m.contains(s, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "ValidationResponse")));
        assertEquals(record, object(m, s, "bdqffdq:appliesTo"));
        assertEquals(RdfWriter.iri(BDQFFDQ + "RUN_HAS_RESULT"), object(m, s, "bdqffdq:hasResponseStatus"));
        assertEquals(RdfWriter.iri(BDQFFDQ + "COMPLIANT"), object(m, s, "bdqffdq:hasResponseResult"));
        assertNull(object(m, s, "bdqffdq:hasResponseResultValue"));
        assertEquals(XSD.STRING, literal(m, s, "bdqffdq:hasResponseComment").getDatatype());
        assertEquals("a comment", literal(m, s, "bdqffdq:hasResponseComment").getLabel());
        Resource q = (Resource) object(m, s, "bdqffdq:hasResponseQualifier");
        assertTrue(m.contains(q, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "ResponseQualifier")));
        assertEquals("AMBIGUOUS", literal(m, q, "rdfs:label").getLabel());
        assertEquals("1963-03-08/1963-08-03", literal(m, q, "rdf:value").getLabel());
        // no terms that are not in bdqffdq, and none of the old PROV links
        for (String old : new String[] { "prov:hadPlan", "prov:wasAttributedTo", "prov:generated", "bdqffdq:criterionInContext" }) {
            assertNull(old, object(m, s, old));
        }
        assertFalse(m.contains(null, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "Result")));

        // the Implementation
        Resource implementation = Models.subject(m.filter(null, RdfWriter.iri(BDQFFDQ + "producesResponse"), s)).orElse(null);
        assertNotNull(implementation);
        assertTrue(m.contains(implementation, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "Implementation")));
        assertEquals(specIri, object(m, implementation, "bdqffdq:usesSpecification"));
        assertEquals(mechIri, object(m, implementation, "bdqffdq:implementedBy"));

        // the Method relating the Specification to the Test
        Resource method = Models.subject(m.filter(null, RdfWriter.iri(BDQFFDQ + "hasSpecification"), specIri)).orElse(null);
        assertNotNull(method);
        assertTrue(m.contains(method, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "ValidationMethod")));
        assertEquals(RdfWriter.iri(criterion.getId()), object(m, method, "bdqffdq:forValidation"));

        // PROV-O
        Resource activity = (Resource) object(m, s, PROV + "wasGeneratedBy");
        assertTrue(m.contains(activity, RDF.TYPE, RdfWriter.iri(PROV + "Activity")));
        assertTrue(m.contains(activity, RdfWriter.iri(PROV + "used"), specIri));
        assertTrue(m.contains(activity, RdfWriter.iri(PROV + "used"), record));
        assertEquals(mechIri, object(m, activity, PROV + "wasAssociatedWith"));
        assertTrue(m.contains(mechIri, RDF.TYPE, RdfWriter.iri(PROV + "SoftwareAgent")));
        assertEquals(XSD.DATETIME, literal(m, activity, PROV + "startedAtTime").getDatatype());
        assertEquals("2026-11-16T12:00:00Z", literal(m, activity, PROV + "startedAtTime").getLabel());

        // and read back
        ValidationResponse read = ValidationResponse.readRdf(new RdfReader(m), s);
        assertEquals(spec.getId(), read.getSpecification().getId());
        assertEquals(mechanism.getId(), read.getMechanism().getId());
        assertEquals(criterion.getId(), read.getCriterion().getId());
        assertEquals("RUN_HAS_RESULT", read.getResult().getState().getLabel());
        assertEquals("COMPLIANT", read.getResult().getEntity().getValue());
        assertEquals("a comment", read.getResult().getComment());
        assertEquals("AMBIGUOUS", read.getResult().getQualifiers().get(0).getLabel());
        assertTrue(read.getResult().isAmbiguous());
        assertEquals(java.time.Instant.parse("2026-11-16T12:00:01Z"), read.getEndedAtTime());
    }

    /**
     * Literal results: an Amendment's proposed changes as JSON (rdf:JSON), read back as a map; a numeric
     * Measure as xsd:integer; and an AmendmentResponse has no bdqffdq:hasResponseResult.
     */
    @Test
    public void testLiteralResults() {
        Entity proposal = new Entity();
        java.util.Map<String, String> changes = new java.util.LinkedHashMap<>();
        changes.put("dwc:month", "11");
        changes.put("dwc:day", "30");
        proposal.setValue(changes);
        Result amended = new Result();
        amended.setState(ResultState.AMENDED);
        amended.setEntity(proposal);
        org.datakurator.ffdq.model.report.AmendmentResponse amendment = new org.datakurator.ffdq.model.report.AmendmentResponse();
        amendment.setResult(amended);

        RdfWriter w = new RdfWriter();
        Resource s = amendment.writeRdf(w);
        Literal json = literal(w.getModel(), s, "bdqffdq:hasResponseResultValue");
        assertEquals(FfdqApiRdf.RDF_JSON, json.getDatatype().stringValue());
        assertEquals("{\"dwc:day\":\"30\",\"dwc:month\":\"11\"}", json.getLabel());
        assertNull(object(w.getModel(), s, "bdqffdq:hasResponseResult"));
        Object readBack = org.datakurator.ffdq.model.report.AmendmentResponse.readRdf(new RdfReader(w.getModel()), s)
                .getResult().getEntity().getValue();
        assertEquals(changes, readBack);

        Entity measured = new Entity();
        measured.setValue("86400");
        Result measure = new Result();
        measure.setState(ResultState.RUN_HAS_RESULT);
        measure.setEntity(measured);
        org.datakurator.ffdq.model.report.MeasurementResponse measurement = new org.datakurator.ffdq.model.report.MeasurementResponse();
        measurement.setResult(measure);
        Resource ms = measurement.writeRdf(w);
        assertEquals(XSD.INTEGER, literal(w.getModel(), ms, "bdqffdq:hasResponseResultValue").getDatatype());
        assertTrue(w.getModel().contains(ms, RDF.TYPE, RdfWriter.iri(BDQFFDQ + "MeasurementResponse")));
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

    /**
     * An ImprovementTarget uses the bdqffdq terms (bdqffdq:targetedValidation, bdqffdq:targetedMeasure),
     * and bdqffdq:improvedBy relates the Amendment (subject) to the ImprovementTarget (object), as the
     * standard's SHACL shapes require.
     */
    @Test
    public void testImprovementTargetTermsAndDirection() {
        Amendment amendment = new Amendment();
        amendment.setId(Namespace.BDQTEST + "amendment");
        Validation validation = new Validation(new Criterion("Standard", "Standard"), new ActedUpon(), new ResourceType("SingleRecord"));
        validation.setId(Namespace.BDQTEST + "validation");
        org.datakurator.ffdq.model.solutions.ImprovementTarget target = new org.datakurator.ffdq.model.solutions.ImprovementTarget();
        target.setImprovedBy(amendment);
        target.setContextualizedCriterion(new java.util.HashSet<>(java.util.Collections.singleton(validation)));

        RdfWriter w = new RdfWriter();
        Resource s = target.writeRdf(w);
        Model m = w.getModel();
        assertTrue(m.contains(RdfWriter.iri(amendment.getId()), RdfWriter.iri(BDQFFDQ + "improvedBy"), s));
        assertFalse(m.contains(s, RdfWriter.iri(BDQFFDQ + "improvedBy"), null));
        assertTrue(m.contains(s, RdfWriter.iri(BDQFFDQ + "targetedValidation"), RdfWriter.iri(validation.getId())));

        org.datakurator.ffdq.model.solutions.ImprovementTarget read =
                org.datakurator.ffdq.model.solutions.ImprovementTarget.readRdf(new RdfReader(m), s);
        assertEquals(amendment.getId(), read.getImprovedBy().getId());
        assertEquals(1, read.getContextualizedCriterion().size());
    }
}
