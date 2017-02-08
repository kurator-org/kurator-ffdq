package org.datakurator.postprocess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.datakurator.data.ffdq.DataResource;
import org.datakurator.data.ffdq.assertions.Result;

import java.util.Map;

/**
 * Created by lowery on 11/21/16.
 */
public class CuratedRecord {
    private String recordId;
    private Map<String, String> initialValues;
    private Map<String, String> finalValues;

    public CuratedRecord(DataResource data) {
        this.initialValues = data.getInitialValues();
        this.finalValues = data.getFinalValues();
        this.recordId = data.getRecordId();
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public Map<String, String> getFinalValues() {
        return finalValues;
    }

    public String getRecordId() {
        return recordId;
    }
}
