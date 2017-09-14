package org.datakurator.data.ffdq.runner;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.datakurator.data.ffdq.Namespace;
import org.datakurator.data.ffdq.ResultStatus;
import org.datakurator.data.ffdq.assertions.DQValidation;
import org.datakurator.data.ffdq.model.needs.*;
import org.datakurator.data.ffdq.model.report.*;
import org.datakurator.data.ffdq.model.solutions.*;
import org.datakurator.data.provenance.CurationStatus;
import org.datakurator.ffdq.api.*;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.json.simple.JSONObject;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by lowery on 8/1/17.
 */
public class RDFBeanFactory {
    private Repository repo;
    private RepositoryConnection conn;
    private RDFBeanManager manager;

    public RDFBeanFactory() {
        // Initialize an in-memory store and the rdf bean manager
        repo = new SailRepository(new MemoryStore());
        repo.initialize();

        conn = repo.getConnection();
        manager = new RDFBeanManager(conn);
    }

    private void saveBean(Object obj) {
        try {
            manager.add(obj);
        } catch (RDFBeanException e) {
            throw new RuntimeException("Could not process the rdf bean instance.", e);
        }
    }

    private Object fetchBean(String guid, Class cls) {
        try {
            Resource r = manager.getResource(guid, cls);
            return manager.get(r);
        } catch (RDFBeanException e) {
            throw new RuntimeException("Could not fetch the rdf bean instance.", e);
        }
    }

    public Mechanism createMechanism(String guid, String label) {
        Mechanism mechanism = new Mechanism(guid, label);
        saveBean(mechanism);

        return mechanism;
    }

    public Specification createSpecification(String guid, String label, Mechanism mechanism) {
        Specification specification = new Specification(guid, label);

        Implementation implementation = new Implementation();
        implementation.setSpecification(specification);
        implementation.setImplementedBy(Collections.singletonList(mechanism));

        saveBean(implementation);

        return specification;
    }

    public InformationElement createInformationElements(List<Parameter> params) {
        InformationElement ie = new InformationElement();

        for (Parameter param : params) {
                // Create an information element from the terms
                ie.addTerm(param.getURI());
        }

        saveBean(ie);

        return ie;
    }

    public void createMeasurementMethod(AssertionTest test, String d) {
        Mechanism mechanism = createMechanism(test.getMechanism(), test.getClassName());
        Specification specification = createSpecification(test.getGuid(), test.getSpecification(), mechanism);
        InformationElement vie = createInformationElements(test.getParameters());

        Dimension dimension = new Dimension(d);

        ContextualizedDimension context = new ContextualizedDimension();
        context.setDimension(dimension);
        context.setResourceType(ResourceType.SINGLE_RECORD);
        context.setInformationElements(vie);
        saveBean(context);

        MeasurementMethod method = new MeasurementMethod();
        method.setSpecification(specification);
        method.setContextualizedDimension(context);
        saveBean(method);
    }

    public void createMeasure(AssertionTest test, Map<String, String> record, DQMeasurementResponse response) {
        Specification specification = (Specification) fetchBean(test.getGuid(), Specification.class);
        Mechanism mechanism = (Mechanism) fetchBean(test.getMechanism(), Mechanism.class);

        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
                "PREFIX ffdq: <http://example.com/ffdq/> " +
                        "SELECT ?id " +
                        "WHERE { ?id a ffdq:MeasurementMethod ." +
                        "?id ffdq:hasSpecification ?specification . " +
                        "FILTER( ?specification = <" + test.getGuid() + "> ) }");

        MeasurementMethod method = (MeasurementMethod) fetchBean(query, MeasurementMethod.class);

        ContextualizedDimension context = method.getContextualizedDimension();

        DataResource dataResource = new DataResource(record);

        // TODO: process response and add result

        ResultStatus status = ResultStatus.NOT_COMPLETE;
        ResultState state = response.getResultState();
        Object value = null;

        if (state.equals(EnumDQResultState.RUN_HAS_RESULT)) {
            status = ResultStatus.COMPLETE;
            value = response.getValue();
        } else if (state.equals(EnumDQResultState.AMBIGUOUS)) {
            status = ResultStatus.AMBIGUOUS;
        } else if (state.equals(EnumDQResultState.EXTERNAL_PREREQUISITES_NOT_MET)) {
            status = ResultStatus.EXTERNAL_PREREQUISITES_NOT_MET;
        } else if (state.equals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET)) {
            status = ResultStatus.DATA_PREREQUISITES_NOT_MET;
        }

        Result result = new Result();
        result.setStatus(status);
        result.setComment(response.getComment());

        Measure measure = new Measure();
        measure.setMechanism(mechanism);
        measure.setSpecification(specification);
        measure.setDimension(context);
        measure.setDataResource(dataResource);
        measure.setResult(result);
        saveBean(measure);
    }

    private Object fetchBean(TupleQuery query, Class cls) {
        try (TupleQueryResult result = query.evaluate()) {
            BindingSet solution = result.next();
            return fetchBean(solution.getValue("id").stringValue(), cls);
        }
    }

    public void createValidationMethod(AssertionTest test, String c) {
        Mechanism mechanism = createMechanism(test.getMechanism(), test.getClassName());
        Specification specification = createSpecification(test.getGuid(), test.getSpecification(), mechanism);
        InformationElement vie = createInformationElements(test.getParameters());

        Criterion criterion = new Criterion(c);

        ContextualizedCriterion context = new ContextualizedCriterion();
        context.setCriterion(criterion);
        context.setResourceType(ResourceType.SINGLE_RECORD);
        context.setInformationElements(vie);
        saveBean(context);

        ValidationMethod method = new ValidationMethod();
        method.setSpecification(specification);
        method.setContextualizedCriterion(context);
        saveBean(method);
    }

    public void createValidation(AssertionTest test, Map<String, String> record, DQValidationResponse response) {
        Specification specification = (Specification) fetchBean(test.getGuid(), Specification.class);
        Mechanism mechanism = (Mechanism) fetchBean(test.getMechanism(), Mechanism.class);

        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
                "PREFIX ffdq: <http://example.com/ffdq/> " +
                        "SELECT ?id " +
                        "WHERE { ?id a ffdq:ValidationMethod . " +
                        "?id ffdq:hasSpecification ?specification . " +
                        "FILTER( ?specification = <" + test.getGuid() + "> ) }");

        ValidationMethod method = (ValidationMethod) fetchBean(query, ValidationMethod.class);

        ContextualizedCriterion context = method.getContextualizedCriterion();

        DataResource dataResource = new DataResource(record);

        // TODO: process response and add result

        ResultState state = response.getResultState();

        if (!state.equals(EnumDQResultState.NOT_RUN)) {
            ResultStatus status = null;

            if (state.equals(EnumDQResultState.RUN_HAS_RESULT)) {
                EnumDQValidationResult result = response.getResult();

                if (result.equals(EnumDQValidationResult.COMPLIANT)) {
                    status = ResultStatus.COMPLIANT;
                } else if (result.equals(EnumDQValidationResult.NOT_COMPLIANT)) {
                    status = ResultStatus.NOT_COMPLIANT;
                }
            } else if (state.equals(EnumDQResultState.AMBIGUOUS)) {
                status = ResultStatus.AMBIGUOUS;
            } else if (state.equals(EnumDQResultState.EXTERNAL_PREREQUISITES_NOT_MET)) {
                status = ResultStatus.EXTERNAL_PREREQUISITES_NOT_MET;
            } else if (state.equals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET)) {
                status = ResultStatus.DATA_PREREQUISITES_NOT_MET;
            }

            Result result = new Result();
            result.setStatus(status);
            result.setComment(response.getComment());

            Validation validation = new Validation();
            validation.setMechanism(mechanism);
            validation.setSpecification(specification);
            validation.setCriterion(context);
            validation.setDataResource(dataResource);
            validation.setResult(result);
            saveBean(validation);
        }
    }

    public void createAmendmentMethod(AssertionTest test, String e) {
        Mechanism mechanism = createMechanism(test.getMechanism(), test.getClassName());
        Specification specification = createSpecification(test.getGuid(), test.getSpecification(), mechanism);
        InformationElement vie = createInformationElements(test.getParameters());

        Enhancement enhancement = new Enhancement(e);

        ContextualizedEnhancement context = new ContextualizedEnhancement();
        context.setEnhancement(enhancement);
        context.setResourceType(ResourceType.SINGLE_RECORD);
        context.setInformationElements(vie);
        saveBean(context);

        AmendmentMethod method = new AmendmentMethod();
        method.setSpecification(specification);
        method.setContextualizedEnhancement(context);
        saveBean(method);
    }

    public void createAmendment(AssertionTest test, Map<String, String> record, DQAmendmentResponse response) {
        Specification specification = (Specification) fetchBean(test.getGuid(), Specification.class);
        Mechanism mechanism = (Mechanism) fetchBean(test.getMechanism(), Mechanism.class);

        TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL,
                "PREFIX ffdq: <http://example.com/ffdq/> " +
                        "SELECT ?id " +
                        "WHERE { ?id a ffdq:AmendmentMethod . " +
                        "?id ffdq:hasSpecification ?specification . " +
                        "FILTER( ?specification = <" + test.getGuid() + "> ) }");

        AmendmentMethod method = (AmendmentMethod) fetchBean(query, ValidationMethod.class);

        ContextualizedEnhancement context = method.getContextualizedEnhancement();

        DataResource dataResource = new DataResource(record);

        // TODO: process response and add result

        ResultStatus status = ResultStatus.NO_CHANGE;
        ResultState state = response.getResultState();

        if (state.equals(EnumDQAmendmentResultState.CHANGED)) {
            status = ResultStatus.CURATED;
        } else if (state.equals(EnumDQAmendmentResultState.FILLED_IN)) {
            status = ResultStatus.FILLED_IN;
        } else if (state.equals(EnumDQAmendmentResultState.TRANSPOSED)) {
            status = ResultStatus.TRANSPOSED;
        }

        List<Entity> members = new ArrayList<>();
        Map<String, String> values = response.getResult();

        for (String field : values.keySet()) {
            Entity entity = new Entity();

            Parameter param = new Parameter(field);
            InformationElement ie = new InformationElement(param.getURI());

            entity.setInformationElement(ie);
            entity.setValue(values.get(field));

            members.add(entity);
        }

        Result result = new Result();
        result.setStatus(status);
        result.setComment(response.getComment());
        result.setMembers(members);

        Amendment amendment = new Amendment();
        amendment.setMechanism(mechanism);
        amendment.setSpecification(specification);
        amendment.setEnhancement(context);
        amendment.setDataResource(dataResource);
        amendment.setResult(result);
        saveBean(amendment);
    }

    public void write(RDFFormat format, OutputStream out) {
        RDFWriter writer = Rio.createWriter(format, out);
        try (RepositoryConnection conn = repo.getConnection()) {
            conn.prepareGraphQuery(QueryLanguage.SPARQL,
                    "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o . MINUS { ?s rdfbeans:bindingClass ?o } } ").evaluate(writer);
        }
    }

    public static void main(String[] args) {
        RDFBeanFactory factory = new RDFBeanFactory();

        String c = "Test to see if the eventDate and verbatimEventDate are consistent.";
        String s = "If a dwc:eventDate is not empty and the verbatimEventDate is not empty compare the value " +
                "of dwc:eventDate with that of dwc:verbatimEventDate, and assert Compliant if the two " +
                "represent the same data or date range.";
        String m = "Kurator: Date Validator - DwCEventDQ";

        //factory.createValidationMethod(null,
        //        Arrays.asList("dwc:eventDate"),
        //        c, s, m);

        factory.write(RDFFormat.TURTLE, System.out);
    }
}
