/**
 * BdqffdqShaclTest.java
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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.datakurator.ffdq.model.Entity;
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.ResponseQualifier;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.report.AmendmentResponse;
import org.datakurator.ffdq.model.report.DataQualityReport;
import org.datakurator.ffdq.model.report.IssueResponse;
import org.datakurator.ffdq.model.report.MeasurementResponse;
import org.datakurator.ffdq.model.report.Response;
import org.datakurator.ffdq.model.report.Result;
import org.datakurator.ffdq.model.report.ValidationResponse;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.DataQualityMethod;
import org.datakurator.ffdq.model.solutions.IssueMethod;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Test;

/**
 * Validates the report RDF kurator-ffdq writes against the SHACL shapes of the BDQ standard's
 * bdqffdq ontology (a copy in src/test/resources/bdq, see its README).
 *
 * The Responses of all four types are made as TestRunner makes them (with the Method from the
 * Test definitions) and as a client without the Method makes them (bdq_workbench), with a
 * Mechanism and a DataQualityReport.  Results are checked for the report's own resources:
 * Responses, Implementations, Mechanisms, ResponseQualifiers, and the DataQualityReport.  (The
 * Test definitions here are minimal, so shapes about Tests are not checked.)
 */
public class BdqffdqShaclTest {

    private static final String BDQFFDQ = Namespace.BDQFFDQ;

    private static final String DEFINITIONS =
            "@prefix bdqffdq: <https://rs.tdwg.org/bdqffdq/terms/> .\n"
            + "@prefix bdqtest: <https://rs.tdwg.org/bdqtest/terms/> .\n"
            + "@prefix bdqcrit: <https://rs.tdwg.org/bdqcrit/terms/> .\n"
            + "@prefix bdqdim: <https://rs.tdwg.org/bdqdim/terms/> .\n"
            + "@prefix bdqenh: <https://rs.tdwg.org/bdqenh/terms/> .\n"
            + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n"
            + "bdqtest:v1 a bdqffdq:Validation ; rdfs:label \"VALIDATION_ONE\" ; bdqffdq:hasCriterion bdqcrit:Standard ;"
            + "  bdqffdq:hasResourceType bdqffdq:SingleRecord ; bdqffdq:hasActedUponInformationElement <urn:uuid:ie1> .\n"
            + "<urn:uuid:ie1> a bdqffdq:ActedUpon ; bdqffdq:composedOf <http://rs.tdwg.org/dwc/terms/countryCode> .\n"
            + "<urn:uuid:vm1> a bdqffdq:ValidationMethod ; bdqffdq:forValidation bdqtest:v1 ; bdqffdq:hasSpecification <urn:uuid:vs1> .\n"
            + "<urn:uuid:vs1> a bdqffdq:Specification ; rdfs:label \"Specification for: VALIDATION_ONE\" .\n"
            + "bdqtest:m1 a bdqffdq:Measure ; rdfs:label \"MEASURE_ONE\" ; bdqffdq:hasDataQualityDimension bdqdim:Completeness ;"
            + "  bdqffdq:hasResourceType bdqffdq:SingleRecord ; bdqffdq:hasActedUponInformationElement <urn:uuid:ie1> .\n"
            + "<urn:uuid:mm1> a bdqffdq:MeasurementMethod ; bdqffdq:forMeasure bdqtest:m1 ; bdqffdq:hasSpecification <urn:uuid:ms1> .\n"
            + "<urn:uuid:ms1> a bdqffdq:Specification ; rdfs:label \"Specification for: MEASURE_ONE\" .\n"
            + "bdqtest:a1 a bdqffdq:Amendment ; rdfs:label \"AMENDMENT_ONE\" ; bdqffdq:hasEnhancement bdqenh:Standardized ;"
            + "  bdqffdq:hasResourceType bdqffdq:SingleRecord ; bdqffdq:hasActedUponInformationElement <urn:uuid:ie1> .\n"
            + "<urn:uuid:am1> a bdqffdq:AmendmentMethod ; bdqffdq:forAmendment bdqtest:a1 ; bdqffdq:hasSpecification <urn:uuid:as1> .\n"
            + "<urn:uuid:as1> a bdqffdq:Specification ; rdfs:label \"Specification for: AMENDMENT_ONE\" .\n"
            + "bdqtest:i1 a bdqffdq:Issue ; rdfs:label \"ISSUE_ONE\" ; bdqffdq:hasCriterion bdqcrit:NotEmpty ;"
            + "  bdqffdq:hasResourceType bdqffdq:SingleRecord ; bdqffdq:hasActedUponInformationElement <urn:uuid:ie1> .\n"
            + "<urn:uuid:im1> a bdqffdq:IssueMethod ; bdqffdq:forIssue bdqtest:i1 ; bdqffdq:hasSpecification <urn:uuid:is1> .\n"
            + "<urn:uuid:is1> a bdqffdq:Specification ; rdfs:label \"Specification for: ISSUE_ONE\" .\n";

    private static Result result(ResultState state, Object value, String comment) {
        Result result = new Result();
        result.setState(state);
        if (value != null) {
            Entity entity = new Entity();
            entity.setValue(value);
            result.setEntity(entity);
        }
        result.setComment(comment);
        return result;
    }

    private static <T extends Response> T response(T response, DataQualityMethod method, Mechanism mechanism, Result result,
            URI record, boolean withMethod) {
        response.setSpecification(method.getSpecification());
        response.setMechanism(mechanism);
        response.setResult(result);
        response.setDataResource(record);
        if (withMethod) {
            response.setMethod(method);
        }
        return response;
    }

    /**
     * Report RDF for all four Response types, made with and without the Method, conforms to the
     * bdqffdq SHACL shapes (no results of any severity on the report's resources).
     */
    @Test
    public void testReportConformsToBdqffdqShapes() throws Exception {
        FFDQModel model = new FFDQModel();
        model.load(new ByteArrayInputStream(DEFINITIONS.getBytes(StandardCharsets.UTF_8)), RDFFormat.TURTLE);
        ValidationMethod vm = (ValidationMethod) model.findMethodForSpecification("urn:uuid:vs1");
        MeasurementMethod mm = (MeasurementMethod) model.findMethodForSpecification("urn:uuid:ms1");
        AmendmentMethod am = (AmendmentMethod) model.findMethodForSpecification("urn:uuid:as1");
        IssueMethod im = (IssueMethod) model.findMethodForSpecification("urn:uuid:is1");

        Mechanism mechanism = new Mechanism("22222222-2222-2222-2222-222222222222", "Example mechanism");
        DataQualityReport report = new DataQualityReport();
        List<Response> responses = new ArrayList<>();
        for (boolean withMethod : new boolean[] { true, false }) {
            URI record = URI.create("urn:uuid:0000000" + (withMethod ? "1" : "2") + "-0000-0000-0000-000000000000");
            ValidationResponse v = response(new ValidationResponse(), vm, mechanism,
                    result(ResultState.RUN_HAS_RESULT, "COMPLIANT", "dwc:countryCode is a valid code"), record, withMethod);
            v.setCriterion(vm.getValidation());
            MeasurementResponse m = response(new MeasurementResponse(), mm, mechanism,
                    result(ResultState.RUN_HAS_RESULT, "COMPLETE", "complete"), record, withMethod);
            m.setDimension(mm.getMeasure());
            Map<String, String> changes = new LinkedHashMap<>();
            changes.put("dwc:countryCode", "GL");
            Result amended = result(ResultState.AMENDED, changes, "proposed a standard value");
            amended.setQualifiers(Arrays.asList(new ResponseQualifier(ResponseQualifier.AMBIGUOUS, "GL", "proof of concept")));
            AmendmentResponse a = response(new AmendmentResponse(), am, mechanism, amended, record, withMethod);
            a.setEnhancement(am.getContextualizedEnhancement());
            IssueResponse i = response(new IssueResponse(), im, mechanism,
                    result(ResultState.INTERNAL_PREREQUISITES_NOT_MET, null, "dwc:countryCode is empty"), record, withMethod);
            i.setIssueInContext(im.getContextualizedIssue());
            v.setInformedBy(a);
            responses.addAll(Arrays.asList(v, m, a, i));
        }
        for (Response r : responses) {
            model.save(r);
            report.addResponse(r);
        }
        model.save(report);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(RDFFormat.TURTLE, out, false);
        Model data = Rio.parse(new ByteArrayInputStream(out.toByteArray()), "", RDFFormat.TURTLE);
        // kept for inspection when the test fails
        java.nio.file.Files.write(java.nio.file.Paths.get("target", "bdqffdq-shacl-test-report.ttl"), out.toByteArray());

        List<String> reportResults = validate(out.toByteArray(), data);
        assertEquals("SHACL results on report resources: " + reportResults, 0, reportResults.size());

        // and the Test of each Response is reachable, so reading gives the same Tests
        DataQualityReport read = (DataQualityReport) model.findOne(report.getId(), DataQualityReport.class);
        assertEquals(8, read.getResponses().size());
        for (Response r : read.getResponses()) {
            if (r instanceof ValidationResponse) {
                Validation criterion = ((ValidationResponse) r).getCriterion();
                assertEquals(Namespace.BDQTEST + "v1", criterion.getId());
                assertTrue(((ValidationResponse) r).getInformedBy() != null);
            }
        }
    }

    /**
     * Validate data against the bdqffdq shapes, with Jena SHACL.
     *
     * @param data the data, as Turtle
     * @param model the data, for selecting the report's resources
     * @return the SHACL results (of any severity) whose focus node is a report resource, as text
     */
    private static List<String> validate(byte[] data, Model model) throws Exception {
        org.apache.jena.rdf.model.Model shapesModel = org.apache.jena.rdf.model.ModelFactory.createDefaultModel();
        try (InputStream shapes = BdqffdqShaclTest.class.getResourceAsStream("/bdq/bdqffdq_SHACL_constraints.ttl")) {
            shapesModel.read(shapes, null, "TTL");
        }
        org.apache.jena.rdf.model.Model dataModel = org.apache.jena.rdf.model.ModelFactory.createDefaultModel();
        dataModel.read(new ByteArrayInputStream(data), null, "TTL");
        org.apache.jena.shacl.ValidationReport report = org.apache.jena.shacl.ShaclValidator.get()
                .validate(org.apache.jena.shacl.Shapes.parse(shapesModel.getGraph()), dataModel.getGraph());
        List<String> results = new ArrayList<>();
        for (org.apache.jena.shacl.validation.ReportEntry entry : report.getEntries()) {
            org.apache.jena.graph.Node focus = entry.focusNode();
            if (focus.isURI() && isReportResource(model, RdfWriter.iri(focus.getURI()))) {
                results.add(entry.severity().level().getLocalName() + " " + focus.getURI() + " " + entry.message());
            }
        }
        return results;
    }

    private static boolean isReportResource(Model data, Resource node) {
        for (String type : new String[] { "Response", "ValidationResponse", "MeasurementResponse", "AmendmentResponse",
                "IssueResponse", "Implementation", "Mechanism", "ResponseQualifier", "DataQualityReport" }) {
            IRI t = RdfWriter.iri(BDQFFDQ + type);
            if (data.contains(node, RDF.TYPE, t)) {
                return true;
            }
        }
        return false;
    }
}
