package org.datakurator.ffdq.util;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.datakurator.ffdq.rdf.FFDQModel;
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
            String input = cmd.getOptionValue("t");
            String output = cmd.getOptionValue("o");

            // Default format is turtle
            RDFFormat format = RDFFormat.TURTLE;

            // Get format from file extension
            if (input.endsWith("jsonld")) {
                format = RDFFormat.JSONLD;
            } else if (input.endsWith("rdf")) {
                format = RDFFormat.RDFXML;
            } else {
                format = RDFFormat.TURTLE;
            }

            // Load RDF data from file into model
            FFDQModel model = new FFDQModel();
            model.load(new FileInputStream(input), format);

            // Load sparql query from file
            String sparql = loadSparql(new FileInputStream(cmd.getOptionValue("q")));

            // Execute query and save result to file
            model.executeQuery(sparql, new FileOutputStream(output));
        } else {
            // Print usage
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ffdq", options);
        }
    }

    private static String loadSparql(InputStream input) throws IOException {
        return IOUtils.toString(new InputStreamReader(input));
    }
}
