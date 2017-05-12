package org.datakurator.data.ffdq.runner;

import org.datakurator.data.ffdq.assertions.Result;
import org.datakurator.data.provenance.BaseRecord;
import org.datakurator.data.provenance.CurationStatus;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.Writer;
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
    private static final String RECORD_ID_FIELD = "dwc:occurrenceID";
    private String mechanism;
    private long count = 0;

    private Map<String, String> fields;

    private Class cls;

    private RunnerStage preEnhancementStage = new RunnerStage("PRE_ENHANCEMENT");
    private RunnerStage enhancementStage = new RunnerStage("ENHANCEMENT");
    private RunnerStage postEnhancementStage = new RunnerStage("POST_ENHANCEMENT");

    private Writer writer;

    public ValidationRunner(Class cls, Writer writer) {
        this.cls = cls;
        //this.fields = fields;
        this.writer = writer;

        for (Annotation annotation : cls.getDeclaredAnnotations()) {
            if (annotation.annotationType().equals(Mechanism.class)) {
                this.mechanism = ((Mechanism) annotation).value();
            }
        }

        try {
            writer.write("{");
            processMethods();
            writer.write("\"report\": [");
        } catch (Exception e) {
            throw new RuntimeException("Error writing dq report json", e);
        }
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

    private void processMethods() throws IOException {
        writer.write("\"profile\": ");

        JSONArray profileArr = new JSONArray();
        Method[] methods = cls.getDeclaredMethods();


        for (final Method method : methods) {
            if (method.isAnnotationPresent(Provides.class)) {
                // Parse method annotations and add dq assertions to report
                JSONObject json = new JSONObject();
                Provides provides = method.getAnnotation(Provides.class);
                json.put("name", provides.value());

                if(method.isAnnotationPresent(Measure.class)) {
                    Measure measure = method.getAnnotation(Measure.class);
                    json.put("type", "MEASURE");
                    json.put("label", measure.label());
                    json.put("description", measure.description());

                    Specification specification = method.getAnnotation(Specification.class);
                    json.put("specification", specification.value());
                    json.put("mechanism", mechanism);
                } else if (method.isAnnotationPresent(Validation.class)) {
                    Validation validation = method.getAnnotation(Validation.class);
                    json.put("type", "VALIDATION");
                    json.put("label", validation.label());
                    json.put("description", validation.description());

                    Specification specification = method.getAnnotation(Specification.class);
                    json.put("specification", specification.value());
                    json.put("mechanism", mechanism);
                } else if (method.isAnnotationPresent(Amendment.class)) {
                    Amendment amendment = method.getAnnotation(Amendment.class);
                    json.put("type", "AMENDMENT");
                    json.put("label", amendment.label());
                    json.put("description", amendment.description());

                    Specification specification = method.getAnnotation(Specification.class);
                    json.put("specification", specification.value());
                    json.put("mechanism", mechanism);
                }

                profileArr.add(json);

                // Create a validation test object for invoking the method
                ValidationTest test = new ValidationTest(provides.value(), method);

                if (method.isAnnotationPresent(PreEnhancement.class)) {
                    addToStage(test, method, preEnhancementStage);
                }

                if (method.isAnnotationPresent(PostEnhancement.class)) {
                    addToStage(test, method, postEnhancementStage);
                }

                if (method.isAnnotationPresent(Amendment.class)) {
                    addToStage(test, method, enhancementStage);
                }

                // Parse parameter annotations
                processParameters(test);
            }
        }

        writer.write(profileArr.toJSONString() + ",");
        writer.flush();
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

    public void validate(Map<String, String> record) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        Object instance = cls.newInstance();
        JSONObject json = new JSONObject();

        if (count > 0) {
            writer.write(", ");
        }

        String recordId = record.get(RECORD_ID_FIELD);
        json.put("recordId", recordId);

        Map<String, String> initialValues = new HashMap<>(record);

        JSONArray reportArr = new JSONArray();
        JSONObject reportStatus = new JSONObject();

        record = runStage(preEnhancementStage, record, instance, reportArr, reportStatus);
        record = runStage(enhancementStage, record, instance, reportArr, reportStatus);
        record = runStage(postEnhancementStage, record, instance, reportArr, reportStatus);
        json.put("reportStatus", reportStatus);
        json.put("assertions", reportArr);

        json.put("initialValues", new JSONObject(initialValues));

        Map<String, String> finalValues = record;
        json.put("finalValues", new JSONObject(finalValues));

        writer.write(json.toJSONString());
        writer.flush();

        count++;
    }

    private Map<String, String> runStage(RunnerStage stage, Map<String, String> record, Object instance, JSONArray reportArr, JSONObject reportStatus) throws InvocationTargetException, IllegalAccessException {
        for (ValidationTest validation : stage.getValidations()) {
            Map<String, String> validationState = new HashMap<>();

            DQValidationResponse retVal = (DQValidationResponse) validation.getMethod().invoke(instance, assembleArgs(validation, record));

            ResultState state = retVal.getResultState();

            if (!state.equals(EnumDQResultState.NOT_RUN)) {
                JSONObject json = new JSONObject();
                CurationStatus status = null;

                json.put("name", validation.getName());
                json.put("type", "VALIDATION");
                json.put("stage", stage.getName());
                json.put("context", createContext(validation.fieldsActedUpon(), validation.fieldsConsulted()));

                if (state.equals(EnumDQResultState.RUN_HAS_RESULT)) {
                    EnumDQValidationResult result = retVal.getResult();

                    if (result.equals(EnumDQValidationResult.COMPLIANT)) {
                        status = CurationStatus.COMPLIANT;
                    } else if (result.equals(EnumDQValidationResult.NOT_COMPLIANT)) {
                        status = CurationStatus.NOT_COMPLIANT;
                    }
                } else if (state.equals(EnumDQResultState.AMBIGUOUS)) {
                    status = CurationStatus.AMBIGUOUS;
                } else if (state.equals(EnumDQResultState.EXTERNAL_PREREQUISITES_NOT_MET)) {
                    status = CurationStatus.EXTERNAL_PREREQUISITES_NOT_MET;
                } else if (state.equals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET)) {
                    status = CurationStatus.DATA_PREREQUISITES_NOT_MET;
                }

                for (String field : validation.fieldsActedUpon()) {
                    if (!validationState.containsKey(field) || !validationState.get(field).equals(CurationStatus.NOT_COMPLIANT.name())) {
                        validationState.put(field, status.name());
                    }
                }

                json.put("status", status.name());
                json.put("comment", retVal.getComment());

                reportArr.add(json);

                if (stage.getName().equals("PRE_ENHANCEMENT")) {
                    reportStatus.put("validationState", new JSONObject(validationState));
                }
            }
        }

        for (ValidationTest measure : stage.getMeasures()) {
            JSONObject json = new JSONObject();

            DQMeasurementResponse retVal = (DQMeasurementResponse) measure.getMethod().invoke(instance, assembleArgs(measure, record));

            json.put("name", measure.getName());
            json.put("type", "MEASURE");
            json.put("stage", stage.getName());
            json.put("context", createContext(measure.fieldsActedUpon(), measure.fieldsConsulted()));

            CurationStatus status = CurationStatus.NOT_COMPLETE;
            ResultState state = retVal.getResultState();
            Object value = null;

            if (state.equals(EnumDQResultState.RUN_HAS_RESULT)) {
                status = CurationStatus.COMPLETE;
                value = retVal.getValue();
            } else if (state.equals(EnumDQResultState.AMBIGUOUS)) {
                status = CurationStatus.AMBIGUOUS;
            } else if (state.equals(EnumDQResultState.EXTERNAL_PREREQUISITES_NOT_MET)) {
                status = CurationStatus.EXTERNAL_PREREQUISITES_NOT_MET;
            } else if (state.equals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET)) {
                status = CurationStatus.DATA_PREREQUISITES_NOT_MET;
            }

            json.put("value", value);
            json.put("status", status.name());
            json.put("comment", retVal.getComment());

            reportArr.add(json);
        }

        Map<String, String> finalValues = new HashMap<>(record);

        for (ValidationTest amendment : stage.getAmendments()) {
            Map<String, String> amendmentState = new HashMap<>();

            JSONObject json = new JSONObject();

            DQAmendmentResponse retVal = (DQAmendmentResponse) amendment.getMethod().invoke(instance, assembleArgs(amendment, record));

            json.put("name", amendment.getName());
            json.put("type", "AMENDMENT");
            json.put("stage", stage.getName());
            json.put("context", createContext(amendment.fieldsActedUpon(), amendment.fieldsConsulted()));

            CurationStatus status = CurationStatus.NO_CHANGE;;
            ResultState state = retVal.getResultState();

            if (state.equals(EnumDQAmendmentResultState.CHANGED)) {
                status = CurationStatus.CURATED;
            } else if (state.equals(EnumDQAmendmentResultState.FILLED_IN)) {
                status = CurationStatus.FILLED_IN;
            } else if (state.equals(EnumDQAmendmentResultState.TRANSPOSED)) {
                status = CurationStatus.TRANSPOSED;
            }

            for (String field : amendment.fieldsActedUpon()) {
                if (!amendmentState.containsKey(field) || !amendmentState.get(field).equals(CurationStatus.CURATED.name()) &&
                        !amendmentState.get(field).equals(CurationStatus.FILLED_IN.name()) &&
                        !amendmentState.get(field).equals(CurationStatus.TRANSPOSED.name())) {

                    if (status != CurationStatus.NO_CHANGE) {
                        amendmentState.put(field, status.name());
                    }
                }
            }

            Map<String, String> result = retVal.getResult();
            finalValues.putAll(result);

            json.put("result", result);
            json.put("status", status.name());
            json.put("comment", retVal.getComment());

            reportArr.add(json);

            if (stage.getName().equals("ENHANCEMENT")) {
                reportStatus.put("amendmentState", new JSONObject(amendmentState));
            }
        }

        return finalValues;
    }

    private JSONObject createContext(List<String> fieldsActedUpon, List<String> fieldsConsulted) {
        JSONObject json = new JSONObject();

        JSONArray actedUpon = new JSONArray();
        for (String field : fieldsActedUpon) {
            actedUpon.add(field);
        }
        json.put("fieldsActedUpon", actedUpon);

        JSONArray consulted = new JSONArray();
        for (String field : fieldsConsulted) {
            consulted.add(field);
        }
        json.put("fieldsConsulted", consulted);

        return json;
    }

    public void close() throws IOException {
        writer.write("]}");
        writer.close();
        System.out.println("Wrote dq report containing " + count + " assertions.");
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
