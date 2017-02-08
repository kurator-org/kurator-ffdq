package org.datakurator.data.ffdq;

import java.util.Map;

/**
 * Created by lowery on 2/8/2017.
 */
public class DataResource {
    private String recordId;

    private Map<String, String> initialValues;
    private Map<String, String> finalValues;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public void setInitialValues(Map<String, String> initialValues) {
        this.initialValues = initialValues;
    }

    public Map<String, String> getFinalValues() {
        return finalValues;
    }

    public void setFinalValues(Map<String, String> finalValues) {
        this.finalValues = finalValues;
    }
}
