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
            if (annotation instanceof DQClass) {
                String mechanismGuid = "urn:uuid:" + ((DQClass) annotation).value();

                // process methods
                Map<String, Specification> definedTests = model.findSpecificationsForMechanism(mechanismGuid);
                processMethods(cls, definedTests);

                for (String testGuid : tests.keySet()) {
                    Specification specification = definedTests.get(testGuid);
                    AssertionMethod assertionMethod = model.findMethodForSpecification(testGuid);

                    AssertionTest test = tests.get(specification.getId());
                    test.setSpecification(specification.getLabel());

                    if (assertionMethod instanceof ValidationMethod) {
                        ValidationMethod validationMethod = (ValidationMethod) assertionMethod;

                        ContextualizedCriterion cc = validationMethod.getContextualizedCriterion();
                        InformationElement informationElement = cc.getInformationElements();

                        validateParameters(test, informationElement);

                        test.setAssertionType("VALIDATION");
                    } else if (assertionMethod instanceof MeasurementMethod) {
                        MeasurementMethod measurementMethod = (MeasurementMethod) assertionMethod;

                        ContextualizedDimension cd = measurementMethod.getContextualizedDimension();
                        InformationElement informationElement = cd.getInformationElements();

                        test.setAssertionType("MEASURE");
                    } else if (assertionMethod instanceof AmendmentMethod) {
                        AmendmentMethod amendmentMethod = (AmendmentMethod) assertionMethod;

                        ContextualizedEnhancement ce = amendmentMethod.getContextualizedEnhancement();
                        InformationElement informationElement = ce.getInformationElements();

                        test.setAssertionType("AMENDMENT");
                    }
                }
            }
        }
    }

    private void validateParameters(AssertionTest test, InformationElement informationElement) {
        Set<URI> implementedParams = new HashSet<>();
        Set<URI> definedParams = new HashSet<>();

        Map<URI, TestParam> testParams = new HashMap<>();
        for (TestParam param : test.getParameters()) {
            testParams.put(param.getURI(), param);
        }

        for (URI uri : informationElement.getComposedOf()) {

        }
    }

    private void processMethods(Class cls, Map<String, Specification> definedTests) {

        HashMap<String, Method> implementedTests = new HashMap<>();

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

        if (!implementedGuids.containsAll(definedGuids)) {
            System.out.println("Java class missing implementation for tests defined in RDF!");

            Set<String> missingGuids = new HashSet<>(definedGuids);
            missingGuids.removeAll(implementedGuids);

            for (String guid : missingGuids) {
                System.out.println("Missing corresponding method in " + cls + " for test \"" + definedTests.get(guid).getLabel() + "\": " + guid.substring(guid.lastIndexOf(":")+1));
                implementedTests.remove(guid);
            }
        }

        if (!definedGuids.containsAll(implementedGuids)) {
            System.out.println("Tests declared in Java class via @DQProvides missing corresponding definitions in the RDF!");

            Set<String> missingGuids = new HashSet<>(implementedGuids);
            missingGuids.removeAll(definedGuids);

            for (String guid : missingGuids) {
                System.out.println("Missing definition in RDF for Java method \"" + implementedTests.get(guid).getName() + "\" in " + cls + ": " + guid.substring(guid.lastIndexOf(":")+1));
                implementedTests.remove(guid);
            }
        }

        // now add the tests that have both a corresponding implementation and a definition in the rdf
        for (String guid : implementedTests.keySet()) {
            Method method = implementedTests.get(guid);

            List<TestParam> params = processParameters(method);
            tests.put(guid, new AssertionTest(guid, cls, method, params));
        }
    }

    private List<TestParam> processParameters(Method method) {
        List<TestParam> params = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            for (Annotation annotation : parameter.getAnnotations()) {
                if (annotation instanceof DQParam) {
                    String term = ((DQParam) annotation).value();
                    params.add(new TestParam(term));
                }
            }
        }

        return params;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        FFDQModel model = new FFDQModel();
        model.load(new FileInputStream("data/DwCEventDQ.ttl"), RDFFormat.TURTLE);

        TestRunner runner = new TestRunner(DwCEventDQ.class, model);
    }


}
