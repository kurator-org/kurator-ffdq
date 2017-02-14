package org.datakurator.postprocess;

/**
 * Created by lowery on 2/13/2017.
 */
public class CuratedField {
    private String field;
    private String value;
    private String status;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
