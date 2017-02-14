package org.datakurator.postprocess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 2/13/2017.
 */
public class AssertionRow {
    private String label;
    private List<CuratedField> record = new ArrayList<>();
    private String comment;
    private String status;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<CuratedField> getRecord() {
        return record;
    }

    public void setRecord(List<CuratedField> record) {
        this.record = record;
    }

    public void addValue(CuratedField value) {
        this.record.add(value);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
