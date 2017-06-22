package org.datakurator.postprocess.xlsx;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.datakurator.data.provenance.CurationStatus;
import org.datakurator.postprocess.model.*;

import java.io.*;
import java.util.*;

/**
 * Created by lowery on 2/17/2017.
 */
public class XLSXPostProcessor {
    private DQReportParser reportParser;
    private List<String> header;

    private static Map<String, CellStyle> styles = new HashMap<>();
    private static Map<String, Integer> actedUponCols = new HashMap<>();

    private ReportSummary initialValuesSummary;
    private ReportSummary finalValuesSummary;

    private AssertionSummary validationSummary;
    private AssertionSummary measureSummary;
    private AssertionSummary amendmentSummary;

    public XLSXPostProcessor(InputStream reportStream) {
        try {
            reportParser = new DQReportParser(reportStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        // Filled in, curated or transposed styled with yellow background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.DARK_YELLOW.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put(CurationStatus.FILLED_IN.toString(), style);
        styles.put(CurationStatus.CURATED.toString(), style);
        styles.put(CurationStatus.TRANSPOSED.toString(), style);

        // Unable determine validity styled with grey background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put(CurationStatus.DATA_PREREQUISITES_NOT_MET.toString(), style);
        styles.put(CurationStatus.EXTERNAL_PREREQUISITES_NOT_MET.toString(), style);
    }

    public void postprocess(OutputStream out) {
        int windowSize = 100; // keep 100 rows in memory, exceeding rows will be flushed to disk

        // Create the workbook and initialize cell styles
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        initStyles(workbook);

        // Summary sheet containing descriptive text
        String summaryText = "The sheet labeled \"Final Values\" contains data including any changes " +
                "made as part of running the workflow. The sheet labeled \"Initial Values\" contains the original data " +
                "supplied as input to the workflow.\n" +
                "\n" +
                "The \"Validations\" sheet gives a summary for each of the validation tests performed. The \"Amendments " +
                "sheet summarizes any changes made to the records. In both sheets rows indicating the test results are " +
                "grouped by record and separated by spaces.\n" +
                "\n" +
                "The 'Measures' sheet contains the value of any measurements performed (i.e. precision, completeness).";

        createSummarySheet(workbook, summaryText);


        try {
            while(reportParser.next()) {

                if (initialValuesSummary == null) {
                    initialValuesSummary = new ReportSummary(reportParser.getDataResourceFields(), "Initial Values", workbook, styles);
                }

                if (finalValuesSummary == null) {
                    finalValuesSummary = new ReportSummary(reportParser.getDataResourceFields(), "Final Values", workbook, styles);
                }

                if (validationSummary == null) {
                    validationSummary = new AssertionSummary(reportParser.getAssertionFields(), "Validations", workbook, styles, Validation.class);
                }

                if (measureSummary == null) {
                    measureSummary = new AssertionSummary(reportParser.getAssertionFields(), "Measures", workbook, styles, Measure.class);
                }

                if (amendmentSummary == null) {
                    amendmentSummary = new AssertionSummary(reportParser.getAssertionFields(), "Amendments", workbook, styles, Improvement.class);
                }

                Map<String, String> initialValues = reportParser.getInitialValues();
                Map<String, String> finalValues = reportParser.getFinalValues();

                // TODO: Fix color coding on final values sheet
                Map<String, String> validationState = new HashMap<>();
                Map<String, String> amendmentState = reportParser.getAmendmentState();

                // process assertions and create flags
                StringBuilder rowValidationTests = new StringBuilder();

                for (Validation validation : reportParser.getValidations()) {
                    String test = validation.getTest().getName();
                    String status = validation.getStatus();

                    String flag = "[" + test + "_" + status + "] ";
                    rowValidationTests.append(flag);

                    for (String field : validation.getContext().getFieldsActedUpon()) {
                        String fieldStatus = validationState.get(field);

                        if (status.equals("COMPLIANT") && fieldStatus == null) {
                            fieldStatus = "COMPLIANT";
                        } else if (status.equals("DATA_PREREQUISITES_NOT_MET") && fieldStatus != "NON_COMPLIANT") {
                            fieldStatus = "DATA_PREREQUISITES_NOT_MET";
                        } else if (status.equals("NON_COMPLIANT")) {
                            fieldStatus = "NON_COMPLIANT";
                        }

                        validationState.put(field, fieldStatus);
                    }
                }

                StringBuilder rowAmendmentTests = new StringBuilder();
                for (Improvement improvement : reportParser.getAmendments()) {
                    String test = improvement.getTest().getName();
                    String status = improvement.getStatus();

                    String flag = "[" + test + "_" + status + "] ";
                    rowAmendmentTests.append(flag);

                    for (String field : improvement.getContext().getFieldsActedUpon()) {
                        String fieldStatus = amendmentState.get(field);

                        if (status.equals("NO_CHANGE") && fieldStatus == null) {
                            fieldStatus = "NO_CHANGE";
                        } else if (status.equals("DATA_PREREQUISITES_NOT_MET") && !fieldStatus.equals("FILLED_IN")) {
                            fieldStatus = "DATA_PREREQUISITES_NOT_MET";
                        } else if (status.equals("FILLED_IN")) {
                            fieldStatus = "FILLED_IN";
                        }

                        amendmentState.put(field, fieldStatus);
                    }

                }

                String recordId = "";

                if (initialValues.containsKey("occurrenceID")) {
                    recordId = initialValues.get("occurrenceID");
                }

                initialValuesSummary.postprocess(initialValues, validationState, rowValidationTests.toString());
                finalValuesSummary.postprocess(finalValues, amendmentState, rowAmendmentTests.toString());

                validationSummary.postprocess(reportParser.getValidations(), initialValues, recordId);
                measureSummary.postprocess(reportParser.getMeasures(), initialValues, recordId);
                amendmentSummary.postprocess(reportParser.getAmendments(), initialValues, recordId);

            }

            workbook.write(out);
            out.close();

            // dispose of temporary files backing this workbook on disk
            workbook.dispose();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSummarySheet(SXSSFWorkbook workbook, String summaryText) {
        SXSSFSheet summarySheet = (SXSSFSheet) workbook.createSheet("Summary");

        // Wrap text on the summary page
        CellStyle summaryStyle = workbook.createCellStyle();
        summaryStyle.setWrapText(true);

        // Add the summary text to the cell
        Row summaryRow = summarySheet.createRow(1);
        Cell summaryCell = summaryRow.createCell(1);

        summaryCell.setCellValue(summaryText);
        summaryCell.setCellStyle(summaryStyle);

        // Create a merged region from (1,1) to (7,9) for the summary text
        summarySheet.addMergedRegion(new CellRangeAddress(1,7,1,9));
    }

    public static void main(String[] args) throws FileNotFoundException {
        XLSXPostProcessor postProcessor = new XLSXPostProcessor(DQReportParser.class.getResourceAsStream("/mcz_test.json"));
        postProcessor.postprocess(new FileOutputStream("tempsxssf.xlsx"));
    }
}
