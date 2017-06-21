package org.datakurator.postprocess.model;

import java.util.Map;

/**
 * Created by lowery on 6/21/17.
 */
public class Improvement extends Assertion {
    private String enhancement;
    private Map<String, String> result;

    public String getEnhancement() {
        return enhancement;
    }

    public void setEnhancement(String enhancement) {
        this.enhancement = enhancement;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }
}
