package org.datakurator.postprocess;

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

    public void addRecord(CuratedRecord record) {
        records.add(record);
    }

    public List<CuratedRecord> getRecords() {
        return records;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
