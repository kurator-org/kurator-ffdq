package org.datakurator.postprocess.xlsx;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.datakurator.postprocess.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by lowery on 2/17/2017.
 */
public class DQReportParser {
    private static final int DEFAULT = -1;
    private static final int IN_PROFILE = 0;
    private static final int IN_REPORTS = 1;
    private static final int IN_ASSERTIONS = 2;
    private static final int IN_INITIALVALS = 3;
    private static final int IN_FINALVALS = 4;
    private static final int IN_CONTEXT = 5;
    private static final int IN_RESULT = 6;
    private static final int IN_ACTED_UPON = 7;
    private static final int IN_CONSULTED = 8;
    private static final int IN_REPORT_STATUS = 9;
    private static final int IN_VALIDATION_STATE = 10;
    private static final int IN_AMENDMENT_STATE = 11;

    private JsonParser parser;
    private int state = DEFAULT;
    private Object currObj;

    private String recordId;

    private Map<String, String> initialValues;
    private Map<String, String> finalValues;
    private Set<String> dataResourceFields = new HashSet<>();

    private Map<String, Object> currAssertion;
    private Context currContext;

    private Map<String, List<Assertion>> assertions = new HashMap<>();
    private Set<String> assertionFields = new HashSet<>();

    private List<String> fieldsActedUpon;
    private List<String> fieldsConsulted;

    private Map<String, Test> profile = new HashMap<>();

    private Map<String, String> validationState;
    private Map<String, String> amendmentState;

    public DQReportParser(InputStream reportStream) throws IOException {
        if (reportStream == null) {
            throw new NullPointerException("Report stream argument is null, does the file exist?");
        }

        JsonFactory jsonFactory = new JsonFactory();
        parser = jsonFactory.createParser(reportStream);
    }

    public String getRecordId() {
        return recordId;
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public Map<String, String> getFinalValues() {
        return finalValues;
    }

    public Map<String, String> getValidationState() {
        return validationState;
    }

    public Map<String, String> getAmendmentState() {
        return amendmentState;
    }

    public List<Assertion> getAssertions(String stage) {
        return assertions.get(stage);
    }

    public List<Validation> getValidations() {
        List<Validation> validations = new ArrayList<>();

        for (Assertion assertion : assertions.get("PRE_ENHANCEMENT")) {
            if (assertion.getTest().getType().equalsIgnoreCase("VALIDATION")) {
                validations.add((Validation) assertion);
            }
        }

        return validations;
    }

    public List<Measure> getMeasures() {
        List<Measure> measures = new ArrayList<>();

        for (Assertion assertion : assertions.get("PRE_ENHANCEMENT")) {
            if (assertion.getTest().getType().equalsIgnoreCase("MEASURE")) {
                measures.add((Measure) assertion);
            }
        }

        return measures;
    }

    public List<Improvement> getAmendments() {
        List<Improvement> amendments = new ArrayList<>();

        for (Assertion assertion : assertions.get("ENHANCEMENT")) {
            if (assertion.getTest().getType().equalsIgnoreCase("AMENDMENT")) {
                amendments.add((Improvement) assertion);
            }
        }

        return amendments;
    }

    public List<String> getDataResourceFields() {
        List<String> fields = new ArrayList<>();

        for (String field : dataResourceFields) {
            fields.add(field);
        }

        return fields;
    }

    public List<String> getAssertionFields() {
        List<String> fields = new ArrayList<>();

        for (String field : assertionFields) {
            fields.add(field);
        }

        return fields;
    }

    public boolean next() throws IOException {
        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();

            if (state == DEFAULT) {
                if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();

                    if (field.equals("profile")) {
                        state = IN_PROFILE;
                    } else if (field.equals("report")) {
                        state = IN_REPORTS;
                    }
                }
            } else if (state == IN_PROFILE) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    // Create the test profile
                    currObj = new Test();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();

                    parser.nextValue();
                    String value = parser.getValueAsString();

                    Test test = (Test) currObj;

                    switch (field) {
                        case "name":
                            test.setName(value);
                            break;
                        case "description":
                            test.setDescription(value);
                        case "specification":
                            test.setSpecification(value);
                        case "label":
                            test.setLabel(value);
                        case "type":
                            test.setType(value);
                        case "mechanism":
                            test.setMechanism(value);
                    }
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    Test test = (Test) currObj;
                    profile.put(test.getName(), test);
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    state = DEFAULT;
                }
            } else if (state == IN_REPORTS) {
                if (jsonToken.equals(JsonToken.START_ARRAY)) {
                    // TODO: Initialize the post processor here
                } else if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    assertions = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();

                    switch (field) {
                        case "recordId":
                            parser.nextValue();
                            recordId = parser.getValueAsString();
                            break;
                        case "assertions":
                            state = IN_ASSERTIONS;
                            break;
                        case "initialValues":
                            state = IN_INITIALVALS;
                            break;
                        case "finalValues":
                            state = IN_FINALVALS;
                            break;
                        case "reportStatus":
                            state = IN_REPORT_STATUS;
                            break;
                    }
            } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    // TODO: Post process the result here
                    return true;
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    // TODO: End of report, close post processor here
                    parser.close();
                }
            } else if (state == IN_ASSERTIONS) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    currAssertion = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();

                    switch (field) {
                        case "context":
                            state = IN_CONTEXT;
                            break;
                        case "result":
                            state = IN_RESULT;
                            break;
                        default:
                            parser.nextValue();
                            currAssertion.put(field, parser.getValueAsString());
                            break;
                    }
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    String type = (String) currAssertion.get("type");
                    String comment = (String) currAssertion.get("comment");
                    String status = (String) currAssertion.get("status");
                    Context context = (Context) currAssertion.get("context");

                    Test test = profile.get(currAssertion.get("name"));
                    Assertion assertion = null;

                    if ("VALIDATION".equalsIgnoreCase(type)) {
                        assertion = new Validation();
                    } else if ("MEASURE".equalsIgnoreCase(type)) {
                        Measure measure = new Measure();
                        measure.setValue((String) currAssertion.get("value"));

                        assertion = measure;
                    } else if ("AMENDMENT".equalsIgnoreCase(type)) {
                        Improvement improvement = new Improvement();
                        improvement.setEnhancement((String) currAssertion.get("enhancement"));
                        improvement.setResult((Map<String, String>) currAssertion.get("result"));

                        assertion = improvement;
                    }

                    assertion.setTest(test);
                    assertion.setContext(context);
                    assertion.setComment(comment);
                    assertion.setStatus(status);

                    List<Assertion> list = assertions.get(currAssertion.get("stage"));
                    if (list == null) {
                        list = new ArrayList<>();
                        assertions.put((String) currAssertion.get("stage"), list);
                    }

                    list.add(assertion);
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    state = IN_REPORTS;
                }
            } else if (state == IN_INITIALVALS) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    initialValues = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();

                    dataResourceFields.add(field);
                    initialValues.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    state = IN_REPORTS;
                }
            } else if (state == IN_FINALVALS) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    finalValues = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();

                    dataResourceFields.add(field);
                    finalValues.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    state = IN_REPORTS;
                }
            } else if (state == IN_REPORT_STATUS) {
                if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    if (field.equals("validationState")) {
                        state = IN_VALIDATION_STATE;
                    } else if (field.equals("amendmentState")) {
                        state = IN_AMENDMENT_STATE;
                    }
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    state = IN_REPORTS;
                }
            } else if (state == IN_VALIDATION_STATE) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    validationState = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();

                    validationState.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    state = IN_REPORT_STATUS;
                }
            } else if (state == IN_AMENDMENT_STATE) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    amendmentState = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();

                    amendmentState.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    state = IN_REPORT_STATUS;
                }
            } else if (state == IN_CONTEXT) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    currContext = new Context();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    if (field.equals("fieldsActedUpon")) {
                        state = IN_ACTED_UPON;
                    } else if (field.equals("fieldsConsulted")) {
                        state = IN_CONSULTED;
                    }
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    currAssertion.put("context", currContext);
                    state = IN_ASSERTIONS;
                }
            } else if (state == IN_ACTED_UPON) {
                if (jsonToken.equals(JsonToken.START_ARRAY)) {
                    fieldsActedUpon = new ArrayList<>();
                } else if (jsonToken.equals(JsonToken.VALUE_STRING)) {
                    String value = parser.getValueAsString();
                    fieldsActedUpon.add(value);
                    assertionFields.add(value);
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    currContext.setFieldsActedUpon(fieldsActedUpon);
                    state = IN_CONTEXT;
                }
            } else if (state == IN_CONSULTED) {
                if (jsonToken.equals(JsonToken.START_ARRAY)) {
                    fieldsConsulted = new ArrayList<>();
                } else if (jsonToken.equals(JsonToken.VALUE_STRING)) {
                    String value = parser.getValueAsString();
                    fieldsConsulted.add(value);
                    assertionFields.add(value);
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    currContext.setFieldsConsulted(fieldsConsulted);
                    state = IN_CONTEXT;
                }
            } else if (state == IN_RESULT) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    currObj = new HashMap<String, String>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();

                    Map<String, String> result = (Map<String, String>) currObj;
                    result.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    currAssertion.put("result", currObj);
                    state = IN_ASSERTIONS;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) throws IOException {
        DQReportParser parser = new DQReportParser(DQReportParser.class.getResourceAsStream("/mcz_test.json"));
            while (parser.next()) {
            String recordId = parser.getRecordId();
            System.out.println(recordId);
        }
    }

    public Map<String,Test> getProfile() {
        return null;
    }
}
