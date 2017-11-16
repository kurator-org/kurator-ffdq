package org.datakurator.ffdq.runner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 12/19/16.
 */
public class RunnerStage {
    private String name;
    private List<AssertionTest> validations = new ArrayList<>();
    private List<AssertionTest> measures = new ArrayList<>();
    private List<AssertionTest> amendments = new ArrayList<>();

    public RunnerStage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssertionTest> getValidations() {
        return validations;
    }

    public void setValidations(List<AssertionTest> validations) {
        this.validations = validations;
    }

    public List<AssertionTest> getMeasures() {
        return measures;
    }

    public void setMeasures(List<AssertionTest> measures) {
        this.measures = measures;
    }

    public List<AssertionTest> getAmendments() {
        return amendments;
    }

    public void setAmendments(List<AssertionTest> amendments) {
        this.amendments = amendments;
    }
}
