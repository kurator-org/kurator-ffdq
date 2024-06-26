
package org.datakurator.ffdq.util;

import org.apache.commons.cli.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.datakurator.ffdq.model.*;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.context.ContextualizedDimension;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;
import org.datakurator.ffdq.model.context.ContextualizedIssue;
import org.datakurator.ffdq.model.needs.AmendmentPolicy;
import org.datakurator.ffdq.model.needs.MeasurementPolicy;
import org.datakurator.ffdq.model.needs.ProblemPolicy;
import org.datakurator.ffdq.model.needs.UseCase;
import org.datakurator.ffdq.model.needs.ValidationPolicy;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.Implementation;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;
import org.datakurator.ffdq.model.solutions.ProblemMethod;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestUtil {
    private final static Logger logger = Logger.getLogger(TestUtil.class.getName());

    private final static String CSV_HEADER_LABEL;
    private final static String CSV_HEADER_VERSION;
    private final static String CSV_HEADER_DESCRIPTION;
    private final static String CSV_HEADER_CRITERION_LABEL;
    private final static String CSV_HEADER_SPECIFICATION;
    private final static String CSV_HEADER_ASSERTION;
    private final static String CSV_HEADER_RESOURCE_TYPE;
    private final static String CSV_HEADER_DIMENSION;
    private final static String CSV_HEADER_INFO_ELEMENT;
    private final static String CSV_HEADER_INFO_ELEMENT_ACTEDUPON;
    private final static String CSV_HEADER_INFO_ELEMENT_CONSULTED;
    private final static String CSV_HEADER_TEST_PARMETERS;
    private final static String CSV_HEADER_USECASES;

    static {
        Properties properties = new Properties();
        try {
            properties.load(TestUtil.class.getResourceAsStream("/config.properties"));

            CSV_HEADER_LABEL = properties.getProperty("csv.header.label");
            CSV_HEADER_VERSION = properties.getProperty("csv.header.version");
            CSV_HEADER_DESCRIPTION = properties.getProperty("csv.header.description");
            CSV_HEADER_CRITERION_LABEL = properties.getProperty("csv.header.criterionLabel");
            CSV_HEADER_SPECIFICATION = properties.getProperty("csv.header.specification");
            CSV_HEADER_ASSERTION = properties.getProperty("csv.header.assertion");
            CSV_HEADER_RESOURCE_TYPE = properties.getProperty("csv.header.resourceType");
            CSV_HEADER_DIMENSION = properties.getProperty("csv.header.dimension");
            CSV_HEADER_INFO_ELEMENT = properties.getProperty("csv.header.informationElement");
            CSV_HEADER_INFO_ELEMENT_ACTEDUPON = properties.getProperty("csv.header.actedUpon");
            CSV_HEADER_INFO_ELEMENT_CONSULTED = properties.getProperty("csv.header.consulted");
            CSV_HEADER_TEST_PARMETERS = properties.getProperty("csv.header.testParameters");
            CSV_HEADER_USECASES = properties.getProperty("csv.header.useCases");
            
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize properties from file config.properties", e);
        }
    }

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addRequiredOption("config", null, true, "Properties file defining the mechanism to use");
        options.addRequiredOption("in", null, true, "Input CSV file containing list of tests");
        options.addRequiredOption("out", null, true, "Output file for the rdf representation of the tests");
        
        options.addOption("useCaseFile", null, true, "Optional Input file containing UseCase-Test relationships, if not specfied, UseCases column in InputFile will be used, if specified, will override InputFile.");
        options.addOption("guidFile", null, true, "Optional Input file containing Method/Contexturalized/Policy guids for each test.");
        
        options.addOption("format", null, true, "Output format (RDFXML, TURTLE, JSON-LD)");

        options.addOption("srcDir", null, true, "The Java sources root directory (e.g. src/main/java)");
        options.addOption("generateClass", null, false, "Generate a new Java class with stub methods for each test");
        options.addOption("appendClass", null, false, "Append to an existing Java class stub methods for new tests");
        options.addOption("checkVersion", null, false, "Report on versions in an existing Java class for each test");

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
            
            // allow linking to UseCases
            boolean includeUseCasesFromFile = false;
            String useCaseFilename = null;
            if (cmd.hasOption("useCaseFile")) {
            	useCaseFilename = cmd.getOptionValue("useCaseFile");
            }
            
            boolean guidsProvided = false;
            String additionalGuidFilename = null;
            if (cmd.hasOption("guidFile")) { 
            	additionalGuidFilename = cmd.getOptionValue("guidFile");
            	guidsProvided = true;
            }
            
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

            MultiValuedMap<String, String> useCaseTestMap = new HashSetValuedHashMap<>();
            Map<String,UseCase> useCaseMap = new HashMap<String,UseCase>();
            if (useCaseFilename != null && useCaseFilename.length()>0) {
            	logger.info(useCaseFilename);
            	File useCaseFile = new File(useCaseFilename);
            	logger.info(Boolean.toString(useCaseFile.canRead()));
            	if (useCaseFile.canRead()) { 
            		try { 
            			FileReader reader = new FileReader(useCaseFile);
            			CSVParser csvParser = new CSVParser(reader,CSVFormat.DEFAULT.withFirstRecordAsHeader());
            			List<CSVRecord> useCaseList = csvParser.getRecords();
            			Iterator<CSVRecord> i = useCaseList.iterator();
            			while (i.hasNext()) { 
            				CSVRecord useCaseRecord = i.next();
            				String useCaseLabel = useCaseRecord.get("UseCase").trim();
            				if (!useCaseMap.containsKey(useCaseLabel)) { 
            					UseCase useCaseInstance = new UseCase();
            					useCaseInstance.setLabel(useCaseLabel);
            					useCaseInstance.setSubject(useCaseLabel.replace("bdq:", "https://rs.tdwg.org/bdq/terms/"));
            					useCaseMap.put(useCaseLabel, useCaseInstance);
            				}
            				String includedTests = useCaseRecord.get("LabelsOfTestsIncluded");
            				String[] bits = includedTests.split("[|]");
            				for (String bit: bits) {
            					String testLabel = bit.trim();
            					if (testLabel.length()>0) {
            						useCaseTestMap.put(testLabel, useCaseLabel);
            					}
            				}
            			}
            			includeUseCasesFromFile = true;
            		} catch (IOException e) { 
            			logger.warning(e.getMessage());
            		}
            	}
            }

            // Populate an ffdq model with metadata for each test from the csv
            FFDQModel model = new FFDQModel();

            List<AssertionTest> tests = parseCSV(csvIn);
            
            if (guidsProvided) { 
            	tests = addAdditionalGuidsFromFile(additionalGuidFilename, tests);
            }

            // Define a mechanism for the tests
            Mechanism mechanism = new Mechanism(mechanismGuid, mechanismName);

            for (AssertionTest test : tests) {
            	
            	logger.log(Level.INFO, test.getGuid());

                // Define elementary concepts first
                Specification specification = new Specification(test.getGuid(), test.getLabel(), test.getSpecification());
                ResourceType resourceType = ResourceType.fromString(test.getResourceType());

                InformationElement informationElement = new InformationElement();
                ActedUpon actedUpon = new ActedUpon();
                Consulted consulted = new Consulted();

               	StringBuilder label = new StringBuilder("Information Element  ");
               	String separator = "";
                for (String str : test.getInformationElement()) {
                	if (str!=null && str.length()>0) { 
                		URI term = Namespace.resolvePrefixedTerm(str);
                		informationElement.addTerm(term);
                		label.append(separator).append(str);
                		separator = ", ";
                	}
                }
                if (test.getInformationElement().size()>0) { 
                	informationElement.setLabel(label.toString());
                }
               	label = new StringBuilder("Information Element ActedUpon ");
               	separator = "";
                for (String str : test.getActedUpon()) {
                	if (str!=null && str.length()>0) { 
                		URI term = Namespace.resolvePrefixedTerm(str);
                		actedUpon.addTerm(term);
                		label.append(separator).append(str);
                		separator = ", ";
                	}
                }
                if (test.getActedUpon().size()>0) { 
                	actedUpon.setLabel(label.toString());
                }
               	label = new StringBuilder("Information Element Consulted ");
               	separator = "";
                for (String str : test.getConsulted()) {
                	if (str!=null && str.length()>0) { 
                		URI term = Namespace.resolvePrefixedTerm(str);
                		consulted.addTerm(term);
                		label.append(separator).append(str);
                		separator = ", ";
                	}
                }
                if (test.getConsulted().size()>0) { 
                	consulted.setLabel(label.toString());
                }

                // Add the specification to an implementation for the current mechanism
                Implementation implementation = new Implementation(specification, Collections.singletonList(mechanism));
                // TODO: Provide means to add implementations, cardinality may be wrong, list specification for one mechanism.
                if (0==1) { 
                	model.save(implementation);
                }
                
                // Load usecases from selected source (input csv for tests, or specified usecase-test file.
                Iterator<String> iuc = null;
                if (includeUseCasesFromFile) {
                	if (useCaseTestMap.get(test.getLabel()).size()>0) { 
                		iuc = useCaseTestMap.get(test.getLabel()).iterator();
                	}
                } else { 
                	iuc = test.getUseCases().iterator();
                	while (iuc.hasNext()) { 
                		String useCaseLabel = iuc.next();	
                		if (!useCaseMap.containsKey(useCaseLabel)) { 
                			UseCase useCaseInstance = new UseCase();
                			useCaseInstance.setLabel(useCaseLabel);
                			useCaseInstance.setSubject(useCaseLabel.replace("bdq:", "https://rs.tdwg.org/bdq/terms/"));
                			useCaseMap.put(useCaseLabel, useCaseInstance);
                		}
                	}
                	iuc = test.getUseCases().iterator();
                }
                // Define measure, validation, and amendment methods
                switch(test.getAssertionType().toUpperCase()) {
                    case "MEASURE":
                        // Define a dimension in the context of resource type and info elements
                        Dimension dimension = Dimension.fromString(test.getDimension());
                        dimension.setLabel(test.getCriterionLabel());
                        ContextualizedDimension cd = new ContextualizedDimension(dimension, informationElement, actedUpon, consulted, resourceType);
                        if (test.getContextualizedGuid()!=null) { 
                        	cd.setId(test.getContextualizedGuid());
                        }
                        cd.setLabel(test.getDescription() + " Measure of " + test.getDimension() +  " for " + resourceType.getLabel());
                        cd.setComment(test.getDescription());
                        // Define a measurement method, a specification tied to a dimension in context
                        MeasurementMethod measurementMethod = new MeasurementMethod(specification, cd);
                        if (test.getMethodGuid()!=null) { 
                        	measurementMethod.setId(test.getMethodGuid());
                        }
                        if (iuc!=null) { 
                        	while (iuc.hasNext()) { 
                        		String useCaseName = iuc.next();
                        		MeasurementPolicy pol = new MeasurementPolicy();
                        		pol.setDimensionInContext(cd);
                        		pol.setUseCase(useCaseMap.get(useCaseName));
                        		if (test.getPolicyGuid()!=null) { 
                        			pol.setId(test.getPolicyGuid());
                        		}
                        		model.save(pol);
                        	}
                        }
                        model.save(measurementMethod);
                        break;

                    case "VALIDATION":
                        // Define a criterion in the context of resource type and info elements
                        Criterion criterion = new Criterion(test.getCriterionLabel());
                        ContextualizedCriterion cc = new ContextualizedCriterion(criterion, informationElement, actedUpon, consulted, resourceType);
                        cc.setLabel(test.getDescription() + " Validation for " + resourceType.getLabel());
                        cc.setComment(test.getDescription());
                        if (test.getContextualizedGuid()!=null) { 
                        	cc.setId(test.getContextualizedGuid());
                        }
                        // Define a validation method, a specification tied to a criterion in context
                        ValidationMethod validationMethod = new ValidationMethod(specification, cc);
                        if (test.getMethodGuid()!=null) { 
                        	validationMethod.setId(test.getMethodGuid());
                        }
                        if (iuc!=null) { 
                        	while (iuc.hasNext()) { 
                        		String useCaseName = iuc.next();
                        		ValidationPolicy vp = new ValidationPolicy();
                        		vp.setCriterionInContext(cc);
                        		vp.setUseCase(useCaseMap.get(useCaseName));
                        		if (test.getPolicyGuid()!=null) { 
                        			vp.setId(test.getPolicyGuid());
                        		}
                         		model.save(vp);
                        	}
                        }
                        model.save(validationMethod);
                        break;

                    case "AMENDMENT":
                        // Define an enhancement in the context of resource type and info elements
                        Enhancement enhancement = new Enhancement(test.getCriterionLabel());
                        ContextualizedEnhancement ce = new ContextualizedEnhancement(enhancement, informationElement, actedUpon, consulted, resourceType);
                        ce.setLabel(test.getDescription() +  "Amedment for " + resourceType.getLabel());
                        ce.setComment(test.getDescription());
                        if (test.getContextualizedGuid()!=null) { 
                        	ce.setId(test.getContextualizedGuid());
                        }
                        // Define an amendment method, a specification tied to a criterion in context
                        AmendmentMethod amendmentMethod = new AmendmentMethod(specification, ce);
                        if (test.getMethodGuid()!=null) {
                        	amendmentMethod.setId(test.getMethodGuid());
                        }
                        if (iuc!=null) { 
                       		while (iuc.hasNext()) { 
                       			String useCaseName = iuc.next();
                       			AmendmentPolicy pol = new AmendmentPolicy();
                       			pol.setEnhancementInContext(ce);
                       			pol.setUseCase(useCaseMap.get(useCaseName));
                       			if (test.getPolicyGuid()!=null) { 
                       				pol.setId(test.getPolicyGuid());
                       			}
                       			model.save(pol);
                       		}
                        }
                        model.save(amendmentMethod);
                        break;
                    case "ISSUE":
                        // Define an enhancement in the context of resource type and info elements
                        Issue issue = new Issue(test.getCriterionLabel());
                        ContextualizedIssue ci = new ContextualizedIssue(issue, informationElement, actedUpon, consulted, resourceType);
                        ci.setLabel(test.getDescription() + " Issue for " + resourceType.getLabel());
                        ci.setComment(test.getDescription());
                        if (test.getContextualizedGuid()!=null) { 
                        	ci.setId(test.getContextualizedGuid());
                        }
                        // Define an amendment method, a specification tied to a criterion in context
                        ProblemMethod problemMethod = new ProblemMethod(specification, ci);
                        if (test.getMethodGuid()!=null) { 
                        	problemMethod.setId(test.getMethodGuid());
                        }
                        if (iuc!=null) { 
                       		while (iuc.hasNext()) { 
                       			String useCaseName = iuc.next();
                       			ProblemPolicy pol = new ProblemPolicy();
                       			pol.setIssueInContext(ci);
                       			pol.setUseCase(useCaseMap.get(useCaseName));
                       			if (test.getPolicyGuid()!=null) { 
                       				pol.setId(test.getPolicyGuid());
                       			}
                       			model.save(pol);
                       		}
                        }
                        model.save(problemMethod);
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
            boolean checkVersion = cmd.hasOption("checkVersion");
            if (checkVersion) { 
                File javaSrc = loadJavaSource(cmd.getOptionValue("srcDir"), packageName, className);
                JavaClassGenerator generator = new JavaClassGenerator(mechanismGuid, mechanismName, packageName, className);

                // Check if the source file exists, 
                if (javaSrc.exists()) {
                	generator.init(new FileInputStream(javaSrc), true);
                } else {
                	throw new RuntimeException("Specified java source file not found.  Unable to check test versions. ");
                }

                // Check all assertion tests against the class
                for (AssertionTest test : tests) {
                    generator.checkTest(test);
                }
            	
            }
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
                        generator.init(new FileInputStream(javaSrc), checkVersion);
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

    /**
     * Given a file containing additional guids and a list of tests, add the relevant guids to each test.
     * 
     * @param additionalGuidFilename file containing guids for method, contexturalized, and policy concepts for each test.
     * @param tests a list of AssertionTests to which to add additional guids
     * @return the provided list of tests, with guids added.
     */
    private static List<AssertionTest> addAdditionalGuidsFromFile(String additionalGuidFilename, List<AssertionTest> tests) {

        Map<String,String> methodMap = new HashMap<String,String>();
        Map<String,String> contextualizedMap = new HashMap<String,String>();
        Map<String,String> policyMap = new HashMap<String,String>();
        if (additionalGuidFilename != null && additionalGuidFilename.length()>0) {
        	logger.info(additionalGuidFilename);
        	File guidFile = new File(additionalGuidFilename);
        	logger.info(Boolean.toString(guidFile.canRead()));
        	if (guidFile.canRead()) { 
        		try { 
        			FileReader reader = new FileReader(guidFile);
        			CSVParser csvParser = new CSVParser(reader,CSVFormat.DEFAULT.withFirstRecordAsHeader());
        			List<CSVRecord> guidList = csvParser.getRecords();
        			Iterator<CSVRecord> i = guidList.iterator();
        			while (i.hasNext()) { 
        				CSVRecord guidRecord = i.next();
        				String testGuid = guidRecord.get("GUID").trim();
        				String method = guidRecord.get("Method").trim();
        				methodMap.put(testGuid, method);
        				String contextualized = guidRecord.get("Contextualized").trim();
        				contextualizedMap.put(testGuid, contextualized);
        				String poliicy = guidRecord.get("Policy").trim();
        				policyMap.put(testGuid, poliicy);
        			}
        			for (AssertionTest test : tests) {
        				if (methodMap.containsKey(test.getGuid())) { 
        					test.setMethodGuid(methodMap.get(test.getGuid()));
        				}
        				if (contextualizedMap.containsKey(test.getGuid())) { 
        					test.setContextualizedGuid(contextualizedMap.get(test.getGuid()));
        				}
        				if (policyMap.containsKey(test.getGuid())) { 
        					test.setPolicyGuid(policyMap.get(test.getGuid()));
        				}
        			}
        		} catch (IOException e) { 
        			logger.warning(e.getMessage());
        		}
        	}
        }
    	
    	return tests;
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
                String version = record.get(CSV_HEADER_VERSION);
                String description = record.get(CSV_HEADER_DESCRIPTION);
                String criterionLabel = record.get(CSV_HEADER_CRITERION_LABEL);
                String specification = record.get(CSV_HEADER_SPECIFICATION);
                String assertionType = record.get(CSV_HEADER_ASSERTION);
                String resourceType = record.get(CSV_HEADER_RESOURCE_TYPE);
                String dimension = record.get(CSV_HEADER_DIMENSION);
                String informationElement = "";
                String useCasesForTestString = record.get(CSV_HEADER_USECASES);
                if (record.isMapped(CSV_HEADER_INFO_ELEMENT)) { 
                	informationElement = record.get(CSV_HEADER_INFO_ELEMENT);
                }
                String actedUpon = record.get(CSV_HEADER_INFO_ELEMENT_ACTEDUPON);
logger.log(Level.INFO, CSV_HEADER_INFO_ELEMENT_ACTEDUPON);                
logger.log(Level.INFO, actedUpon);                
                String consulted = record.get(CSV_HEADER_INFO_ELEMENT_CONSULTED);
                String testParameters = record.get(CSV_HEADER_TEST_PARMETERS);
                logger.log(Level.FINE, assertionType);
                logger.log(Level.FINE, label);
                
                List<String> useCaseNames = new ArrayList<String>();
                if (useCasesForTestString!=null && useCasesForTestString.length()>0) { 
                	useCaseNames = parseUseCaseString(useCasesForTestString);
                }
                
                AssertionTest test = new AssertionTest(guid, label, version, description, criterionLabel, specification, assertionType, resourceType,
                        dimension, parseInformationElementStr(informationElement), parseInformationElementStr(actedUpon), parseInformationElementStr(consulted), parseTestParametersString(testParameters), useCaseNames);
                
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
    
    /**
     * UseCases are expected to be a comma delmited list.
     * 
     * @param str the input string containing use case names
     * @return input elements as a list of strings, each element containing one use case name.
     */
    private static List<String> parseUseCaseString(String str) {
        List<String> useCases = new ArrayList<>();

        if (str!=null && str.length()>0) { 
        	if (str.contains(",")) {
        		for (String ie : str.split(",")) {
        			useCases.add(ie.trim());
        		}
        	} else {
        		useCases.add(str.trim());
        	}
        }

        return useCases;
    }
}
