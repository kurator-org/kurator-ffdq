package org.datakurator.postprocess;

public class ValidationState {
    private String value = "";
    private String status;

    public void setValue(String value) {
        this.value = value;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public String toString() {
        return "ValidationState{" +
                "value='" + value + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
