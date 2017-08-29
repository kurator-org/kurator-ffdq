package org.datakurator.data.ffdq.sparql;

import org.apache.commons.io.IOUtils;
import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.datakurator.data.ffdq.ResultStatus;
import org.datakurator.data.ffdq.model.needs.ValidationPolicy;
import org.datakurator.data.ffdq.model.needs.Criterion;
import org.datakurator.data.ffdq.model.needs.InformationElement;
import org.datakurator.data.ffdq.model.needs.ResourceType;
import org.datakurator.data.ffdq.model.needs.UseCase;
import org.datakurator.data.ffdq.model.report.DataResource;
import org.datakurator.data.ffdq.model.report.Result;
import org.datakurator.data.ffdq.model.report.Validation;
import org.datakurator.data.ffdq.model.solutions.*;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Example sparql queries on the ffdq rdf model
 */
public class QueryUtil {

    public static void main(String [] args) throws URISyntaxException {
        // Initialize an in-memory store and run SPARQL query
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        try (RepositoryConnection conn = repo.getConnection()) {
            // Load RDF data from file
            conn.add(QueryUtil.class.getResourceAsStream("/example.ttl"), "", RDFFormat.TURTLE );

            RDFWriter writer = Rio.createWriter(RDFFormat.JSONLD, System.out);

            conn.prepareGraphQuery(QueryLanguage.SPARQL,
                    "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ").evaluate(writer);

            System.out.println();

            // Given a use case find the mechanisms used
            String sparql = loadSparql(QueryUtil.class.getResourceAsStream("/sparql/mechanism.sparql"));
            conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate(new SPARQLResultsTSVWriter(System.out));

            // Given a use case find the validation result status and valuable
            // information elements
            sparql = loadSparql(QueryUtil.class.getResourceAsStream("/sparql/results.sparql"));
            conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate(new SPARQLResultsTSVWriter(System.out));

            // Given a use case find the validation mechanisms,
            // implementation and valuable information elements
            //
            // The mechanism and specification guids link a
            // validation to the Java class and method that
            // implementing a specific standardized test.
            //
            // The valuable information elements are linked to
            // "fieldsActedUpon" and "fieldsConsulted" via
            // annotated method parameters
            sparql = loadSparql(QueryUtil.class.getResourceAsStream("/sparql/guids.sparql"));
            conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate(new SPARQLResultsTSVWriter(System.out));
        //} catch (RDFBeanException e) {
        //    e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadSparql(InputStream input) throws IOException {
        return IOUtils.toString(new InputStreamReader(input));
    }
}
