package org.datakurator.ffdq.util;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.datakurator.ffdq.model.*;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.context.ContextualizedDimension;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.datakurator.ffdq.rdf.Namespace;
import org.datakurator.ffdq.model.solutions.*;
import org.datakurator.ffdq.runner.AssertionTest;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by lowery on 11/13/17.
 */
public class TestUtil {
    private final static Logger logger = Logger.getLogger(TestUtil.class.getName());

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addRequiredOption("config", null, true, "Properties file defining the mechanism to use");
        options.addRequiredOption("in", null, true, "Input CSV file containing list of tests");
        options.addRequiredOption("out", null, true, "Output file for the rdf representation of the tests");

        options.addOption("format", null, true, "Output format (RDFXML, TURTLE, JSON-LD)");

        options.addOption("srcDir", null, true, "The Java sources root directory (e.g. src/main/java)");
        options.addOption("generateClass", null, false, "Generate a new Java class with stub methods for each test");
        options.addOption("appendClass", null, false, "Append to an existing Java class stub methods for new tests");

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            // Get option values
            String configFile = cmd.getOptionValue("config");

            String csvIn = cmd.getOptionValue("in");
            String rdfOut = cmd.getOptionValue("out");

            // Default output format is turtle
            RDFFormat format = RDFFormat.TURTLE;

            if (cmd.hasOption("format")) {
                String value = cmd.getOptionValue("format");

                switch (value) {
                    case "RDFXML":
                        format = RDFFormat.RDFXML;
                        break;
                    case "TURTLE":
                        format = RDFFormat.TURTLE;
                        break;
                    case "JSON-LD":
                        format = RDFFormat.JSONLD;
                        break;
                }
            }

            // Load the properties file
            Properties props = new Properties();
            props.load(new FileInputStream(configFile));

            // Create mechanism from properties file
            String mechanismGuid = props.getProperty("ffdq.mechanism.guid");
            String mechanismName = props.getProperty("ffdq.mechanism.name");

            String packageName = props.getProperty("ffdq.mechanism.javaPackage");
            String className = props.getProperty("ffdq.mechanism.javaClass");

            // Populate an ffdq model with metadata for each test from the csv
            FFDQModel model = new FFDQModel();

            List<AssertionTest> tests = parseCSV(csvIn);

            // Define a mechanism for the tests
            Mechanism mechanism = new Mechanism(mechanismGuid, mechanismName);

            for (AssertionTest test : tests) {

                // Define elementary concepts first
                Specification specification = new Specification(test.getGuid(), test.getSpecification());
                ResourceType resourceType = ResourceType.fromString(test.getResourceType());

                InformationElement informationElement = new InformationElement();

                for (String str : test.getInformationElement()) {
                    URI term = Namespace.resolvePrefixedTerm(str);
                    informationElement.addTerm(term);
                }

                // Add the specification to an implementation for the current mechanism
                Implementation implementation = new Implementation(specification, Collections.singletonList(mechanism));
                model.saveBean(implementation);

                // Define measure, validation, and amendment methods
                switch(test.getAssertionType().toUpperCase()) {
                    case "MEASURE":

                        // Define a dimension in the context of resource type and info elements
                        Dimension dimension = Dimension.fromString(test.getDimension());
                        ContextualizedDimension cd = new ContextualizedDimension(dimension, informationElement, resourceType);

                        // Define a measurement method, a specification tied to a dimension in context
                        MeasurementMethod measurementMethod = new MeasurementMethod(specification, cd);
                        model.saveBean(measurementMethod);
                        break;

                    case "VALIDATION":

                        // Define a criterion in the context of resource type and info elements
                        Criterion criterion = new Criterion(test.getDescription());
                        ContextualizedCriterion cc = new ContextualizedCriterion(criterion, informationElement, resourceType);

                        // Define a validation method, a specification tied to a criterion in context
                        ValidationMethod validationMethod = new ValidationMethod(specification, cc);
                        model.saveBean(validationMethod);
                        break;

                    case "AMENDMENT":

                        // Define an enhancement in the context of resource type and info elements
                        Enhancement enhancement = new Enhancement(test.getDescription());
                        ContextualizedEnhancement ce = new ContextualizedEnhancement(enhancement, informationElement, resourceType);

                        // Define an amendment method, a specification tied to a criterion in context
                        AmendmentMethod amendmentMethod = new AmendmentMethod(specification, ce);
                        model.saveBean(amendmentMethod);
                        break;
                }

            }

            // Write rdf to file
            FileOutputStream out = new FileOutputStream(rdfOut);
            model.write(format, out);
            logger.info("Wrote rdf for tests to: " + new File(rdfOut).getAbsolutePath());

            // Run DQ Class generation step if generateClass or appendClass options were set
            boolean generateClass = cmd.hasOption("generateClass");
            boolean appendClass = cmd.hasOption("appendClass");

            if (generateClass || appendClass) {
                File javaSrc = loadJavaSource(cmd.getOptionValue("srcDir"), packageName, className);
                ClassGenerator generator = new ClassGenerator(mechanismGuid, mechanismName, packageName, className);

                // Check if the source file exists, if so try to append if the appendClass option is set
                // otherwise generate a new class if the generateClass option is set
                if (javaSrc.exists()) {

                    if (!appendClass) {
                        throw new RuntimeException("Java source file already exists! Append to existing source via " +
                                "the \"appendClass\" option.");
                    } else {
                        // Initialize to append to an existing DQ Class
                        generator.init(new FileInputStream(javaSrc));
                    }

                } else {

                    if (!generateClass) {
                        throw new RuntimeException("Java source file does not exist! Try generating a new class" +
                                " via the \"generateClass\" option.");
                    } else {
                        // Initialize to generate a new DQ Class
                        generator.init();
                    }

                }

                // Add all assertion tests to the DQ Class generator
                for (AssertionTest test : tests) {
                    generator.addTest(test);
                }

                // Write generated class to java source file
                generator.writeOut(new FileOutputStream(javaSrc));
                logger.info("Wrote java source file for class to: " + javaSrc.getAbsolutePath());
            }
        } catch (ParseException e) {
            System.out.println("ERROR: " + e.getMessage() + "\n");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar TestUtil.jar", options);
        }

    }

    private static List<AssertionTest> parseCSV(String filename) throws IOException {
        File csvFile = new File(filename);

        if (!csvFile.exists()) {
            throw new FileNotFoundException("CSV input file not found: " + csvFile.getAbsolutePath());
        }

        List<AssertionTest> tests = new ArrayList<>();

        Reader reader = new InputStreamReader(new FileInputStream(csvFile));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

        for (CSVRecord record : records) {
            // Parse and validate csv records
            String guid = record.get("GUID");

            if (guid.isEmpty() || guid == null) {
                throw new IllegalArgumentException("Missing required GUID for test #" + record.getRecordNumber());
            }

            String label = record.get("Label");
            String description = record.get("Description");
            String specification = record.get("Specification");
            String assertionType = record.get("Type");
            String resourceType = record.get("Resource Type");
            String dimension = record.get("Dimension");
            String informationElement = record.get("Information Element");

            AssertionTest test = new AssertionTest(guid, label, description, specification, assertionType, resourceType,
                    dimension, parseInformationElementStr(informationElement));

            tests.add(test);
        }

        return tests;
    }

    private static File loadJavaSource(String srcDir, String packageName, String className) {
        // Convert the Java package name to directory and class name to source file
        String packageDir = packageName.replaceAll("\\.", File.separator);
        String sourceFile = className + ".java";

        // Load java source file
        File sourcesRoot;

        if (srcDir != null) {
            sourcesRoot = new File(srcDir);
        } else {
            // default to current directory if no srcDir was specified
            sourcesRoot = new File("");
        }

        File pkgDir = Paths.get(sourcesRoot.getAbsolutePath(), packageDir).toFile();
        if (!pkgDir.exists()) {
            pkgDir.mkdirs();
        }

        logger.info("Using sources root directory: " + sourcesRoot.getAbsolutePath());
        logger.info("Java package directory: " + packageDir);

        return Paths.get(pkgDir.getAbsolutePath(), sourceFile).toFile();
    }

    private static List<String> parseInformationElementStr(String str) {
        List<String> infoElems = new ArrayList<>();

        if (str.contains(",")) {
            for (String ie : str.split(",")) {
                infoElems.add(ie.trim());
            }
        } else {
            infoElems.add(str.trim());
        }

        return infoElems;
    }
}
