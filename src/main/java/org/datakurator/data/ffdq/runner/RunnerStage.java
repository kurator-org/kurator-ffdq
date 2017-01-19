package org.datakurator.data.ffdq.runner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 12/19/16.
 */
public class RunnerStage {
    private List<ValidationTest> validations = new ArrayList<>();
    private List<ValidationTest> measures = new ArrayList<>();
    private List<ValidationTest> amendments = new ArrayList<>();

    public List<ValidationTest> getValidations() {
        return validations;
    }

    public void setValidations(List<ValidationTest> validations) {
        this.validations = validations;
    }

    public List<ValidationTest> getMeasures() {
        return measures;
    }

    public void setMeasures(List<ValidationTest> measures) {
        this.measures = measures;
    }

    public List<ValidationTest> getAmendments() {
        return amendments;
    }

    public void setAmendments(List<ValidationTest> amendments) {
        this.amendments = amendments;
    }
}
