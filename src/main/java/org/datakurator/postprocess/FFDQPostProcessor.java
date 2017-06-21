package org.datakurator.postprocess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.datakurator.data.ffdq.AssertionsConfig;
import org.datakurator.data.ffdq.DQReport;
import org.datakurator.data.ffdq.DQReportBuilder;
import org.datakurator.data.ffdq.DataResource;
import org.datakurator.data.ffdq.assertions.*;
import org.datakurator.data.provenance.CurationStage;
import org.datakurator.postprocess.xlsx.ReportSummary;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 11/21/16.
 */
public class FFDQPostProcessor {
    private AssertionsConfig assertions;
    private List<DQReport> reports;

    public FFDQPostProcessor(InputStream report, InputStream config) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        reports = mapper.readValue(report, new TypeReference<List<DQReport>>(){});

        DQReportBuilder builder = new DQReportBuilder(config);
        assertions = builder.getAssertions();
    }

    public FFDQPostProcessor(List<DQReport> reports, InputStream config) {
        this.reports = reports;
    }

    public String measureSummary() throws IOException {
        List<MeasureSummary> measureSummaryList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        int length = assertions.getMeasures().size();

        for (int i = 0; i < length; i++) {
            DQMeasure measure = assertions.getMeasures().get(i);
            MeasureSummary summary = new MeasureSummary(measure);

            summary.postprocess(reports);
            measureSummaryList.add(summary);

            sb.append(summary.toJson());

            if (i < length-1) {
                sb.append(",");
            }
        }

        sb.append("]");

        return sb.toString();
    }

    public String curatedDataset() throws IOException {
        // Data resource
        CuratedDataset dataset = new CuratedDataset();
        Map<String, String> fields = new HashMap<>();

        for (DQReport report : reports) {
            DataResource data = report.getDataResource();
            CuratedRecord record = new CuratedRecord(data);

            // include the record id
            fields.put("recordId", "recordId");

            for (String field : data.getInitialValues().keySet()) {
                // TODO: mapping of field to label via config
                fields.put(field, field);
            }

            dataset.addRecord(record);
        }
        dataset.setFields(fields);

        // Measure summary
        List<MeasureSummary> measureSummaryList = new ArrayList<>();

        int length = assertions.getMeasures().size();

        for (DQMeasure measure : assertions.getMeasures()) {
            MeasureSummary summary = new MeasureSummary(measure);

            summary.postprocess(reports);
            measureSummaryList.add(summary);
        }

        DatasetSummary summary = new DatasetSummary();
        summary.setDataset(dataset);
        summary.setMeasures(measureSummaryList);
        summary.setImprovements(improvementSummary());
        summary.setValidations(validationSummary());

        // To json

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(summary);
    }

    public List<AssertionSummary> validationSummary() {
        List<AssertionSummary> summaryList = new ArrayList<>();

        for (DQReport report : reports) {
            AssertionSummary summary = new AssertionSummary();
            summary.setRecordId(report.getRecordId());

            List<DQValidation> validations = new ArrayList<>();

            for (DQReportStage stage : report.getStages()) {
                if (stage.getStage().equals(CurationStage.POST_ENHANCEMENT)) {
                    validations = stage.getValidations();
                }
            }

            for (DQValidation validation : validations) {
                String rowStatus = validation.getResult().getStatus();

                AssertionRow assertionRow = new AssertionRow();
                assertionRow.setLabel(validation.getLabel());
                assertionRow.setComment(commentString(validation));
                assertionRow.setStatus(rowStatus);

                Map<String, String> curatedValues = validation.getResult().getCuratedValues();
                for (String field : curatedValues.keySet()) {
                    CuratedField curatedField = new CuratedField();
                    curatedField.setField(field);
                    curatedField.setValue(curatedValues.get(field));

                    if (validation.getContext().getFieldsActedUpon().contains(field)) {
                        curatedField.setStatus(rowStatus);
                    }

                    assertionRow.addValue(curatedField);
                }

                summary.addAssertionRow(assertionRow);
            }

            summaryList.add(summary);
        }

        return summaryList;
    }

    public List<AssertionSummary> improvementSummary() {
        List<AssertionSummary> summaryList = new ArrayList<>();

        for (DQReport report : reports) {
            AssertionSummary summary = new AssertionSummary();
            summary.setRecordId(report.getRecordId());

            List<DQImprovement> improvements = new ArrayList<>();

            for (DQReportStage stage : report.getStages()) {
                if (stage.getStage().equals(CurationStage.ENHANCEMENT)) {
                    improvements = stage.getImprovements();
                }
            }

            for (DQImprovement improvement : improvements) {
                String rowStatus = improvement.getResult().getStatus();

                AssertionRow assertionRow = new AssertionRow();
                assertionRow.setLabel(improvement.getLabel());
                assertionRow.setComment(commentString(improvement));
                assertionRow.setStatus(rowStatus);

                Map<String, String> initialValues = improvement.getResult().getInitialValues();
                Map<String, String> curatedValues = improvement.getResult().getCuratedValues();

                for (String field : curatedValues.keySet()) {
                    CuratedField curatedField = new CuratedField();
                    curatedField.setField(field);

                    String curatedValue = curatedValues.get(field);
                    String initialValue = initialValues.get(field);

                    if (initialValue == null || initialValue.isEmpty()) {
                        initialValue = "empty"; // Placeholder for empty values
                    }

                    if (curatedValue == null || curatedValue.isEmpty()) {
                        curatedValue = "empty"; // Placeholder for empty values
                    }

                    // if the initial value is different than the currated value, add "CHANGED TO: ..."
                    curatedField.setValue(curatedValue.equals(initialValue) ? curatedValue : initialValue + " CHANGED TO: " + curatedValue);

                    if (improvement.getContext().getFieldsActedUpon().contains(field)) {
                        curatedField.setStatus(rowStatus);
                    }

                    assertionRow.addValue(curatedField);
                }

                summary.addAssertionRow(assertionRow);
            }

            summaryList.add(summary);
        }

        return summaryList;
    }

    private static String commentString(DQAssertion assertion) {
        StringBuilder comments = new StringBuilder();
        int count = 0;
        for (String comment : assertion.getResult().getComments()) {
            if (count > 0) {
                comments.append(" | ");
            }

            comments.append(comment);
        }

        return comments.toString();
    }
}
