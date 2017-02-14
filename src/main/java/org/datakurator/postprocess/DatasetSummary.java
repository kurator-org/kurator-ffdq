package org.datakurator.postprocess;

import java.util.List;

/**
 * Created by lowery on 2/13/2017.
 */
public class DatasetSummary {
    private List<AssertionSummary> validations;
    private List<AssertionSummary> improvements;
    private List<MeasureSummary> measures;

    private CuratedDataset dataset;

    public List<AssertionSummary> getValidations() {
        return validations;
    }

    public void setValidations(List<AssertionSummary> validations) {
        this.validations = validations;
    }

    public List<AssertionSummary> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<AssertionSummary> improvements) {
        this.improvements = improvements;
    }

    public List<MeasureSummary> getMeasures() {
        return measures;
    }

    public void setMeasures(List<MeasureSummary> measures) {
        this.measures = measures;
    }

    public CuratedDataset getDataset() {
        return dataset;
    }

    public void setDataset(CuratedDataset dataset) {
        this.dataset = dataset;
    }
}
