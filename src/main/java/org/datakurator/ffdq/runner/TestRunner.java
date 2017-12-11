package org.datakurator.ffdq.runner;

import org.datakurator.ffdq.annotations.DQClass;
import org.datakurator.ffdq.annotations.DQParam;
import org.datakurator.ffdq.annotations.DQProvides;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.context.ContextualizedDimension;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
import org.datakurator.ffdq.model.solutions.AssertionMethod;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;
import org.datakurator.ffdq.model.solutions.ValidationMethod;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.datakurator.ffdq.rdf.Namespace;
import org.datakurator.postprocess.model.Assertion;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.filteredpush.qc.date.DwCEventDQ;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class TestRunner {
    private Class cls;
    private FFDQModel model;

    private Map<String, AssertionTest> tests = new HashMap<>();

    public TestRunner(Class cls, FFDQModel model) throws URISyntaxException {
        this.cls = cls;
        this.model = model;

        for (Annotation annotation : cls.getAnnotations()) {

            // Find the class level annotation and get the value for mechanism guid
            if (annotation instanceof DQClass) {
                String mechanismGuid = "urn:uuid:" + ((DQClass) annotation).value();

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

            // Get the assertion method from the model and add metadata to the test
            AssertionMethod assertionMethod = model.findMethodForSpecification(guid);
            InformationElement informationElement;
            String assertionType;

            if (assertionMethod instanceof ValidationMethod) {

                ValidationMethod validationMethod = (ValidationMethod) assertionMethod;
                assertionType = AssertionTest.VALIDATION;

                ContextualizedCriterion cc = validationMethod.getContextualizedCriterion();
                informationElement = cc.getInformationElements();

            } else if (assertionMethod instanceof MeasurementMethod) {

                MeasurementMethod measurementMethod = (MeasurementMethod) assertionMethod;
                assertionType = AssertionTest.MEASURE;

                ContextualizedDimension cd = measurementMethod.getContextualizedDimension();
                informationElement = cd.getInformationElements();

            } else if (assertionMethod instanceof AmendmentMethod) {

                AmendmentMethod amendmentMethod = (AmendmentMethod) assertionMethod;
                assertionType = AssertionTest.AMENDMENT;

                ContextualizedEnhancement ce = amendmentMethod.getContextualizedEnhancement();
                informationElement = ce.getInformationElements();

            } else {
                throw new UnsupportedOperationException("Unsupported assertion type: " + assertionMethod.getClass());
            }

            // Process parameter level annotations and create an instance of AssertionTest
            List<TestParam> params = processParameters(method, informationElement);
            AssertionTest test = new AssertionTest(guid, assertionType, cls, method, params);

            // Add metadata for specification
            Specification specification = definedTests.get(guid);
            test.setSpecification(specification.getLabel());

            tests.put(guid, new AssertionTest(guid, assertionType, cls, method, params));
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
        FFDQModel model = new FFDQModel();
        model.load(new FileInputStream("data/DwCEventDQ.ttl"), RDFFormat.TURTLE);

        TestRunner runner = new TestRunner(DwCEventDQ.class, model);
    }


}
