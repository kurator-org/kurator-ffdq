package org.datakurator.data.ffdq.runner;

import org.datakurator.data.provenance.BaseRecord;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQAmendmentResponse;
import org.datakurator.ffdq.api.DQMeasurementResponse;
import org.datakurator.ffdq.api.DQValidationResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 12/14/16.
 */

public class ValidationRunner {
    private static final String RECORD_ID_FIELD = "occurrenceId";

    private Map<String, String> fields;

    private Class cls;

    private RunnerStage preEnhancementStage = new RunnerStage("PRE_ENHANCEMENT");
    private RunnerStage enhancementStage = new RunnerStage("ENHANCEMENT");
    private RunnerStage postEnhancementStage = new RunnerStage("POST_ENHANCEMENT");

    public ValidationRunner(Class cls) {
        this.cls = cls;
        //this.fields = fields;
        processMethods();
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    private void processParameters(ValidationTest test) {
        Method method = test.getMethod();

        Annotation[][] annotatedParams = test.getMethod().getParameterAnnotations();

        for (Annotation[] annotatedParam : annotatedParams) {
            if (annotatedParam.length > 0) {
                ValidationParam param = new ValidationParam();

                for (Annotation annotation : annotatedParam) {
                    if (annotation.annotationType().equals(ActedUpon.class)) {
                        ActedUpon actedUpon = (ActedUpon) annotation;
                        param.setTerm(actedUpon.value());
                        param.setUsage(ValidationParam.ACTED_UPON);
                    } else if (annotation.annotationType().equals(Consulted.class)) {
                        Consulted consulted = (Consulted) annotation;
                        param.setTerm(consulted.value());
                        param.setUsage(ValidationParam.CONSULTED);
                    }

                    test.addParam(param);
                }
            }
        }
    }

    private void processMethods() {
        Method[] methods = cls.getDeclaredMethods();

        for (final Method method : methods) {
            if (method.isAnnotationPresent(Provides.class)) {
                // Parse method annotations
                Provides provides = method.getAnnotation(Provides.class);

                ValidationTest test = new ValidationTest(provides.value(), method);

                if (method.isAnnotationPresent(PreEnhancement.class)) {
                    addToStage(test, method, preEnhancementStage);
                } else if (method.isAnnotationPresent(PostEnhancement.class)) {
                    addToStage(test, method, postEnhancementStage);
                } else if (method.isAnnotationPresent(Amendment.class)) {
                    addToStage(test, method, enhancementStage);
                }

                // Parse parameter annotations
                processParameters(test);
            }

        }
        //System.out.println(tests);
    }

    private void addToStage(ValidationTest test, Method method, RunnerStage stage) {
        if(method.isAnnotationPresent(Measure.class)) {
            stage.getMeasures().add(test);
        } else if (method.isAnnotationPresent(Validation.class)) {
            stage.getValidations().add(test);
        } else if (method.isAnnotationPresent(Amendment.class)) {
            stage.getAmendments().add(test);
        }
    }

    public void validate(Map<String, String> record) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object instance = cls.newInstance();

        String recordId = record.get(RECORD_ID_FIELD);
        System.out.println("recordId: " + recordId);

        Map<String, String> initialValues = new HashMap<>(record);
        System.out.println("initialValues: " + initialValues);

        record = runStage(preEnhancementStage, record, instance);
        record = runStage(enhancementStage, record, instance);
        record = runStage(postEnhancementStage, record, instance);

        Map<String, String> finalValues = record;
        System.out.println("finalValues: " + finalValues);
        System.out.println();
    }

    private Map<String, String> runStage(RunnerStage stage, Map<String, String> record, Object instance) throws InvocationTargetException, IllegalAccessException {
        String stageName = stage.getName();

        System.out.println("stage: " + stageName);

        for (ValidationTest validation : stage.getValidations()) {
            DQValidationResponse retVal = (DQValidationResponse) validation.getMethod().invoke(instance, assembleArgs(validation, record));

            String name = validation.getName();
            String state = retVal.getResultState().getName();
            String result = retVal.getResult().name();
            String comment = retVal.getComment();
            List<String> actedUpon = validation.fieldsActedUpon();

            System.out.println("\tValidation { name=" + name + ", actedUpon=" + actedUpon + ", state=" + state + ", result=" + result +
                    ", comment=" + comment + " }");
        }

        for (ValidationTest measure : stage.getMeasures()) {
            DQMeasurementResponse retVal = (DQMeasurementResponse) measure.getMethod().invoke(instance, assembleArgs(measure, record));

            String name = measure.getName();
            String state = retVal.getResultState().getName();
            String result = retVal.getValue().toString();
            String comment = retVal.getComment();
            List<String> actedUpon = measure.fieldsActedUpon();

            System.out.println("\tMeasure { name=" + name + ", actedUpon=" + actedUpon + ", state=" + state + ", result=" + result +
                    ", comment=" + comment + " }");
        }

        Map<String, String> finalValues = new HashMap<>(record);

        for (ValidationTest amendment : stage.getAmendments()) {
            DQAmendmentResponse retVal = (DQAmendmentResponse) amendment.getMethod().invoke(instance, assembleArgs(amendment, record));

            String name = amendment.getName();
            String state = retVal.getResultState().getName();
            Map<String, String> result = retVal.getResult();
            String comment = retVal.getComment();
            List<String> actedUpon = amendment.fieldsActedUpon();

            finalValues.putAll(result);

            System.out.println("\tAmendment { name=" + name + ", actedUpon=" + actedUpon + ", state=" + state + ", result=" + result +
                    ", comment=" + comment + " }");
        }

        return finalValues;
    }


    private String[] assembleArgs(ValidationTest test, Map<String, String> record) {
            List<ValidationParam> inputs = test.getInputs();
            String[] args = new String[inputs.size()];

            for (int i = 0; i < args.length; i++) {
                ValidationParam input = inputs.get(i);
                String term = input.getTerm();
                String value = record.get(term);

                args[i] = value;
            }

            return args;
    }

}
