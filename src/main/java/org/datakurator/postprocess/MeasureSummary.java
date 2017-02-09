package org.datakurator.postprocess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.datakurator.data.ffdq.AssertionsConfig;
import org.datakurator.data.ffdq.DQReport;
import org.datakurator.data.ffdq.DQReportBuilder;
import org.datakurator.data.ffdq.assertions.DQMeasure;
import org.datakurator.data.ffdq.assertions.DQReportStage;
import org.datakurator.data.provenance.CurationStage;
import org.datakurator.data.provenance.CurationStatus;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.List;

/**
 * Created by lowery on 11/21/16.
 */
public class MeasureSummary {
    private DQMeasure measure;

    private int countTotalReports;

    private int countCompleteBefore = 0;
    private int countCompleteAfter = 0;

    private int assurance = 0; // TODO: postprocessor should determine value for this

    public MeasureSummary(DQMeasure measure) {
        this.measure = measure;
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
        String namedContext = measure.getContext().getName();

        for (DQMeasure measure : measures) {
            if (measure.getContext().getName().equals(namedContext)) {
                return measure.getResult().getStatus().equals(CurationStatus.COMPLETE.name());
            }
        }

        // measure specified doesn't exist in the report
        throw new RuntimeException("Measure with context " + namedContext + " not found in dq report");
    }

    private double calculatePercentageComplete(int countComplete) {
        return countTotalReports;
    }

    public String getTitle() {
        return measure.getLabel();
    }

    public int getCompleteBefore() {
        return countCompleteBefore;
    }

    public int getCompleteAfter() {
        return countCompleteAfter;
    }

    public int getIncompleteBefore() {
        return getTotal() - countCompleteBefore;
    }

    public int getIncompleteAfter() {
        return getTotal() - countCompleteAfter;
    }

    public int getAssurance() {
        return assurance;
    }

    public String getLabel() {
        return measure.getLabel();
    }

    public String getDimension() {
        return measure.getDimension();
    }

    public String getMechanism() {
        return measure.getMechanism();
    }

    public String getSpecification() {
        return  measure.getSpecification();
    }


    public int getTotal() {
        return countTotalReports;
    }

    public String toJson() {
        JSONObject before = new JSONObject();

        before.put("complete", getCompleteBefore());
        before.put("incomplete", getIncompleteBefore());
        before.put("assurance", getAssurance());

        JSONObject after = new JSONObject();

        after.put("complete", getCompleteAfter());
        after.put("incomplete", getIncompleteAfter());
        after.put("assurance", getAssurance());

        JSONObject summary = new JSONObject();

        summary.put("title", getTitle());
        summary.put("specification", getSpecification());
        summary.put("mechanism", getMechanism());
        summary.put("before", before);
        summary.put("after", after);

        return summary.toJSONString();
    }

    public void writeJson(OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out);
        writer.write(toJson());
        writer.flush();
        writer.close();
    }


    public void writeXls(OutputStream out) throws IOException {

        Workbook workbook = new HSSFWorkbook();

        // initialize styles


        Font whiteFont = workbook.createFont();
        whiteFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle quarter = workbook.createCellStyle();  // 0 - 25%
        quarter.setFillForegroundColor(IndexedColors.RED.getIndex());
        quarter.setFillPattern(CellStyle.SOLID_FOREGROUND);
        quarter.setFont(whiteFont);

        CellStyle half = workbook.createCellStyle();  // 25 - 50%
        half.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        half.setFillPattern(CellStyle.SOLID_FOREGROUND);
        half.setFont(whiteFont);

        CellStyle threeQuarters = workbook.createCellStyle();  // 50 - 75%
        threeQuarters.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        threeQuarters.setFillPattern(CellStyle.SOLID_FOREGROUND);

        CellStyle full = workbook.createCellStyle();  // 75 - 100%
        full.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        full.setFillPattern(CellStyle.SOLID_FOREGROUND);
        full.setFont(whiteFont);

        CellStyle[] bgColors = { quarter, half, threeQuarters, full };

        Font headerFont = workbook.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        Sheet sheet = workbook.createSheet("Summary");

        // create header
        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Measure");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Before");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("After");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Count");
        headerCell.setCellStyle(headerStyle);

        // measure summary
        Row summary = sheet.createRow(1);
        summary.createCell(0).setCellValue(getTitle());

        Cell beforeCell = summary.createCell(1);
        beforeCell.setCellValue(getCompleteBefore());

        applyStyleForValue(beforeCell, getCompleteBefore(), bgColors, whiteFont);

        Cell afterCell = summary.createCell(2);
        afterCell.setCellValue(getCompleteAfter());

        applyStyleForValue(afterCell, getCompleteAfter(), bgColors, whiteFont);

        summary.createCell(3).setCellValue(getTotal());

        // auto resize first column with measure name
        sheet.autoSizeColumn(0);

        workbook.write(out);
        out.close();
        workbook.close();
    }

    private void applyStyleForValue(Cell cell, double value, CellStyle[] styles, Font font) {
        if (value < 25) {
            cell.setCellStyle(styles[0]);
        } else if (value >= 25 && value < 50) {
            cell.setCellStyle(styles[1]);
        } else if (value >= 50 && value < 75) {
            cell.setCellStyle(styles[2]);
        } else if (value >= 75) {
            cell.setCellStyle(styles[3]);
        }
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<DQReport> reports = mapper.readValue(MeasureSummary.class.getResourceAsStream("/dq_report.json"),
                new TypeReference<List<DQReport>>(){});

        InputStream config = MeasureSummary.class.getResourceAsStream("/ev-assertions.json");
        DQReportBuilder builder = new DQReportBuilder(config);

        AssertionsConfig assertions = builder.getAssertions();
        for (DQMeasure measure : assertions.getMeasures()) {
            MeasureSummary summary = new MeasureSummary(measure);
            summary.postprocess(reports);

            FileOutputStream outJson = new FileOutputStream(measure.getContext().getName() + ".json");
            summary.writeJson(outJson);
        }


        //MeasureSummary summary = new MeasureSummary("Event Date Completeness", "eventDateIsNotEmpty");


        //FileOutputStream out = new FileOutputStream("test.xls");
        //summary.writeXls(out);


    }
}
