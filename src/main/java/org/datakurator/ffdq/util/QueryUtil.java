/** QueryUtil.java
 *
 * Copyright 2017 President and Fellows of Harvard College
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
package org.datakurator.ffdq.util;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;

/**
 * Execute sparql queries on the ffdq rdf model
 *
 * @author mole
 * @version $Id: $Id
 */
public class QueryUtil {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws org.apache.commons.cli.ParseException if any.
     * @throws java.io.IOException if any.
     */
    public static void main(String[] args) throws ParseException, IOException {
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
