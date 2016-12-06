package org.datakurator.postprocess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
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
        return (countComplete/(double) countTotalReports)*100.0;
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
        beforeCell.setCellValue(getBefore());

        applyStyleForValue(beforeCell, getBefore(), bgColors, whiteFont);

        Cell afterCell = summary.createCell(2);
        afterCell.setCellValue(getAfter());

        applyStyleForValue(afterCell, getAfter(), bgColors, whiteFont);

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
