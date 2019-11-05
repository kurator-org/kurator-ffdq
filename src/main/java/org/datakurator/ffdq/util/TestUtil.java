
package org.datakurator.ffdq.util;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.datakurator.ffdq.model.*;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.context.ContextualizedDimension;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.Implementation;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.datakurator.ffdq.rdf.Namespace;
import org.datakurator.ffdq.runner.AssertionTest;
import org.datakurator.ffdq.runner.UnsupportedTypeException;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestUtil {
    private final static Logger logger = Logger.getLogger(TestUtil.class.getName());

    private final static String CSV_HEADER_LABEL;
    private final static String CSV_HEADER_DESCRIPTION;
    private final static String CSV_HEADER_SPECIFICATION;
    private final static String CSV_HEADER_ASSERTION;
    private final static String CSV_HEADER_RESOURCE_TYPE;
    private final static String CSV_HEADER_DIMENSION;
    private final static String CSV_HEADER_INFO_ELEMENT;
    private final static String CSV_HEADER_TEST_PARMETERS;

    static {
        Properties properties = new Properties();
        try {
            properties.load(TestUtil.class.getResourceAsStream("/config.properties"));

            CSV_HEADER_LABEL = properties.getProperty("csv.header.label");
            CSV_HEADER_DESCRIPTION = properties.getProperty("csv.header.description");
            CSV_HEADER_SPECIFICATION = properties.getProperty("csv.header.specification");
            CSV_HEADER_ASSERTION = properties.getProperty("csv.header.assertion");
            CSV_HEADER_RESOURCE_TYPE = properties.getProperty("csv.header.resourceType");
            CSV_HEADER_DIMENSION = properties.getProperty("csv.header.dimension");
            CSV_HEADER_INFO_ELEMENT = properties.getProperty("csv.header.informationElement");
            CSV_HEADER_TEST_PARMETERS = properties.getProperty("csv.header.testParameters");
            
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize properties from file config.properties", e);
        }
    }

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addRequiredOption("config", null, true, "Properties file defining the mechanism to use");
        options.addRequiredOption("in", null, true, "Input CSV file containing list of tests");
        options.addRequiredOption("out", null, true, "Output file for the rdf representation of the tests");

        options.addOption("format", null, true, "Output format (RDFXML, TURTLE, JSON-LD)");

        options.addOption("srcDir", null, true, "The Java sources root directory (e.g. src/main/java)");
        options.addOption("generateClass", null, false, "Generate a new Java class with stub methods for each test");
        options.addOption("appendClass", null, false, "Append to an existing Java class stub methods for new tests");

        options.addOption("generatePython", null, false, "Generate a new Python class with stub methods for each test");
        
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
                Specification specification = new Specification(test.getGuid(), test.getLabel(), test.getSpecification());
                ResourceType resourceType = ResourceType.fromString(test.getResourceType());

                InformationElement informationElement = new InformationElement();

                for (String str : test.getInformationElement()) {
                    URI term = Namespace.resolvePrefixedTerm(str);
                    informationElement.addTerm(term);
                }

                // Add the specification to an implementation for the current mechanism
                Implementation implementation = new Implementation(specification, Collections.singletonList(mechanism));
                model.save(implementation);

                // Define measure, validation, and amendment methods
                switch(test.getAssertionType().toUpperCase()) {
                    case "MEASURE":

                        // Define a dimension in the context of resource type and info elements
                        Dimension dimension = Dimension.fromString(test.getDimension());
                        ContextualizedDimension cd = new ContextualizedDimension(dimension, informationElement, resourceType);

                        // Define a measurement method, a specification tied to a dimension in context
                        MeasurementMethod measurementMethod = new MeasurementMethod(specification, cd);
                        model.save(measurementMethod);
                        break;

                    case "VALIDATION":

                        // Define a criterion in the context of resource type and info elements
                        Criterion criterion = new Criterion(test.getDescription());
                        ContextualizedCriterion cc = new ContextualizedCriterion(criterion, informationElement, resourceType);

                        // Define a validation method, a specification tied to a criterion in context
                        ValidationMethod validationMethod = new ValidationMethod(specification, cc);
                        model.save(validationMethod);
                        break;

                    case "AMENDMENT":

                        // Define an enhancement in the context of resource type and info elements
                        Enhancement enhancement = new Enhancement(test.getDescription());
                        ContextualizedEnhancement ce = new ContextualizedEnhancement(enhancement, informationElement, resourceType);

                        // Define an amendment method, a specification tied to a criterion in context
                        AmendmentMethod amendmentMethod = new AmendmentMethod(specification, ce);
                        model.save(amendmentMethod);
                        break;
                }

            }

            // Write rdf to file
            FileOutputStream out = new FileOutputStream(rdfOut);
            model.write(format, out);
            logger.info("Wrote rdf for tests to: " + new File(rdfOut).getAbsolutePath());

            // Generate python if requested.
            boolean generatePython = cmd.hasOption("generatePython");
            
            // Proof of concept python generation
            if (generatePython) { 
            	String sourceFile = className + ".py";
            	File pythonSrc = new File(sourceFile);
            	if (!pythonSrc.exists()) {  
            		PythonClassGenerator generator = new PythonClassGenerator(mechanismGuid, mechanismName, packageName, className);
            		generator.init();
            		for (AssertionTest test : tests) {
            			generator.addTest(test);
            		}

            		// Write generated class to python source file
            		generator.writeOut(new FileOutputStream(pythonSrc));
            		logger.info("Wrote python source file for class to: " + pythonSrc.getAbsolutePath());
            	} else { 
            		logger.info("Warning: Did not generate python code.  Python source file exists: " + pythonSrc.getAbsolutePath());
            	}
            }
            
            // Run DQ Class generation step if generateClass or appendClass options were set
            boolean generateClass = cmd.hasOption("generateClass");
            boolean appendClass = cmd.hasOption("appendClass");
            if (generateClass || appendClass) {
                File javaSrc = loadJavaSource(cmd.getOptionValue("srcDir"), packageName, className);
                JavaClassGenerator generator = new JavaClassGenerator(mechanismGuid, mechanismName, packageName, className);

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
            try {
                // Parse and validate csv records
                String guid = record.get("GUID");

                if (guid.isEmpty() || guid == null) {
                    throw new IllegalArgumentException("Missing required GUID for test #" + record.getRecordNumber());
                }

                String label = record.get(CSV_HEADER_LABEL);
                String description = record.get(CSV_HEADER_DESCRIPTION);
                String specification = record.get(CSV_HEADER_SPECIFICATION);
                String assertionType = record.get(CSV_HEADER_ASSERTION);
                String resourceType = record.get(CSV_HEADER_RESOURCE_TYPE);
                String dimension = record.get(CSV_HEADER_DIMENSION);
                String informationElement = record.get(CSV_HEADER_INFO_ELEMENT);
                String testParameters = record.get(CSV_HEADER_TEST_PARMETERS);

                AssertionTest test = new AssertionTest(guid, label, description, specification, assertionType, resourceType,
                        dimension, parseInformationElementStr(informationElement), parseTestParametersString(testParameters));

                tests.add(test);
            } catch (UnsupportedTypeException e) {
            	// skip record if not supported.
            	logger.log(Level.WARNING, "Unsupported Type, skipping test #" + record.getRecordNumber());
            	logger.log(Level.WARNING, e.getMessage(), e);
            } catch (IllegalArgumentException e) {
            	logger.log(Level.INFO, e.getMessage(), e);
                throw new RuntimeException("Could not find column header in input csv, the config.properties file might have incorrect mappings.", e);
            }
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

	/**
	 * Test Parameters are not currently structured, this method is a stub
	 * in case parsing is added.
	 * 
	 * @param testParameters string containing information about testParameters.
	 * @return
	 */
    private static List<String> parseTestParametersString(String testParameters) {
		List<String> result = new ArrayList<String>();
		result.add(testParameters);
		return result;
	}	
	
    /**
     * Information Elements are expected to be a comma delmited list of namespace:name pairs.
     * 
     * @param str the input string containing information elements
     * @return input elements as a list of strings, each element containing one namespace:name pair.
     */
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
