package org.datakurator.data.ffdq.runner;

/**
 * Created by lowery on 12/15/16.
 */
public class ValidationParam {
    private String term;
    private String value;
    private int usage;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    @Override
    public String toString() {
        return "ValidationParam{" +
                "term='" + term + '\'' +
                ", value='" + value + '\'' +
                ", usage=" + usage +
                '}';
    }
}
