package org.datakurator.ffdq.runner;

import org.datakurator.ffdq.annotations.DQClass;
import org.datakurator.ffdq.annotations.DQParam;
import org.datakurator.ffdq.annotations.DQProvides;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.solutions.AmendmentMethod;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                processMethods();

                for (ValidationMethod validationMethod : model.findValidationMethods(mechanismGuid)) {
                    Specification specification = validationMethod.getSpecification();

                    ContextualizedCriterion cc = validationMethod.getContextualizedCriterion();
                    InformationElement informationElement = cc.getInformationElements();

                    AssertionTest test = tests.get(specification.getId());

//                    for (URI uri : informationElement.getComposedOf()) {
//                        System.out.println(uri);
//                    }
//
//                    for (TestParam param : test.getParameters()) {
//                        System.out.println(param.getURI());
//                    }

                    test.setSpecification(specification.getLabel());
                    test.setAssertionType("VALIDATION");

                    System.out.println(test.getMethod().getName());
                }

                for (MeasurementMethod measurementMethod : model.findMeasurementMethods(mechanismGuid)) {
                    Specification specification = measurementMethod.getSpecification();
                    AssertionTest test = tests.get(specification.getId());

                    test.setSpecification(specification.getLabel());
                    test.setAssertionType("MEASURE");

                    System.out.println(test.getMethod().getName());
                }

                for (AmendmentMethod amendmentMethod : model.findAmendmentMethods(mechanismGuid)) {
                    Specification specification = amendmentMethod.getSpecification();
                    AssertionTest test = tests.get(specification.getId());

                    test.setSpecification(specification.getLabel());
                    test.setAssertionType("AMENDMENT");

                    System.out.println(test.getMethod().getName());
                }
            }
        }
    }

    private void processMethods() {
        for (Method method : cls.getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation instanceof DQProvides) {
                    String guid = "urn:uuid:" + ((DQProvides) annotation).value();

                    List<TestParam> params = processParameters(method);
                    tests.put(guid, new AssertionTest(guid, cls, method, params));
                }
            }
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
