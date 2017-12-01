package org.datakurator.ffdq.rdf;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.datakurator.ffdq.model.Criterion;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.ResourceType;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.report.Assertion;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.datakurator.ffdq.runner.AssertionTest;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.*;
import java.util.List;

/**
 * Created by lowery on 11/14/17.
 */
public class FFDQModel {
    private Repository repo;
    private RepositoryConnection conn;
    private RDFBeanManager manager;

    public FFDQModel() {
        // Initialize an in-memory store and the rdf bean manager
        repo = new SailRepository(new MemoryStore());
        repo.initialize();

        conn = repo.getConnection();
        manager = new RDFBeanManager(conn);
    }

    public void saveBean(Object obj) {
        try {
            manager.add(obj);
        } catch (RDFBeanException e) {
            throw new RuntimeException("Could not process the rdf bean instance.", e);
        }
    }

    public Object fetchBean(String guid, Class cls) {
        try {
            Resource r = manager.getResource(guid, cls);
            return manager.get(r);
        } catch (RDFBeanException e) {
            throw new RuntimeException("Could not fetch the rdf bean instance.", e);
        }
    }

    public void load(InputStream input, RDFFormat format) throws IOException {
        // Load RDF data from file
        Model model = Rio.parse(input, "", format);
        conn.add(model);
    }

    public void write(RDFFormat format, OutputStream out) {
        RDFWriter writer = Rio.createWriter(format, out);
        try (RepositoryConnection conn = repo.getConnection()) {
            conn.prepareGraphQuery(QueryLanguage.SPARQL,
                    "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> " +
                       "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o . MINUS { ?s rdfbeans:bindingClass ?o } } ").evaluate(writer);
        }
    }

    public void executeQuery(String sparql, OutputStream out) {
        if (sparql.contains("CONSTRUCT")) {

            // Sparql CONSTRUCT
            RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);
            conn.prepareGraphQuery(QueryLanguage.SPARQL, sparql).evaluate(writer);

        } else if (sparql.contains("SELECT")) {

            // Sparql SELECT
            TupleQueryResultHandler handler = new SPARQLResultsTSVWriter(out);
            conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate(handler);

        }
    }

    /*
    public ValidationMethod getValidationMethod(String guid) {
        try (RepositoryConnection conn = repo.getConnection()) {
            TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL,
                    "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> " +
                            "PREFIX ffdq: <http://example.com/ffdq/> " +
                            "SELECT ?method " +
                            "WHERE " +
                            "{ ?method a ffdq:ValidationMethod . ?method ffdq:hasSpecification <urn:uuid:" + guid + "> }").evaluate();

            BindingSet solution = result.next();
            String id = solution.getValue("method").stringValue();

            return (ValidationMethod) fetchBean(id, ValidationMethod.class);
        }
    }

    public List<AssertionTest> getAllTests() {
        try (RepositoryConnection conn = repo.getConnection()) {
            TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL,
                    "PREFIX rdfbeans: <http://viceversatech.com/rdfbeans/2.0/> " +
                            "PREFIX ffdq: <http://example.com/ffdq/> " +
                            "SELECT ?method ?specification ?mechanism " +
                            "WHERE " +
                            "{ ?method a ffdq:ValidationMethod . " +
                            "?method ffdq:hasSpecification ?specification . " +
                            "?implementation ffdq:hasSpecification ?specification . " +
                            "?implementation ffdq:implementedBy ?mechanism }").evaluate();

            while (result.hasNext()) {
                BindingSet solution = result.next();

                String id = solution.getValue("method").stringValue();
                String guid = solution.getValue("specification").stringValue();
                String mechanism = solution.getValue("mechanism").stringValue();

                System.out.println();
                System.out.println(mechanism);
                System.out.println();

                ValidationMethod method = (ValidationMethod) fetchBean(id, ValidationMethod.class);
                Specification specification = method.getSpecification();

                ContextualizedCriterion cc = method.getContextualizedCriterion();

                Criterion criterion = cc.getCriterion();
                ResourceType rt = cc.getResourceType();
                InformationElement ie = cc.getInformationElements();

                System.out.println(specification.getId());
                System.out.println(specification.getLabel());
                System.out.println(criterion.getLabel());
                System.out.println(rt);
                System.out.println(ie.getComposedOf());

                AssertionTest test = new AssertionTest(
                        specification.getId(),
                        "",
                        criterion.getLabel(),
                        specification.getLabel(),
                        "VALIDATION", rt.toString(), null, )
            }
            return null;
            //return (ValidationMethod) fetchBean(id, ValidationMethod.class);
        }
    }*/

    public static void main(String[] args) {

    }
}
