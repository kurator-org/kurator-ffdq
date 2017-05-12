package org.datakurator.postprocess.xlsx;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.datakurator.data.provenance.CurationStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/17/2017.
 */
public class XLSXPostProcessor {
    private DQReportParser reportParser;
    private List<String> header;

    private static Map<String, CellStyle> styles = new HashMap<>();
    private static Map<String, Integer> actedUponCols = new HashMap<>();

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

    public XLSXPostProcessor(InputStream reportStream) {
        try {
            reportParser = new DQReportParser(reportStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postprocess(OutputStream out) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        initStyles(workbook);

        int windowSize = 100; // keep 100 rows in memory, exceeding rows will be flushed to disk

        SXSSFSheet summarySheet = (SXSSFSheet) workbook.createSheet("Summary");
        SXSSFSheet finalValuesSheet = (SXSSFSheet) workbook.createSheet("Final Values");
        SXSSFSheet initialValuesSheet = (SXSSFSheet) workbook.createSheet("Initial Values");

        SXSSFSheet validationsSheet = (SXSSFSheet) workbook.createSheet("Validations");
        SXSSFSheet amendmentsSheet = (SXSSFSheet) workbook.createSheet("Amendments");
        SXSSFSheet measuresSheet = (SXSSFSheet) workbook.createSheet("Measures");

        CellStyle summaryStyle = workbook.createCellStyle();
        summaryStyle.setWrapText(true);

        Row summaryRow = summarySheet.createRow(1);
        Cell summaryCell = summaryRow.createCell(1);

        summaryCell.setCellValue("The sheet labeled \"Final Values\" contains data including any changes made as part of running the workflow. The sheet labeled \"Initial Values\" contains the original data supplied as input to the workflow.\n" +
                "\n" +
                "The \"Validations\" sheet gives a summary for each of the validation tests performed. The \"Amendments sheet summarizes any changes made to the records. In both sheets rows indicating the test results are grouped by record and separated by spaces.\n" +
                "\n" +
                "The 'Measures' sheet contains the value of any measurements performed (i.e. precision, completeness).");

        summaryCell.setCellStyle(summaryStyle);

        summarySheet.addMergedRegion(new CellRangeAddress(1,7,1,9));

        finalValuesSheet.setRandomAccessWindowSize(100);
        initialValuesSheet.setRandomAccessWindowSize(100);

        validationsSheet.setRandomAccessWindowSize(100);
        amendmentsSheet.setRandomAccessWindowSize(100);
        measuresSheet.setRandomAccessWindowSize(100);

        Row validationsHeader = validationsSheet.createRow(0);
        Row amendmentsHeader = amendmentsSheet.createRow(0);

        try {
            int rowNum = 0;
            int validationRowNum = 0;
            int amendmentRowNum = 0;
            int measuresRowNum = 0;

            while(reportParser.next()) {
                Map<String, String> initialValues = reportParser.getInitialValues();
                Map<String, String> finalValues = reportParser.getFinalValues();

                Map<String, String> validationState = reportParser.getValidationState();
                Map<String, String> amendmentState = reportParser.getAmendmentState();

                List<Map<String, Object>> assertions = reportParser.getAssertions();
                Map<String, Map<String, String>> profile = reportParser.getProfile();

                if (header == null) {
                    header = new ArrayList<>();
                    for (String key : finalValues.keySet()) {
                        header.add(key);
                    }

                    // Create header row
                    Row initialValuesHeader = initialValuesSheet.createRow(rowNum);
                    Row finalValuesHeader = finalValuesSheet.createRow(rowNum);

                    Row measuresHeader = measuresSheet.createRow(rowNum);

                    for (int i = 0; i < header.size(); i++) {
                        initialValuesHeader.createCell(i).setCellValue(header.get(i));
                        finalValuesHeader.createCell(i).setCellValue(header.get(i));
                    }

                    initialValuesHeader.createCell(header.size()).setCellValue("Data Quality Flags (Validations)");
                    finalValuesHeader.createCell(header.size()).setCellValue("Data Quality Flags (Amendments)");

                    validationsHeader.createCell(0).setCellValue("Record Id");
                    validationsHeader.createCell(1).setCellValue("Validation");
                    validationsHeader.createCell(2).setCellValue("Status");
                    validationsHeader.createCell(3).setCellValue("Comment");

                    amendmentsHeader.createCell(0).setCellValue("Record Id");
                    amendmentsHeader.createCell(1).setCellValue("Amendment");
                    amendmentsHeader.createCell(2).setCellValue("Status");
                    amendmentsHeader.createCell(3).setCellValue("Comment");

                    measuresHeader.createCell(0).setCellValue("Record Id");
                    measuresHeader.createCell(1).setCellValue("Measure");
                    measuresHeader.createCell(2).setCellValue("Status");
                    measuresHeader.createCell(3).setCellValue("Comment");
                    measuresHeader.createCell(4).setCellValue("Value");

                    //for (int i = 0; i < fieldsActedUpon.size(); i++) {
                    //    validationsHeader.createCell(i+4).setCellValue(fieldsActedUpon.get(i));
                    //    amendmentsHeader.createCell(i+4).setCellValue(fieldsActedUpon.get(i));
                    //}

                    rowNum++;
                    validationRowNum++;
                    amendmentRowNum++;
                    measuresRowNum++;
                }

                // process assertions

                StringBuilder rowValidationTests = new StringBuilder();
                StringBuilder rowAmendmentTests = new StringBuilder();

                for (Map<String, Object> assertion : assertions) {
                    //List<String> fieldsActedUpon = (List<String>) assertion.get("actedUpon");
                    //List<String> fieldsConsulted = (List<String>) assertion.get("consulted");

                    List<String> fields = new ArrayList<>();
                    fields.addAll((List<String>) assertion.get("actedUpon"));
                    fields.addAll((List<String>) assertion.get("consulted"));

                    String recordId = reportParser.getRecordId();

                    if ("VALIDATION".equalsIgnoreCase((String) assertion.get("type")) &&
                            "PRE_ENHANCEMENT".equalsIgnoreCase((String) assertion.get("stage"))) {
                        Row validationsRow = validationsSheet.createRow(validationRowNum);

                        if (recordId != null) {
                            validationsRow.createCell(0).setCellValue(recordId);
                        }

                        String test = (String) assertion.get("name");
                        String validation = profile.get(test).get("label");

                        validationsRow.createCell(1).setCellValue(validation);

                        String status = (String) assertion.get("status");

                        if (rowValidationTests.length() > 0) {
                            rowValidationTests.append(", ");
                        }

                        rowValidationTests.append("[" + validation + "=" + status + "]");

                        Cell statusCell = validationsRow.createCell(2);
                        statusCell.setCellValue(status);

                        if (styles.containsKey(status)) {
                            statusCell.setCellStyle(styles.get(status));
                        }

                        validationsRow.createCell(3).setCellValue((String) assertion.get("comment"));

                        int columnOffset = 4;
                        for (int colNum = 0; colNum < fields.size(); colNum++) {
                            String field = fields.get(colNum);
                            validationsHeader.createCell(columnOffset + colNum).setCellValue(field);

                            String value = initialValues.get(field);
                            Cell cell = validationsRow.createCell(columnOffset + colNum);
                            cell.setCellValue(value != null ? value : "");

                            if (styles.containsKey(status)) {
                                cell.setCellStyle(styles.get(status));
                            }
                        }

                        validationRowNum++;
                    } else if ("AMENDMENT".equalsIgnoreCase((String) assertion.get("type")) &&
                            "ENHANCEMENT".equalsIgnoreCase((String) assertion.get("stage"))) {
                        Row amendmentsRow = amendmentsSheet.createRow(amendmentRowNum);

                        if (recordId != null) {
                            amendmentsRow.createCell(0).setCellValue(recordId);
                        }

                        String test = (String) assertion.get("name");
                        String amendment = profile.get(test).get("label");

                        amendmentsRow.createCell(1).setCellValue(amendment);

                        String status = (String) assertion.get("status");

                        if (!status.equals("NO_CHANGE")) {
                            if (rowAmendmentTests.length() > 0) {
                                rowAmendmentTests.append(", ");
                            }

                            rowAmendmentTests.append("[" + amendment + "=" + status + "]");
                        }

                        Cell statusCell = amendmentsRow.createCell(2);
                        statusCell.setCellValue(status);

                        if (styles.containsKey(status)) {
                            statusCell.setCellStyle(styles.get(status));
                        }

                        amendmentsRow.createCell(3).setCellValue((String) assertion.get("comment"));

                        if (assertion.containsKey("result") && assertion.get("result") != null && !((Map) assertion.get("result")).isEmpty()) {
                            Map<String, String> result = (Map<String, String>) assertion.get("result");

                            int columnOffset = 4;
                            for (int colNum = 0; colNum < fields.size(); colNum++) {
                                String field = fields.get(colNum);
                                amendmentsHeader.createCell(columnOffset + colNum).setCellValue(field);

                                String value = initialValues.get(field);
                                Cell cell = amendmentsRow.createCell(columnOffset);
                                cell.setCellValue(value != null ? value : "");

                                if (styles.containsKey(status)) {
                                    cell.setCellStyle(styles.get(status));
                                }
                            }
                        }

                        amendmentRowNum++;
                    } else if ("MEASURE".equalsIgnoreCase((String) assertion.get("type")) &&
                            "PRE_ENHANCEMENT".equalsIgnoreCase((String) assertion.get("stage"))) {
                        Row measuresRow = measuresSheet.createRow(measuresRowNum);

                        if (recordId != null) {
                            measuresRow.createCell(0).setCellValue(recordId);
                        }

                        String test = (String) assertion.get("name");
                        String measure = profile.get(test).get("label");

                        measuresRow.createCell(1).setCellValue(measure);

                        String status = (String) assertion.get("status");

                        Cell statusCell = measuresRow.createCell(2);
                        statusCell.setCellValue(status);

                        if (styles.containsKey(status)) {
                            statusCell.setCellStyle(styles.get(status));
                        }

                        measuresRow.createCell(3).setCellValue((String) assertion.get("comment"));

                        if (assertion.containsKey("value") && assertion.get("value") != null) {
                            measuresRow.createCell(4).setCellValue((String) assertion.get("value"));
                        }

                        measuresRowNum++;
                    }
                }


                // initial and final values sheets
                Row initialValuesRow = finalValuesSheet.createRow(rowNum);
                Row finalValuesRow = initialValuesSheet.createRow(rowNum);

                for (int i = 0; i < header.size(); i++) {
                    String field = header.get(i);
                    String initialValue = initialValues.get(field);
                    String finalValue = finalValues.get(field);

                    String initialStatus = validationState.get(field);
                    String finalStatus = amendmentState.get(field);

                    Cell initialValueCell = initialValuesRow.createCell(i);
                    initialValueCell.setCellValue(initialValue);

                    if (initialStatus != null) {
                        if (styles.containsKey(initialStatus)) {
                            initialValueCell.setCellStyle(styles.get(initialStatus));
                        }
                    }

                    Cell finalValueCell = finalValuesRow.createCell(i);
                    finalValueCell.setCellValue(finalValue);

                    if (finalStatus != null) {
                        if (styles.containsKey(finalStatus)) {
                            finalValueCell.setCellStyle(styles.get(finalStatus));
                        }
                    }
                }

                initialValuesRow.createCell(header.size()).setCellValue(rowValidationTests.toString());
                finalValuesRow.createCell(header.size()).setCellValue(rowAmendmentTests.toString());

                rowNum++;

                // add empty row between blocks of assertions
                validationRowNum++;
                amendmentRowNum++;
                measuresRowNum++;
            }

            workbook.write(out);
            out.close();

            // dispose of temporary files backing this workbook on disk
            workbook.dispose();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        XLSXPostProcessor postProcessor = new XLSXPostProcessor(DQReportParser.class.getResourceAsStream(args[0]));
        postProcessor.postprocess(new FileOutputStream("tempsxssf.xlsx"));
    }
}
