package org.datakurator.data.ffdq.assertions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 3/30/16.
 */
public class Result {
    private Map<String, String> initialValues;
    private Map<String, String> curatedValues;

    private List<String> comments;
    private String status;

    public Result() {} // default constructor for Jackson

    public Result(Map<String, String> initialValues, Map<String, String> curatedValues, List<String> comments, String status) {
        this.initialValues = initialValues;
        this.curatedValues = curatedValues;
        this.comments = comments;
        this.status = status;
    }

    public Result(Map<String, String> initialValues, List<String> comments, String status) {
        this.initialValues = initialValues;
        this.curatedValues = Collections.EMPTY_MAP;
        this.comments = comments;
        this.status = status;
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public void setInitialValues(Map<String, String> initialValues) {
        this.initialValues = initialValues;
    }

    public Map<String, String> getCuratedValues() {
        return curatedValues;
    }

    public void setCuratedValues(Map<String, String> curatedValues) {
        this.curatedValues = curatedValues;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
