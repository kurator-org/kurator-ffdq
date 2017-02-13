package org.datakurator.postprocess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jena.base.Sys;
import org.datakurator.data.ffdq.AssertionsConfig;
import org.datakurator.data.ffdq.DQReport;
import org.datakurator.data.ffdq.DQReportBuilder;
import org.datakurator.data.ffdq.DataResource;
import org.datakurator.data.ffdq.assertions.DQMeasure;
import org.datakurator.data.ffdq.assertions.DQReportStage;
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

        return dataset.toJson();
    }

    public void reportSummary(File outputFile) throws IOException {
        ReportSummary summary = new ReportSummary(reports);
        summary.toXls(outputFile);
    }
}
