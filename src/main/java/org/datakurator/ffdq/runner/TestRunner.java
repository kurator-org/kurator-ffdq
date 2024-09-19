
/**
 * TestRunner.java
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
 *
 * @author mole
 * @version $Id: $Id
 */
package org.datakurator.ffdq.runner;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.ResultValue;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.model.*;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.context.Measure;
import org.datakurator.ffdq.model.context.Amendment;
import org.datakurator.ffdq.model.context.DataQualityNeed;
import org.datakurator.ffdq.model.report.*;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.AssertionMethod;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class TestRunner {
    private final static Logger logger = Logger.getLogger(TestRunner.class.getName());

    private Class cls;
    private FFDQModel model;

    private Map<String, AssertionTest> tests = new HashMap<>();

    private Mechanism mechanism;

    private List<ValidationMethod> validations = new ArrayList<>();
    private List<MeasurementMethod> measures = new ArrayList<>();
    private List<AmendmentMethod> amendments = new ArrayList<>();

    /**
     * <p>Constructor for TestRunner.</p>
     *
     * @param cls a {@link java.lang.Class} object.
     * @param model a {@link org.datakurator.ffdq.rdf.FFDQModel} object.
     */
    public TestRunner(Class cls, FFDQModel model) {
        this(cls, model, new HashMap<>());
    }

    /**
     * <p>Constructor for TestRunner.</p>
     *
     * @param cls a {@link java.lang.Class} object.
     * @param model a {@link org.datakurator.ffdq.rdf.FFDQModel} object.
     * @param params a {@link java.util.Map} object.
     */
    public TestRunner(Class cls, FFDQModel model, Map<String, Object> params) {
        this.cls = cls;
        this.model = model;

        for (Annotation annotation : cls.getAnnotations()) {

            // Find the class level annotation and get the value for mechanism guid
            if (annotation instanceof org.datakurator.ffdq.annotations.Mechanism) {

            	logger.log(Level.INFO, annotation.toString());
                       	
                String mechanismGuid = ((org.datakurator.ffdq.annotations.Mechanism) annotation).value();
                
                try { 
                mechanism = (Mechanism) model.findOne(mechanismGuid, Mechanism.class);

                // check to see if the annotation has a label value and override value from rdf if present
                String mechanismLabel = ((org.datakurator.ffdq.annotations.Mechanism) annotation).label();
                if (mechanismLabel != null) {
                    mechanism.setLabel(mechanismLabel);
                }
                } catch (Exception e) { 
                	logger.log(Level.SEVERE, e.getMessage());
                }

                // Query the BDQFFDQ model for test specifications implemented by the mechanism tied to the DQClass
                Map<String, Specification> definedTests = model.findSpecificationsForMechanism(mechanismGuid);
                
                // TODO: Implmement handling of tests as DataQualityNeed subclasses, rather than specifications
                // Map<String, DataQualityNeed> definedTests = model.findTestsForMechanism(mechanismGuid);
                
                logger.log(Level.INFO, "Specifications found for Mechanism in RDF: " + definedTests.size());

                // Process method level annotations and check that test methods in the DQClass are consistent
                // with Measurement, ValidationAssertion and AmendmentAssertion Methods defined in the RDF
                processMethods(cls, definedTests);

            }
        }
    }

    private void processMethods(Class cls, Map<String, Specification> definedTests) {

        HashMap<String, AssertionTest> implementedTests = new HashMap<>();

        // Get the test guid (specification) defined in the DQProvides method level annotation
        for (Method javaMethod : cls.getMethods()) {
            AssertionTest test = new AssertionTest();
            test.setMethod(javaMethod);
            test.setCls(cls);

            for (Annotation annotation : javaMethod.getAnnotations()) {

                if (annotation instanceof Provides) {
                    String guid = ((Provides) annotation).value();
                    
                    if (guid.startsWith("urn:uuid:")){ 
                    	guid = guid.replace("urn:uuid:", "");
                    }
                    if (!guid.startsWith("https://rs.tdwg.org/bdqcore/terms/")) { 
                    	guid = "https://rs.tdwg.org/bdqcore/terms/" + guid;
                    }

                    logger.log(Level.INFO, guid);
                    test.setGuid(guid);
                    implementedTests.put(guid, test);
                } else if (annotation instanceof org.datakurator.ffdq.annotations.Measure) {
                        String description = ((org.datakurator.ffdq.annotations.Measure) annotation).description();
                        String dimension = ((org.datakurator.ffdq.annotations.Measure) annotation).dimension().toString();
                        String label = ((org.datakurator.ffdq.annotations.Measure) annotation).label();

                        if (description != null) {
                            test.setDescription(description);
                        }

                        if (dimension != null) {
                            test.setDimension(dimension);
                        }

                        if (label != null) {
                            test.setLabel(label);
                        }
                    } else if (annotation instanceof org.datakurator.ffdq.annotations.Validation) {
                        String description = ((org.datakurator.ffdq.annotations.Validation) annotation).description();
                        String label = ((org.datakurator.ffdq.annotations.Validation) annotation).label();

                        if (description != null) {
                            test.setDescription(description);
                        }

                        if (label != null) {
                            test.setLabel(label);
                        }

                        test.setDescription(description);
                        test.setLabel(label);
                    } else if (annotation instanceof org.datakurator.ffdq.annotations.Amendment) {
                        String description = ((org.datakurator.ffdq.annotations.Amendment) annotation).description();
                        String label = ((org.datakurator.ffdq.annotations.Amendment) annotation).label();

                        if (description != null) {
                            test.setDescription(description);
                        }

                        if (label != null) {
                            test.setLabel(label);
                        }
                    } else if (annotation instanceof org.datakurator.ffdq.annotations.Specification) {
                        String specification = ((org.datakurator.ffdq.annotations.Specification) annotation).value();

                        test.setSpecification(specification);
                    }
            }

        }

        Set<String> definedGuids = new HashSet<>(definedTests.keySet());
        Set<String> implementedGuids = new HashSet<>(implementedTests.keySet());

        // Check that all tests defined for the current mechanism in the BDQFFDQ rdf correspond to a DQClass method
        // that implements the test
        if (!implementedGuids.containsAll(definedGuids)) {
            logger.warning("Java class missing implementation for tests defined in RDF!");

            Set<String> missingGuids = new HashSet<>(definedGuids);
            missingGuids.removeAll(implementedGuids);

            for (String guid : missingGuids) {
                logger.warning("Missing corresponding method in " + cls + " for test \"" +
                        definedTests.get(guid).getLabel() + "\": " + guid.substring(guid.lastIndexOf(":")+1));

                implementedTests.remove(guid);
            }
        }

        // Check that all test method in the DQClass have associated metadata in the form of Measurement, ValidationAssertion,
        // and AmendmentAssertion Methods in the BDQFFDQ rdf
        if (!definedGuids.containsAll(implementedGuids)) {
            logger.warning("Tests declared in Java class via @DQProvides missing corresponding definitions " +
                    "in the RDF!");

            Set<String> missingGuids = new HashSet<>(implementedGuids);
            missingGuids.removeAll(definedGuids);

            for (String guid : missingGuids) {
                logger.warning("Missing definition in RDF for Java method \"" +
                        implementedTests.get(guid).getLabel() + "\" in " + cls + ": " +
                        guid);

                implementedTests.remove(guid);
            }
        }

        // Include only tests that have both a corresponding implementation in the class and a definition in the rdf
        for (String guid : implementedTests.keySet()) {
            AssertionTest test = implementedTests.get(guid);

            // Add metadata for specification
            if (test.getSpecification() == null) {
                Specification specification = definedTests.get(guid);
                test.setSpecification(specification.getLabel());
            }

            // TODO: Assumed to be single record for now
            test.setResourceType(AssertionTest.SINGLE_RECORD);

            // Get the assertion method from the model and add metadata to the test
            AssertionMethod assertionMethod = model.findMethodForSpecification(guid);

            ResourceType resourceType;
            InformationElement informationElement;

            if (assertionMethod instanceof ValidationMethod) {

                ValidationMethod validationMethod = (ValidationMethod) assertionMethod;
                test.setAssertionType(AssertionTest.VALIDATION);
                validations.add(validationMethod);

                Validation cc = validationMethod.getValidation();
                if (test.getDescription() == null) {
                    test.setDescription(cc.getCriterion().getLabel());
                }

                test.setResourceType(cc.getResourceType().getLabel());

                informationElement = cc.getInformationElements();

            } else if (assertionMethod instanceof MeasurementMethod) {

                MeasurementMethod measurementMethod = (MeasurementMethod) assertionMethod;
                test.setAssertionType(AssertionTest.MEASURE);
                measures.add(measurementMethod);

                Measure cd = measurementMethod.getMeasure();
                if (test.getDimension() == null) {
                    test.setDimension(cd.getDimension().getLabel());
                }
                test.setResourceType(cd.getResourceType().getLabel());

                informationElement = cd.getInformationElements();

            } else if (assertionMethod instanceof AmendmentMethod) {

                AmendmentMethod amendmentMethod = (AmendmentMethod) assertionMethod;
                test.setAssertionType(AssertionTest.AMENDMENT);
                amendments.add(amendmentMethod);

                Amendment ce = amendmentMethod.getContextualizedEnhancement();
                if (test.getDescription() == null) {
                    test.setDescription(ce.getEnhancement().getLabel());
                }

                test.setResourceType(ce.getResourceType().getLabel());

                informationElement = ce.getInformationElements();

            } else {
                throw new UnsupportedOperationException("Unsupported assertion type: " + assertionMethod.getClass());
            }

            // Process parameter level annotations
            Method method = test.getMethod();
            try {
                List<TestParam> params = processParameters(method, informationElement);
                test.setParameters(params);
            } catch (Exception e) {
                throw new RuntimeException("Error processing parameters for method: " + cls.getName() + "." +
                        method.getName(), e);
            }

            tests.put(guid, test);
        }
    }

    private List<TestParam> processParameters(Method method, InformationElement informationElement) {
        Map<URI, TestParam> testParams = new HashMap<>();
        List<TestParam> params = new ArrayList<>();

        int index = 0;
        for (Parameter parameter : method.getParameters()) {

            for (Annotation annotation : parameter.getAnnotations()) {
                if (annotation instanceof ActedUpon || annotation instanceof Consulted) {
                    String term = null;

                    // Get the term name from annotation value
                    if (annotation instanceof ActedUpon) {
                        term = ((ActedUpon) annotation).value();
                    } else if (annotation instanceof Consulted) {
                        term = ((Consulted) annotation).value();
                    }

                    // Create a parameter from the annotation value and the variable name
                    TestParam param = new TestParam(term, index, parameter);
                    testParams.put(param.getURI(), param);
                    params.add(param);
                } else if (annotation instanceof org.datakurator.ffdq.annotations.Parameter) {
                    String term = ((org.datakurator.ffdq.annotations.Parameter) annotation).name();

                    TestParam param = new TestParam(term, index, parameter);
                    params.add(param);
                }
            }
            index++;
        }

        Set<URI> implementedParams = new HashSet<>(testParams.keySet());
        Set<URI> definedParams = new HashSet<>(informationElement.getComposedOf());

        if (!implementedParams.containsAll(definedParams)) {
            System.out.println("Information elements defined in RDF missing corresponding method parameter in " +
                    "DQClass!");

            Set<URI> missingParams = new HashSet<>(definedParams);
            missingParams.removeAll(implementedParams);

            for (URI uri : missingParams) {
                System.out.println("Class " + cls.getName() + " missing method parameter for information element: " +
                        uri);

                testParams.remove(uri);
            }
        }

        if (!definedParams.containsAll(implementedParams)) {
            logger.warning("Method parameters annotated with DQParam missing corresponding information elements " +
                    "in RDF!");

            Set<URI> missingParams = new HashSet<>(implementedParams);
            missingParams.removeAll(definedParams);

            for (URI uri : missingParams) {
                // Parameter variable name
                TestParam param = testParams.get(uri);

                logger.warning("Missing information element in rdf \"" + uri + "\" for arg " + param.getIndex() +
                        " (" + param.getTerm() + ") of method " + cls.getName() + "." + method.getName());

                testParams.remove(uri);
            }
        }

        return params;
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException if any.
     * @throws java.net.URISyntaxException if any.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        Options options = new Options();
        options.addRequiredOption("in", null, true, "Input occurrence tsv data file");
        options.addRequiredOption("out", null, true, "Output file for the rdf " +
                "representation of the dq report");

        options.addOption("format", null, true, "Input/output rdf format (RDFXML, " +
                "TURTLE, JSON-LD)");

        options.addRequiredOption("rdf", null, true, "Input file containing the rdf " +
                "representation of the tests");

        options.addRequiredOption("cls", null, true, "Fully qualified name of Java class" +
                " on the classpath to run tests from");

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            // Get option values
            String tsvIn = cmd.getOptionValue("in");
            String rdfIn = cmd.getOptionValue("rdf");

            String rdfOut = cmd.getOptionValue("out");

            String dqClass = cmd.getOptionValue("cls");


            // Default input/output format is turtle
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

            // Initialize the the ffdq model
            FFDQModel model = new FFDQModel();

            // Load test definitions from rdf file into model
            File rdfFile = new File(rdfIn);
            if (!rdfFile.exists()) {
                throw new FileNotFoundException("RDF input file not found: " + rdfFile.getAbsolutePath());
            }

            model.load(new FileInputStream(rdfFile), format);

            // Initialize test runner instance
            Class cls = TestRunner.class.getClassLoader().loadClass(dqClass);
            TestRunner runner = new TestRunner(cls, model);

            // Load occurrence data from tsv file
            File tsvFile = new File(tsvIn);

            if (!tsvFile.exists()) {
                throw new FileNotFoundException("Input occurrence data file not found: " + tsvFile.getAbsolutePath());
            }

            // Run tests for each record in the occurrence data input
            Reader reader = new InputStreamReader(new FileInputStream(tsvFile));
            Iterable<CSVRecord> records = CSVFormat.newFormat('\t').withFirstRecordAsHeader().parse(reader);

            for (CSVRecord record : records) {
                runner.run(record.toMap());
            }

            // Write dq report as rdf
            model.write(RDFFormat.TURTLE, new FileOutputStream(rdfOut),true);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load dq class!", e);
        } catch (ParseException e) {
            System.out.println("ERROR: " + e.getMessage() + "\n");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar TestRunner.jar", options);
        }
    }

    /**
     * <p>run.</p>
     *
     * @param record a {@link java.util.Map} object.
     */
    public void run(Map<String, String> record) {
        // Create a new data resource from the record and load triples via model
        DataResource dataResource = new DataResource(model.getVocab(), record);
        model.load(dataResource.asModel());

        try {
            Object instance = cls.getDeclaredConstructor().newInstance();

            // create a dq report object
            for (MeasurementMethod measurementMethod : measures) {
                Specification specification = measurementMethod.getSpecification();
                Measure dimension = measurementMethod.getMeasure();

                AssertionTest test = tests.get(specification.getId());

                Result result = invokeTest(test, instance, dataResource.asMap());

                MeasureAssertion measure = new MeasureAssertion();

                measure.setDimension(dimension);
                measure.setDataResource(dataResource.getURI());
                measure.setMechanism(mechanism);
                measure.setSpecification(specification);
                measure.setResult(result);

                model.save(measure);
            }

            for (ValidationMethod validationMethod : validations) {
                Specification specification = validationMethod.getSpecification();
                Validation criterion = validationMethod.getValidation();

                AssertionTest test = tests.get(specification.getId());

                Result result = invokeTest(test, instance, dataResource.asMap());

                ValidationAssertion validation = new ValidationAssertion();

                validation.setCriterion(criterion);
                validation.setDataResource(dataResource.getURI());
                validation.setMechanism(mechanism);
                validation.setSpecification(specification);
                validation.setResult(result);

                model.save(validation);
            }

            for (AmendmentMethod amendmentMethod : amendments) {
                Specification specification = amendmentMethod.getSpecification();
                Amendment enhancement = amendmentMethod.getContextualizedEnhancement();

                AssertionTest test = tests.get(specification.getId());

                Result result = invokeTest(test, instance, dataResource.asMap());

                AmendmentAssertion amendment = new AmendmentAssertion();

                amendment.setEnhancement(enhancement);
                amendment.setDataResource(dataResource.getURI());
                amendment.setMechanism(mechanism);
                amendment.setSpecification(specification);
                amendment.setResult(result);

                model.save(amendment);
            }
        } catch (RunnerException ex) {
            // This probably results from an exception being thrown by code running in the test method
            logger.warning(ex.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            // This probably indicates that the asserted class or method hasn't been found.
            throw new RuntimeException("Could not instantiate an instance of the DQClass: " + cls.getName(), e);
        } catch (IllegalArgumentException e) {
        	logger.warning(e.getMessage());
            throw new RuntimeException("Could not instantiate an instance of the DQClass: " + cls.getName(), e);
		} catch (InvocationTargetException e) {
        	logger.warning(e.getMessage());
            throw new RuntimeException("Could not instantiate an instance of the DQClass: " + cls.getName(), e);
		} catch (NoSuchMethodException e) {
        	logger.warning(e.getMessage());
            throw new RuntimeException("Could not instantiate an instance of the DQClass: " + cls.getName(), e);
		} catch (SecurityException e) {
        	logger.warning(e.getMessage());
            throw new RuntimeException("Could not instantiate an instance of the DQClass: " + cls.getName(), e);
		}
    }

    private Result invokeTest(AssertionTest test, Object instance, Map<String, String> record) throws RunnerException {
        Map<String, String> actedUpon = new HashMap<>();

        for (TestParam param : test.getParameters()) {
            String term = param.getTerm();
            String value = record.get(term);

            actedUpon.put(term, value);
        }

        try {
            DQResponse response = (DQResponse) test.invoke(instance, record);
            Result result = new Result();

            model.save(response.getResultState());
            result.setState(response.getResultState());

            if (response.getValue() != null) {
                ResultValue value = response.getValue();
                Entity entity = value.getEntity();

                if (value instanceof AmendmentValue) {
                    // Data Resource id from entity value
                    URI uri = (URI) entity.getValue();

                    // Load amended values from the data resource into model
                    Map<String, String> amendedValues = ((AmendmentValue) value).getObject();
                    DataResource dataResource = new DataResource(uri.toString(), model.getVocab(), amendedValues);

                    model.load(dataResource.asModel());
                }

                result.setEntity(entity);
                model.save(entity);
            }

            result.setComment(response.getComment());
            model.save(result);

            return result;
        } catch (InvocationTargetException e) {
            throw new RunnerException("Exception occured originating from test method: " + test.getMethod().getName(), e);
        }
    }
}
