package org.datakurator.postprocess.xlsx;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.datakurator.data.ffdq.DQReport;
import org.datakurator.data.ffdq.assertions.*;
import org.datakurator.data.provenance.CurationStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by lowery on 2/13/2017.
 */
public class ReportSummary {
    private static Map<String, CellStyle> styles = new HashMap<>(); // Curation status to cell style mappings
    private List<DQReport> reports;

    public ReportSummary(List<DQReport> reports) {
        this.reports = reports;
    }

    public void toXls(File output) throws IOException {
        Workbook wb = new HSSFWorkbook();

        initStyles(wb);

        Sheet validationsSheet = wb.createSheet("Validations");
        Sheet improvementsSheet = wb.createSheet("Improvements");
        Sheet measuresSheet = wb.createSheet("Measures");

        int validationsRowNum = 0;
        int improvementsRowNum = 0;
        int measuresRowNum = 0;

        for (DQReport report : reports) {

            DQReportStage postEnhancement = report.getStages().get(1);
            DQReportStage enhancement = report.getStages().get(2);

            List<DQMeasure> measures = postEnhancement.getMeasures();
            List<DQValidation> validations = postEnhancement.getValidations();
            List<DQImprovement> improvements = enhancement.getImprovements();

            // Process validations
            Row validationsRow = validationsSheet.createRow(validationsRowNum);
            validationsRowNum += processAssertions(report, validations, validationsRow, validationsSheet) + 1;

            // Process improvements
            Row improvementsRow = improvementsSheet.createRow(improvementsRowNum);
            improvementsRowNum += processAssertions(report, improvements, improvementsRow, improvementsSheet) + 1;

            // Process measures
            Row measuresRow = measuresSheet.createRow(measuresRowNum);
            measuresRowNum += processAssertions(report, measures, measuresRow, measuresSheet) + 1;
        }

        wb.write(new FileOutputStream(output));
    }

    private static void initStyles(Workbook wb) {
        // White font
        Font font = wb.createFont();
        font.setColor(HSSFColor.WHITE.index);

        // Compliant or complete styled with green background
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.GREEN.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put(CurationStatus.COMPLIANT.toString(), style);
        styles.put(CurationStatus.COMPLETE.toString(), style);

        // Not compliant or not complete styled with red background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.RED.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put(CurationStatus.NOT_COMPLIANT.toString(), style);
        styles.put(CurationStatus.NOT_COMPLETE.toString(), style);

        // Filled in styled with yellow background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.DARK_YELLOW.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put(CurationStatus.FILLED_IN.toString(), style);

        // Unable determine validity styled with grey background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put(CurationStatus.DATA_PREREQUISITES_NOT_MET.toString(), style);
        styles.put(CurationStatus.EXTERNAL_PREREQUISITES_NOT_MET.toString(), style);
    }

    private static int processAssertions(DQReport report, List<? extends DQAssertion> assertions, Row row, Sheet sheet) {
        List<String> fields = new ArrayList<>(report.getDataResource().getInitialValues().keySet());

        // Create header
        //row.createCell(0).setCellValue("assertion");
        row.createCell(1).setCellValue("recordId");

        for (int i = 0; i < fields.size(); i++) {
            row.createCell(i+2).setCellValue(fields.get(i));
        }

        row.createCell(fields.size()+2).setCellValue("comments");
        row.createCell(fields.size()+3).setCellValue("status");

        // Create validation rows
        int rowNum = row.getRowNum();
        for (int i = 0; i < assertions.size(); i++) {
            DQAssertion validation = assertions.get(i);
            Row validationRow = sheet.createRow(rowNum+1+i);

            // Validation label
            validationRow.createCell(0).setCellValue(validation.getLabel());

            // Record id
            validationRow.createCell(1).setCellValue(validation.getRecordId());

            // Validation initial and final values
            Result result = validation.getResult();
            Map<String, String> curatedValues = result.getCuratedValues();
            Map<String, String> initialValues = result.getInitialValues();

            List<String> fieldsActedUpon = validation.getContext().getFieldsActedUpon();

            int j = 0;
            while (j < fields.size()) {
                String field = fields.get(j);
                Cell cell = validationRow.createCell(j+2);
                String curatedValue = curatedValues.get(field);
                String initialValue = initialValues.get(field);

                if (initialValue == null || initialValue.isEmpty()) {
                    initialValue = "empty"; // Placeholder for empty values
                }

                if (curatedValue == null || curatedValue.isEmpty()) {
                    curatedValue = "empty"; // Placeholder for empty values
                }

                // if the initial value is different than the currated value, add "CHANGED TO: ..."
                cell.setCellValue(curatedValue.equals(initialValue) ? curatedValue : initialValue + " CHANGED TO: " + curatedValue);

                // set cell style based on value of field status
                if (fieldsActedUpon.contains(field)) {
                    cell.setCellStyle(styles.get(result.getStatus()));
                }

                j++;
            }

            // Validation comments
            Cell commentsCell = validationRow.createCell(j+2);

            StringBuilder comments = new StringBuilder();
            int count = 0;
            for (String comment : validation.getResult().getComments()) {
                if (count > 0) {
                    comments.append(" | ");
                }

                comments.append(comment);
            }

            commentsCell.setCellValue(comments.toString());

            // Validation status
            Cell statusCell = validationRow.createCell(j+3);
            statusCell.setCellValue(result.getStatus());
            statusCell.setCellStyle(styles.get(result.getStatus()));
        }

        return assertions.size()+1; // numRows (includes header)
    }
}
