package org.datakurator.postprocess.model;

/**
 * Created by lowery on 6/21/17.
 */
public class Measure extends Assertion {
    private String value;
    private String dimension;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }
}
