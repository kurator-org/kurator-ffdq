package org.datakurator.data.ffdq.sparql;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryLanguage;
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
import java.net.URISyntaxException;

/**
 * Execute sparql queries on the ffdq rdf model
 */
public class QueryUtil {

    public static void main(String[] args) throws URISyntaxException, ParseException, IOException {
        Options options = new Options();

        options.addOption("t", "triples", true, "File containing triples (jsonld or turtle).");
        options.addOption("q", "query", true, "File containing sparql query.");
        options.addOption("o", "out", true, "Output file for query result.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("t") && cmd.hasOption("q") && cmd.hasOption("o")) {
            // Get option values
            InputStream input = new FileInputStream(cmd.getOptionValue("t"));
            String output = cmd.getOptionValue("o");

            String sparql = loadSparql(new FileInputStream(cmd.getOptionValue("q")));

            // Execute query and save result to file
            executeQuery(input, output, sparql);
        } else {
            // Print usage
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar ffdq.jar", options);
        }
    }

    private static void executeQuery(InputStream input, String output, String sparql) {
        // Initialize an in-memory store and run SPARQL query
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();

        try (RepositoryConnection conn = repo.getConnection()) {

            // Load RDF data from file
            Model model = Rio.parse(input, "", RDFFormat.TURTLE);
            conn.add(model);

            if (sparql.contains("CONSTRUCT")) {

                // Sparql CONSTRUCT
                RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, new FileOutputStream(output));
                conn.prepareGraphQuery(QueryLanguage.SPARQL, sparql).evaluate(writer);

            } else if (sparql.contains("SELECT")) {

                // Sparql SELECT
                TupleQueryResultHandler handler = new SPARQLResultsTSVWriter(new FileOutputStream(output));
                conn.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate(handler);

            }

            System.out.println("Wrote sparql query to output file: " + output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadSparql(InputStream input) throws IOException {
        return IOUtils.toString(new InputStreamReader(input));
    }
}
