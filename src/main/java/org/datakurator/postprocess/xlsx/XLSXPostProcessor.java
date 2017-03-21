package org.datakurator.postprocess.xlsx;

import com.github.jsonldjava.utils.Obj;
import org.apache.jena.base.Sys;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.datakurator.data.provenance.CurationStatus;
import org.datakurator.ffdq.annotations.Validation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public void postprocess() {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        initStyles(workbook);

        int windowSize = 100; // keep 100 rows in memory, exceeding rows will be flushed to disk

        SXSSFSheet finalValuesSheet = (SXSSFSheet) workbook.createSheet("Final Values");
        SXSSFSheet initialValuesSheet = (SXSSFSheet) workbook.createSheet("Initial Values");

        SXSSFSheet validationsSheet = (SXSSFSheet) workbook.createSheet("Validations");
        SXSSFSheet amendmentsSheet = (SXSSFSheet) workbook.createSheet("Amendments");
        SXSSFSheet measuresSheet = (SXSSFSheet) workbook.createSheet("Measures");


        finalValuesSheet.setRandomAccessWindowSize(100);
        initialValuesSheet.setRandomAccessWindowSize(100);

        validationsSheet.setRandomAccessWindowSize(100);
        amendmentsSheet.setRandomAccessWindowSize(100);
        measuresSheet.setRandomAccessWindowSize(100);

        Row validationsHeader = validationsSheet.createRow(0);

        try {
            int rowNum = 0;
            int validationRowNum = 0;
            int amendmentRowNum = 0;
            int measuresRowNum = 0;

            while(reportParser.next()) {
                Map<String, String> initialValues = reportParser.getInitialValues();
                Map<String, String> finalValues = reportParser.getFinalValues();

                List<Map<String, Object>> assertions = reportParser.getAssertions();

                if (header == null) {
                    header = new ArrayList<>();
                    for (String key : finalValues.keySet()) {
                        header.add(key);
                    }

                    // Create header row
                    Row initialValuesHeader = initialValuesSheet.createRow(rowNum);
                    Row finalValuesHeader = finalValuesSheet.createRow(rowNum);

                    Row amendmentsHeader = amendmentsSheet.createRow(rowNum);
                    Row measuresHeader = measuresSheet.createRow(rowNum);

                    for (int i = 0; i < header.size(); i++) {
                        initialValuesHeader.createCell(i).setCellValue(header.get(i));
                        finalValuesHeader.createCell(i).setCellValue(header.get(i));
                    }

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

                Row initialValuesRow = finalValuesSheet.createRow(rowNum);
                Row finalValuesRow = initialValuesSheet.createRow(rowNum);

                for (int i = 0; i < header.size(); i++) {
                    String initialValue = finalValues.get(header.get(i));
                    String finalValue = finalValues.get(header.get(i));

                    initialValuesRow.createCell(i).setCellValue(initialValue);
                    finalValuesRow.createCell(i).setCellValue(finalValue);
                }

                // process assertions

                for (Map<String, Object> assertion : assertions) {
                    List<String> fieldsActedUpon = (List<String>) assertion.get("actedUpon");
                    List<String> fieldsConsulted = (List<String>) assertion.get("consulted");

                    if ("VALIDATION".equalsIgnoreCase((String) assertion.get("type")) &&
                            "PRE_ENHANCEMENT".equalsIgnoreCase((String) assertion.get("stage"))) {
                        Row validationsRow = validationsSheet.createRow(validationRowNum);
                        validationsRow.createCell(0).setCellValue(reportParser.getRecordId());
                        validationsRow.createCell(1).setCellValue((String) assertion.get("name"));

                        String status = (String) assertion.get("status");

                        Cell statusCell = validationsRow.createCell(2);
                        statusCell.setCellValue(status);

                        if (styles.containsKey(status)) {
                            statusCell.setCellStyle(styles.get(status));
                        }

                        validationsRow.createCell(3).setCellValue((String) assertion.get("comment"));

//                        for (int i = 0; i < fieldsActedUpon.size(); i++) {
//                            String field = fieldsActedUpon.get(i);
//                            Cell cell = validationsRow.createCell(i+4);
//                            cell.setCellValue(initialValues.get(field));
//
//                            if (styles.containsKey(status)) {
//                                cell.setCellStyle(styles.get(status));
//                            }
//                        }

                        for (String field : fieldsActedUpon) {
                            if (!actedUponCols.containsKey(field)) {
                                int colNum = actedUponCols.size();
                                actedUponCols.put(field, colNum);

                                int columnOffset = 4;
                                validationsHeader.createCell(columnOffset + colNum).setCellValue(field);
                            }

                            int i = actedUponCols.get(field);

                            Cell cell = validationsRow.createCell(i + 4);
                            cell.setCellValue(initialValues.get(field));

                            if (styles.containsKey(status)) {
                                cell.setCellStyle(styles.get(status));
                            }
                        }

                        validationRowNum++;
                    } else if ("AMENDMENT".equalsIgnoreCase((String) assertion.get("type")) &&
                            "ENHANCEMENT".equalsIgnoreCase((String) assertion.get("stage"))) {
                        Row amendmentsRow = amendmentsSheet.createRow(amendmentRowNum);

                        amendmentsRow.createCell(0).setCellValue(reportParser.getRecordId());
                        amendmentsRow.createCell(1).setCellValue((String) assertion.get("name"));

                        String status = (String) assertion.get("status");

                        Cell statusCell = amendmentsRow.createCell(2);
                        statusCell.setCellValue(status);

                        if (styles.containsKey(status)) {
                            statusCell.setCellStyle(styles.get(status));
                        }

                        amendmentsRow.createCell(3).setCellValue((String) assertion.get("comment"));

                        if (assertion.containsKey("result") && assertion.get("result") != null && !((Map) assertion.get("result")).isEmpty()) {
                            Map<String, String> result = (Map<String, String>) assertion.get("result");

                            for (int i = 0; i < fieldsActedUpon.size(); i++) {
                                String field = fieldsActedUpon.get(i);
                                Cell cell = amendmentsRow.createCell(i + 4);
                                cell.setCellValue(result.get(field));

                                if (styles.containsKey(status)) {
                                    cell.setCellStyle(styles.get(status));
                                }
                            }
                        }

                        amendmentRowNum++;
                    } else if ("MEASURE".equalsIgnoreCase((String) assertion.get("type")) &&
                            "PRE_ENHANCEMENT".equalsIgnoreCase((String) assertion.get("stage"))) {
                        Row measuresRow = measuresSheet.createRow(measuresRowNum);

                        measuresRow.createCell(1).setCellValue((String) assertion.get("name"));

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

                    rowNum++;
                    validationRowNum++; // space between blocks of validations
                    amendmentRowNum++;
                    measuresRowNum++;
                }
            }

            FileOutputStream out = new FileOutputStream("tempsxssf.xlsx");
            workbook.write(out);
            out.close();

            // dispose of temporary files backing this workbook on disk
            workbook.dispose();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        XLSXPostProcessor postProcessor = new XLSXPostProcessor(DQReportParser.class.getResourceAsStream(args[0]));
        postProcessor.postprocess();
    }
}
