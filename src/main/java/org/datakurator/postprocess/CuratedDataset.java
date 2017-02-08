package org.datakurator.postprocess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 11/21/16.
 */
public class CuratedDataset {
    private List<CuratedRecord> records = new ArrayList<>();
    private Map<String, String> fields = new HashMap<>();

    public CuratedDataset(Map<String, String> fields) {
        this.fields = fields;
    }
    public CuratedDataset() {

    }

    public void addRecord(CuratedRecord record) {
        records.add(record);
    }

    public List<CuratedRecord> getRecords() {
        return records;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
