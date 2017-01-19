package org.datakurator.postprocess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.datakurator.data.ffdq.AssertionsConfig;
import org.datakurator.data.ffdq.DQReport;
import org.datakurator.data.ffdq.DQReportBuilder;
import org.datakurator.data.ffdq.assertions.DQMeasure;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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


}
