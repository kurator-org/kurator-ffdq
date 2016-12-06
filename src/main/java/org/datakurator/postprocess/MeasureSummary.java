package org.datakurator.postprocess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.datakurator.data.ffdq.AssertionsConfig;
import org.datakurator.data.ffdq.DQReport;
import org.datakurator.data.ffdq.DataResource;
import org.datakurator.data.ffdq.assertions.DQMeasure;
import org.datakurator.data.ffdq.assertions.DQReportStage;
import org.datakurator.data.provenance.CurationStage;
import org.datakurator.data.provenance.CurationStatus;
import org.datakurator.data.provenance.Measure;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 11/21/16.
 */
public class MeasureSummary {
    private String title;
    private String namedContext;

    private int countTotalReports;

    private int countCompleteBefore = 0;
    private int countCompleteAfter = 0;

    public MeasureSummary(String title, String namedContext) {
        this.title = title;
        this.namedContext = namedContext;
    }

    public void postprocess(List<DQReport> reports) {
        countTotalReports = reports.size();

        for (DQReport report : reports) {
            for (DQReportStage stage : report.getStages()) {
                if (stage.getStage().equals((CurationStage.PRE_ENHANCEMENT)) &&
                        measureIsComplete(stage.getMeasures())) {
                    countCompleteBefore++;
                } else if (stage.getStage().equals((CurationStage.POST_ENHANCEMENT)) &&
                        measureIsComplete(stage.getMeasures())) {
                    countCompleteAfter++;
                }
            }
        }
    }

    private boolean measureIsComplete(List<DQMeasure> measures) {
        for (DQMeasure measure : measures) {
            if (measure.getContext().getName().equals(namedContext)) {
                return measure.getResult().getStatus().equals(CurationStatus.COMPLETE.name());
            }
        }

        // measure specified doesn't exist in the report
        throw new RuntimeException("Measure with context " + namedContext + " not found in dq report");
    }

    private double calculatePercentageComplete(int countComplete) {
        return (countComplete/countTotalReports)*100.0;
    }

    public String getTitle() {
        return title;
    }

    public double getBefore() {
        return calculatePercentageComplete(countCompleteBefore);
    }

    public double getAfter() {
        return calculatePercentageComplete(countCompleteAfter);
    }

    public double getTotal() {
        return countTotalReports;
    }

    public void writeXls(OutputStream out) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Summary");

        // create header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("measure");
        header.createCell(1).setCellValue("before");
        header.createCell(2).setCellValue("after");
        header.createCell(3).setCellValue("count");

        // measure summary
        Row summary = sheet.createRow(1);
        summary.createCell(0).setCellValue(getTitle());
        summary.createCell(1).setCellValue(getBefore());
        summary.createCell(2).setCellValue(getAfter());
        summary.createCell(3).setCellValue(getTotal());

        workbook.write(out);
        out.close();
        workbook.close();
    }


    public void writeJson(OutputStream out) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.writerWithDefaultPrettyPrinter().writeValue(out, this);
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<DQReport> reports = mapper.readValue(MeasureSummary.class.getResourceAsStream("/dq_report.json"),
                new TypeReference<List<DQReport>>(){});

        MeasureSummary summary = new MeasureSummary("Event Date Completeness", "eventDateIsNotEmpty");
        summary.postprocess(reports);

        FileOutputStream out = new FileOutputStream("test.xls");
        summary.writeXls(out);

        FileOutputStream outJson = new FileOutputStream("test.json");
        summary.writeJson(outJson);
    }
}
