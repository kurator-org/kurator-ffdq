package org.datakurator.data.ffdq.rdf;

import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by lowery on 1/3/17.
 */
public class SparqlExample {
    private static String baseUri = "http://datakurator.org/ffdq/";


    private static Model model = ModelFactory.createDefaultModel();

    public static void main(String[] args) {
        // Specification
        Resource specification = model.createResource(baseUri + "specification/fillInFromAtomicParts");
        specification.addLiteral(model.createProperty("http://www.w3.org/2011/content#", "ContentAsText"), "Fill in eventDate based on values from atomic fields");

        // Mechanism
        Resource mechanism = model.createResource(baseUri + "mechanism/DateValidator")
        .addProperty(model.createProperty(baseUri + "implements"), specification);

        // Enhancement
        Resource enhancement = model.createResource(baseUri + "enhancements/fillInFromAtomicParts");
        addFieldsActedUpon(enhancement, "eventDate");
        addFieldsConsulted(enhancement, "verbatimEventDate", "startDayOfYear", "endDayOfYear", "year", "month", "day");
        enhancement.addProperty(model.createProperty(baseUri + "hasSpecification"), specification)
                .addProperty(model.createProperty(baseUri + "mechanism"), mechanism);


        Resource workflow = model.createResource(baseUri + "workflows/EventDateValidator");

        workflow.addProperty(model.createProperty(baseUri + "hasActor"), mechanism);

        model.write(System.out, "N3");

        System.out.println();
        System.out.println();

        // What fields in the records can possibly be amended by the workflow as a whole
        System.out.println("What fields in the records can possibly be amended by the workflow as a whole");
        System.out.println();

        String queryString = "PREFIX ffdq: <http://datakurator.org/ffdq/>\n" +
                             "SELECT ?field\n" +
                             "WHERE { <http://datakurator.org/ffdq/workflows/EventDateValidator> ffdq:hasActor ?mechanism .\n" +
                             "        ?mechanism ffdq:implements ?specification .\n" +
                             "        ?enhancement ffdq:hasSpecification ?specification .\n" +
                             "        ?enhancement ffdq:actedUpon ?field\n}";

        execQuery(queryString);

        // For a particular field that can be amended (or assessed), what other fields must also be represented in the records
        System.out.println("For a particular field that can be amended (or assessed), what other fields must also be represented in the records:");
        System.out.println();

        queryString =  "PREFIX ffdq: <http://datakurator.org/ffdq/>\n" +
                       "SELECT ?field \n" +
                       "WHERE { ?enhancement ffdq:actedUpon 'eventDate' .\n" +
                       "        ?enhancement ffdq:consulted ?field \n}";

        execQuery(queryString);

        // For all of the amendments that can be made by the workflow components, what are all of the fields required for all of the amendments to be possible
        System.out.println("For all of the amendments that can be made by the workflow components, what are all of the fields required for all of the amendments to be possible");
        System.out.println();

        queryString = "PREFIX ffdq: <http://datakurator.org/ffdq/>\n" +
                "SELECT ?field \n" +
                "WHERE { <http://datakurator.org/ffdq/workflows/EventDateValidator> ffdq:hasActor ?mechanism .\n" +
                "        ?mechanism ffdq:implements ?specification .\n" +
                "        ?enhancement ffdq:hasSpecification ?specification . \n" +
                "        { ?enhancement ffdq:actedUpon ?field } UNION { ?enhancement ffdq:consulted ?field }\n}";

        execQuery(queryString);

        //StmtIterator iter =
        //        model.listStatements(new SimpleSelector(subject, predicate, object);
    }

    private static void execQuery(String queryString) {
        System.out.println(queryString);
        System.out.println();

        Query query = QueryFactory.create(queryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet rs = qexec.execSelect();
        ResultSetFormatter.out(System.out, rs, query);
    }

    private static void addFieldsActedUpon(Resource amendment, String... dwcTerms) {
        for (String term : dwcTerms) {
            amendment.addProperty(model.createProperty(baseUri, "actedUpon"), term);
        }
    }

    private static void addFieldsConsulted(Resource amendment, String... dwcTerms) {
        for (String term : dwcTerms) {
            amendment.addProperty(model.createProperty(baseUri, "consulted"), term);
        }
    }
}
