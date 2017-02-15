package org.datakurator.data.ffdq.runner;

import org.datakurator.data.provenance.BaseRecord;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQAmendmentResponse;
import org.datakurator.ffdq.api.DQMeasurementResponse;
import org.datakurator.ffdq.api.DQValidationResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 12/14/16.
 */

public class ValidationRunner {
    private static final int ACTED_UPON = 0;
    private static final int CONSULTED = 1;

    private Class cls;

    private RunnerStage preEnhancementStage = new RunnerStage();
    private RunnerStage enhancementStage = new RunnerStage();
    private RunnerStage postEnhancementStage = new RunnerStage();

    public ValidationRunner(Class cls) {
        this.cls = cls;
        processMethods();
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
                        param.setUsage(ACTED_UPON);
                    } else if (annotation.annotationType().equals(Consulted.class)) {
                        Consulted consulted = (Consulted) annotation;
                        param.setTerm(consulted.value());
                        param.setUsage(CONSULTED);
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
                } else if (method.isAnnotationPresent(Enhancement.class)) {
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

        runStage(preEnhancementStage, record, instance);
        runStage(enhancementStage, record, instance);
        runStage(postEnhancementStage, record, instance);

    }

    private void runStage(RunnerStage stage, Map<String, String> record, Object instance) throws InvocationTargetException, IllegalAccessException {
        for (ValidationTest validation : stage.getValidations()) {
            DQValidationResponse retVal = (DQValidationResponse) validation.getMethod().invoke(instance, assembleArgs(validation, record));


            System.out.println("Validation { name=" + validation.getName() + ", method=" + validation.getMethod().getName() +
                    ", state=" + retVal.getResultState().getName() + ", comment=" + retVal.getComment());
        }

        for (ValidationTest measure : stage.getMeasures()) {
            DQMeasurementResponse retVal = (DQMeasurementResponse) measure.getMethod().invoke(instance, assembleArgs(measure, record));

            System.out.println("Measure { name=" + measure.getName() + ", method=" + measure.getMethod().getName() +
                    ", state=" + retVal.getResultState().getName() + ", comment=" + retVal.getComment());
        }

        for (ValidationTest amendment : stage.getAmendments()) {
            DQAmendmentResponse retVal = (DQAmendmentResponse) amendment.getMethod().invoke(instance, assembleArgs(amendment, record));

            System.out.println("Amendment { name=" + amendment.getName() + ", method=" + amendment.getMethod().getName() +
                    ", state=" + retVal.getResultState().getName() + ", comment=" + retVal.getComment());
        }
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
