package org.datakurator.ffdq.runner;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.datakurator.ffdq.annotations.DQClass;
import org.datakurator.ffdq.annotations.DQParam;
import org.datakurator.ffdq.annotations.DQProvides;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.*;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.context.ContextualizedDimension;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;
import org.datakurator.ffdq.model.report.Amendment;
import org.datakurator.ffdq.model.report.Measure;
import org.datakurator.ffdq.model.report.Result;
import org.datakurator.ffdq.model.report.Validation;
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

public class TestRunner {
    private Class cls;
    private FFDQModel model;

    private Map<String, AssertionTest> tests = new HashMap<>();

    private Mechanism mechanism;

    private List<ValidationMethod> validations = new ArrayList<>();
    private List<MeasurementMethod> measures = new ArrayList<>();
    private List<AmendmentMethod> amendments = new ArrayList<>();

    public TestRunner(Class cls, FFDQModel model) throws URISyntaxException {
        this.cls = cls;
        this.model = model;

        for (Annotation annotation : cls.getAnnotations()) {
            System.out.println(annotation);
            // Find the class level annotation and get the value for mechanism guid
            if (annotation instanceof DQClass) {

                String mechanismGuid = "urn:uuid:" + ((DQClass) annotation).value();
                mechanism = (Mechanism) model.findOne(mechanismGuid, Mechanism.class);

                // Query the FFDQ model for test specifications implemented by the mechanism tied to the DQClass
                Map<String, Specification> definedTests = model.findSpecificationsForMechanism(mechanismGuid);

                // Process method level annotations and check that test methods in the DQClass are consistent
                // with Measurement, Validation and Amendment Methods defined in the RDF
                processMethods(cls, definedTests);

            }
        }
    }

    private void processMethods(Class cls, Map<String, Specification> definedTests) {

        HashMap<String, Method> implementedTests = new HashMap<>();

        // Get the test guid (specification) defined in the DQProvides method level annotation
        for (Method javaMethod : cls.getMethods()) {

            for (Annotation annotation : javaMethod.getAnnotations()) {
                if (annotation instanceof DQProvides) {
                    String guid = "urn:uuid:" + ((DQProvides) annotation).value();
                    implementedTests.put(guid, javaMethod);
                }
            }

        }

        Set<String> definedGuids = new HashSet<>(definedTests.keySet());
        Set<String> implementedGuids = new HashSet<>(implementedTests.keySet());

        // Check that all tests defined for the current mechanism in the FFDQ rdf correspond to a DQClass method
        // that implements the test
        if (!implementedGuids.containsAll(definedGuids)) {
            System.out.println("Java class missing implementation for tests defined in RDF!");

            Set<String> missingGuids = new HashSet<>(definedGuids);
            missingGuids.removeAll(implementedGuids);

            for (String guid : missingGuids) {
                System.out.println("Missing corresponding method in " + cls + " for test \"" + definedTests.get(guid).getLabel() + "\": " + guid.substring(guid.lastIndexOf(":")+1));
                implementedTests.remove(guid);
            }
        }

        // Check that all test method in the DQClass have associated metadata in the form of Measurement, Validation,
        // and Amendment Methods in the FFDQ rdf
        if (!definedGuids.containsAll(implementedGuids)) {
            System.out.println("Tests declared in Java class via @DQProvides missing corresponding definitions in the RDF!");

            Set<String> missingGuids = new HashSet<>(implementedGuids);
            missingGuids.removeAll(definedGuids);

            for (String guid : missingGuids) {
                System.out.println("Missing definition in RDF for Java method \"" + implementedTests.get(guid).getName() + "\" in " + cls + ": " + guid.substring(guid.lastIndexOf(":")+1));
                implementedTests.remove(guid);
            }
        }

        // Include only tests that have both a corresponding implementation in the class and a definition in the rdf
        for (String guid : implementedTests.keySet()) {
            Method method = implementedTests.get(guid);

            // Create an instance of AssertionTest
            AssertionTest test = new AssertionTest(guid, cls, method);

            // Add metadata for specification
            Specification specification = definedTests.get(guid);
            test.setSpecification(specification.getLabel());

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

                ContextualizedCriterion cc = validationMethod.getContextualizedCriterion();
                test.setDescription(cc.getCriterion().getLabel());
                test.setResourceType(cc.getResourceType().getLabel());

                informationElement = cc.getInformationElements();

            } else if (assertionMethod instanceof MeasurementMethod) {

                MeasurementMethod measurementMethod = (MeasurementMethod) assertionMethod;
                test.setAssertionType(AssertionTest.MEASURE);
                measures.add(measurementMethod);

                ContextualizedDimension cd = measurementMethod.getContextualizedDimension();
                test.setDimension(cd.getDimension().getLabel());
                test.setResourceType(cd.getResourceType().getLabel());

                informationElement = cd.getInformationElements();

            } else if (assertionMethod instanceof AmendmentMethod) {

                AmendmentMethod amendmentMethod = (AmendmentMethod) assertionMethod;
                test.setAssertionType(AssertionTest.AMENDMENT);
                amendments.add(amendmentMethod);

                ContextualizedEnhancement ce = amendmentMethod.getContextualizedEnhancement();
                test.setDescription(ce.getEnhancement().getLabel());
                test.setResourceType(ce.getResourceType().getLabel());

                informationElement = ce.getInformationElements();

            } else {
                throw new UnsupportedOperationException("Unsupported assertion type: " + assertionMethod.getClass());
            }

            // Process parameter level annotations
            List<TestParam> params = processParameters(method, informationElement);
            test.setParameters(params);

            tests.put(guid, test);
        }
    }

    private List<TestParam> processParameters(Method method, InformationElement informationElement) {
        Map<URI, TestParam> testParams = new HashMap<>();

        int index = 0;
        for (Parameter parameter : method.getParameters()) {
            index++;

            for (Annotation annotation : parameter.getAnnotations()) {
                if (annotation instanceof DQParam) {
                    String term = ((DQParam) annotation).value();

                    // Create a parameter from the annotation value and the variable name
                    TestParam param = new TestParam(term, index, parameter);
                    testParams.put(param.getURI(), param);
                }
            }
        }

        Set<URI> implementedParams = new HashSet<>(testParams.keySet());
        Set<URI> definedParams = new HashSet<>(informationElement.getComposedOf());

        if (!implementedParams.containsAll(definedParams)) {
            System.out.println("Information elements defined in RDF missing corresponding method parameter in DQClass!");

            Set<URI> missingParams = new HashSet<>(definedParams);
            missingParams.removeAll(implementedParams);

            for (URI uri : missingParams) {
                System.out.println("Class " + cls.getName() + " missing method parameter for information element: " + uri);
                testParams.remove(uri);
            }
        }

        if (!definedParams.containsAll(implementedParams)) {
            System.out.println("Method parameters annotated with DQParam missing corresponding information elements in RDF!");

            Set<URI> missingParams = new HashSet<>(implementedParams);
            missingParams.removeAll(definedParams);

            for (URI uri : missingParams) {
                // Parameter variable name
                TestParam param = testParams.get(uri);

                System.out.println("Missing information element in rdf \"" + uri + "\" for arg " + param.getIndex() + " (" + param.getTerm() + ") of method " + cls.getName() + "." + method.getName());
                testParams.remove(uri);
            }
        }

        return new ArrayList<>(testParams.values());
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Options options = new Options();
        options.addRequiredOption("in", null, true, "Input occurrence tsv data file");
        options.addRequiredOption("out", null, true, "Output file for the rdf representation of the dq report");

        options.addOption("format", null, true, "Input/output rdf format (RDFXML, TURTLE, JSON-LD)");

        options.addRequiredOption("rdf", null, true, "Input file containing the rdf representation of the tests");
        options.addRequiredOption("cls", null, true, "Fully qualified name of Java class on the classpath to run tests from");

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

            // Load test definitions from rdf file into model
            File rdfFile = new File(rdfIn);

            if (!rdfFile.exists()) {
                throw new FileNotFoundException("RDF input file not found: " + rdfFile.getAbsolutePath());
            }

            FFDQModel model = new FFDQModel();
            model.load(new FileInputStream(rdfFile), format);

            // Init test runner instance
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
            model.write(RDFFormat.TURTLE, new FileOutputStream(rdfOut));

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load dq class!", e);
        } catch (ParseException e) {
            System.out.println("ERROR: " + e.getMessage() + "\n");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar TestRunner.jar", options);
        }
    }

        private void run(Map<String, String> record) {
        try {
            Object instance = cls.newInstance();

            // create a dq report object
            DataResource dataResource = new DataResource(record);

            for (MeasurementMethod measurementMethod : measures) {
                Specification specification = measurementMethod.getSpecification();
                ContextualizedDimension dimension = measurementMethod.getContextualizedDimension();

                AssertionTest test = tests.get(specification.getId());
                Result result = invokeTest(test, instance, record);

                Measure measure = new Measure();

                measure.setDimension(dimension);
                measure.setDataResource(dataResource);
                measure.setMechanism(mechanism);
                measure.setSpecification(specification);
                measure.setResult(result);

                model.save(measure);
            }

            for (ValidationMethod validationMethod : validations) {
                Specification specification = validationMethod.getSpecification();
                ContextualizedCriterion criterion = validationMethod.getContextualizedCriterion();

                AssertionTest test = tests.get(specification.getId());
                Result result = invokeTest(test, instance, record);

                Validation validation = new Validation();

                validation.setCriterion(criterion);
                validation.setDataResource(dataResource);
                validation.setMechanism(mechanism);
                validation.setSpecification(specification);
                validation.setResult(result);

                model.save(validation);
            }

            for (AmendmentMethod amendmentMethod : amendments) {
                Specification specification = amendmentMethod.getSpecification();
                ContextualizedEnhancement enhancement = amendmentMethod.getContextualizedEnhancement();

                AssertionTest test = tests.get(specification.getId());
                Result result = invokeTest(test, instance, record);

                Amendment amendment = new Amendment();

                amendment.setEnhancement(enhancement);
                amendment.setDataResource(dataResource);
                amendment.setMechanism(mechanism);
                amendment.setSpecification(specification);
                amendment.setResult(result);

                model.save(amendment);
            }

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not instantiate an instance of the DQClass: " + cls.getName(), e);
        }
    }

    private Result invokeTest(AssertionTest test, Object instance, Map<String, String> record) {
        Map<String, String> actedUpon = new HashMap<>();

        for (TestParam param : test.getParameters()) {
            String term = param.getTerm();
            String value = record.get(term);

            actedUpon.put(term, value);
        }

        try {
            DQResponse response = (DQResponse) test.invoke(instance, record);

            if (response.getResultState() == null) {
                throw new RuntimeException("Response object returned by " + test.getCls().getName() + "." + test.getMethod().getName() + " has a null value for resultState when run on input data: " + actedUpon);
            }

            Result result = new Result();

            model.save(response.getResultState());
            result.setResultState(response.getResultState());

            if (response.getValue() != null) {
                model.save(response.getValue());
                result.setResultValue(response.getValue());
            }

            result.setComment(response.getComment());
            model.save(result);

            return result;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Could not invoke test method: " + test.getCls().getName() + "." + test.getMethod().getName(), e);
        }
    }
}
