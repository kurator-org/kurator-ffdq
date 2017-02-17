package org.datakurator.postprocess.xlsx;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private int state = DEFAULT;
    private Map<String, String> currObj;

    private String recordId;

    private Map<String, String> initialValues;
    private Map<String, String> finalValues;

    private Map<String, Object> assertion;
    private List<String> fieldsActedUpon;

    private Map<String, Map<String, String>> profile = new HashMap<>();

    public void postprocess(InputStream reportStream) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser parser = jsonFactory.createParser(reportStream);

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
                    currObj = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();
                    currObj.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    profile.put(currObj.remove("name"), currObj);
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    state = DEFAULT;
                    System.out.println(profile);
                }
            } else if (state == IN_REPORTS) {
                if (jsonToken.equals(JsonToken.START_ARRAY)) {
                    // TODO: Initialize the post processor here
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();

                    if (field.equals("recordId")) {
                        parser.nextValue();
                        recordId = parser.getValueAsString();
                    } else if (field.equals("assertions")) {
                        state = IN_ASSERTIONS;
                    } else if (field.equals("initialValues")) {
                        state = IN_INITIALVALS;
                    } else if (field.equals("finalValues")) {
                        state = IN_FINALVALS;
                    }
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    // TODO: Post process the result here
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    // TODO: End of report, close post processor here
                    parser.close();
                }
            } else if (state == IN_ASSERTIONS) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    assertion = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    if (field.equals("context")) {
                        state = IN_CONTEXT;
                    } else if (field.equals("result")) {
                        state = IN_RESULT;
                    } else {
                        parser.nextValue();
                        assertion.put(field, parser.getValueAsString());
                    }
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    System.out.println(assertion);
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    state = IN_REPORTS;
                }
            } else if (state == IN_INITIALVALS) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    initialValues = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();

                    initialValues.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    System.out.println(initialValues);
                    state = IN_REPORTS;
                }
            } else if (state == IN_FINALVALS) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    finalValues = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();

                    finalValues.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    System.out.println(finalValues);
                    state = IN_REPORTS;
                }
            } else if (state == IN_CONTEXT) {
                if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    if (field.equals("fieldsActedUpon")) {
                        state = IN_ACTED_UPON;
                    }
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    state = IN_ASSERTIONS;
                }
            } else if (state == IN_ACTED_UPON) {
                if (jsonToken.equals(JsonToken.START_ARRAY)) {
                    fieldsActedUpon = new ArrayList<>();
                } else if (jsonToken.equals(JsonToken.VALUE_STRING)) {
                    fieldsActedUpon.add(parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_ARRAY)) {
                    System.out.println(fieldsActedUpon);
                    state = IN_CONTEXT;
                }
            } else if (state == IN_RESULT) {
                if (jsonToken.equals(JsonToken.START_OBJECT)) {
                    currObj = new HashMap<>();
                } else if (jsonToken.equals(JsonToken.FIELD_NAME)) {
                    String field = parser.getCurrentName();
                    parser.nextValue();

                    currObj.put(field, parser.getValueAsString());
                } else if (jsonToken.equals(JsonToken.END_OBJECT)) {
                    assertion.put("result", currObj);
                    state = IN_ASSERTIONS;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        DQReportParser postprocessor = new DQReportParser();
        postprocessor.postprocess(DQReportParser.class.getResourceAsStream("/dq_report.json"));
    }
}
