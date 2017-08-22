package org.datakurator.data.ffdq.runner;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.datakurator.data.ffdq.Namespace;
import org.datakurator.data.ffdq.assertions.DQValidation;
import org.datakurator.data.ffdq.model.needs.*;
import org.datakurator.data.ffdq.model.report.*;
import org.datakurator.data.ffdq.model.solutions.*;
import org.datakurator.ffdq.api.DQAmendmentResponse;
import org.datakurator.ffdq.api.DQMeasurementResponse;
import org.datakurator.ffdq.api.DQValidationResponse;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

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
    private RDFBeanManager manager;

    public RDFBeanFactory() {
        // Initialize an in-memory store and the rdf bean manager
        repo = new SailRepository(new MemoryStore());
        repo.initialize();

        RepositoryConnection conn = repo.getConnection();
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

    public Specification createSpecification(String label, Mechanism mechanism) {
        Specification specification = new Specification(label);

        Implementation implementation = new Implementation();
        implementation.setSpecification(specification);
        implementation.setImplementedBy(Collections.singletonList(mechanism));

        saveBean(implementation);

        return specification;
    }

    public List<InformationElement> createInformationElements(List<Parameter> params) {
        List<InformationElement> vie = new ArrayList<>();

        for (Parameter param : params) {
                // Create an information element from the term
                InformationElement ie = new InformationElement();
                ie.setComposedOf(param.getURI());

                saveBean(ie);
                vie.add(ie);
        }

        return vie;
    }

    public void createMeasurementMethod(AssertionTest test, String d) {
        Mechanism mechanism = createMechanism(test.getMechanism(), test.getClassName());
        Specification specification = createSpecification(test.getSpecification(), mechanism);
        List<InformationElement> vie = createInformationElements(test.getParameters());

        Dimension dimension = new Dimension(d);

        ContextualizedDimension context = new ContextualizedDimension();
        context.setDimension(dimension);
        context.setResourceType(ResourceType.SINGLE_RECORD);
        context.setInformationElements(vie);
        saveBean(context);

        MeasurementMethod method = new MeasurementMethod(test.getGuid());
        method.setSpecification(specification);
        method.setContextualizedDimension(context);
        saveBean(method);
    }

    public void createMeasure(AssertionTest test, Map<String, String> record, DQMeasurementResponse response) {
        MeasurementMethod method = (MeasurementMethod) fetchBean(test.getGuid(), MeasurementMethod.class);
        Mechanism mechanism = (Mechanism) fetchBean(test.getMechanism(), Mechanism.class);
        Specification specification = method.getSpecification();

        ContextualizedDimension context = method.getContextualizedDimension();

        DataResource dataResource = new DataResource(record);

        // TODO: process response and add result

        Measure measure = new Measure();
        measure.setMechanism(mechanism);
        measure.setSpecification(specification);
        measure.setDimension(context);
        measure.setDataResource(dataResource);
        saveBean(measure);
    }

    public void createValidationMethod(AssertionTest test, String c) {
        Mechanism mechanism = createMechanism(test.getMechanism(), test.getClassName());
        Specification specification = createSpecification(test.getSpecification(), mechanism);
        List<InformationElement> vie = createInformationElements(test.getParameters());

        Criterion criterion = new Criterion(c);

        ContextualizedCriterion context = new ContextualizedCriterion();
        context.setCriterion(criterion);
        context.setResourceType(ResourceType.SINGLE_RECORD);
        context.setInformationElements(vie);
        saveBean(context);

        ValidationMethod method = new ValidationMethod(test.getGuid());
        method.setSpecification(specification);
        method.setContextualizedCriterion(context);
        saveBean(method);
    }

    public void createValidation(AssertionTest test, Map<String, String> record, DQValidationResponse response) {
        ValidationMethod method = (ValidationMethod) fetchBean(test.getGuid(), ValidationMethod.class);
        Mechanism mechanism = (Mechanism) fetchBean(test.getMechanism(), Mechanism.class);
        Specification specification = method.getSpecification();

        ContextualizedCriterion context = method.getContextualizedCriterion();

        DataResource dataResource = new DataResource(record);

        // TODO: process response and add result

        Validation validation = new Validation();
        validation.setMechanism(mechanism);
        validation.setSpecification(specification);
        validation.setCriterion(context);
        validation.setDataResource(dataResource);
        saveBean(validation);
    }

    public void createAmendmentMethod(AssertionTest test, String e) {
        Mechanism mechanism = createMechanism(test.getMechanism(), test.getClassName());
        Specification specification = createSpecification(test.getSpecification(), mechanism);
        List<InformationElement> vie = createInformationElements(test.getParameters());

        Enhancement enhancement = new Enhancement(e);

        ContextualizedEnhancement context = new ContextualizedEnhancement();
        context.setEnhancement(enhancement);
        context.setResourceType(ResourceType.SINGLE_RECORD);
        context.setInformationElements(vie);
        saveBean(context);

        AmendmentMethod method = new AmendmentMethod(test.getGuid());
        method.setSpecification(specification);
        method.setContextualizedEnhancement(context);
        saveBean(method);
    }

    public void createAmendment(AssertionTest test, Map<String, String> record, DQAmendmentResponse response) {
        AmendmentMethod method = (AmendmentMethod) fetchBean(test.getGuid(), AmendmentMethod.class);
        Mechanism mechanism = (Mechanism) fetchBean(test.getMechanism(), Mechanism.class);
        Specification specification = method.getSpecification();

        ContextualizedEnhancement context = method.getContextualizedEnhancement();

        DataResource dataResource = new DataResource(record);

        // TODO: process response and add result

        Amendment amendment = new Amendment();
        amendment.setMechanism(mechanism);
        amendment.setSpecification(specification);
        amendment.setEnhancement(context);
        amendment.setDataResource(dataResource);
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
