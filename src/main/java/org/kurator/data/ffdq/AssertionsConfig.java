package org.kurator.data.ffdq;

import org.kurator.data.ffdq.assertions.Assertion;
import org.kurator.data.ffdq.assertions.Improvement;
import org.kurator.data.ffdq.assertions.Measure;
import org.kurator.data.ffdq.assertions.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 11/2/16.
 */
public class AssertionsConfig {
    private Map<String, Assertion> assertionMap = new HashMap<>();

    private List<Measure> measures = new ArrayList<>();
    private List<Validation> validations = new ArrayList<>();
    private List<Improvement> improvements = new ArrayList<>();

    public AssertionsConfig() { }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
        mapAssertions(measures);
    }

    private void mapAssertions(List<? extends Assertion> assertions) {
        for (Assertion assertion : assertions) {
            assertionMap.put(assertion.getContext().getName(), assertion);
        }
    }

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
        mapAssertions(validations);
    }

    public List<Improvement> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<Improvement> improvements) {
        this.improvements = improvements;
        mapAssertions(improvements);
    }

    public Assertion forContext(String name) {
        return assertionMap.get(name);
    }
}
