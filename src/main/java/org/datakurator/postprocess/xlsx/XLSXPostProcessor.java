package org.datakurator.postprocess.xlsx;

import com.github.jsonldjava.utils.Obj;
import org.apache.jena.base.Sys;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.datakurator.ffdq.annotations.Validation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/17/2017.
 */
public class XLSXPostProcessor {
    private DQReportParser reportParser;
    private List<String> header;

    public XLSXPostProcessor(InputStream reportStream) {
        try {
            reportParser = new DQReportParser(reportStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postprocess() {
        SXSSFWorkbook workbook = new SXSSFWorkbook();

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

        try {
            int rowNum = 0;
            int validationRowNum = 0;
            int amendmentRowNum = 0;

            while(reportParser.next()) {
                Map<String, String> initialValues = reportParser.getInitialValues();
                Map<String, String> finalValues = reportParser.getFinalValues();

                List<Map<String, Object>> assertions = reportParser.getAssertions();
                List<String> fieldsActedUpon = reportParser.getActedUpon();

                if (header == null) {
                    header = new ArrayList<>();
                    for (String key : finalValues.keySet()) {
                        header.add(key);
                    }

                    // Create header row
                    Row initialValuesHeader = initialValuesSheet.createRow(rowNum);
                    Row finalValuesHeader = finalValuesSheet.createRow(rowNum);

                    Row validationsHeader = validationsSheet.createRow(rowNum);
                    Row amendmentsHeader = amendmentsSheet.createRow(rowNum);

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

                    for (int i = 0; i < fieldsActedUpon.size(); i++) {
                        validationsHeader.createCell(i+4).setCellValue(fieldsActedUpon.get(i));
                        amendmentsHeader.createCell(i+4).setCellValue(fieldsActedUpon.get(i));
                    }

                    rowNum++;
                    validationRowNum++;
                    amendmentRowNum++;
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
                    if ("VALIDATION".equalsIgnoreCase((String) assertion.get("type")) &&
                            "PRE_ENHANCEMENT".equalsIgnoreCase((String) assertion.get("stage"))) {
                        Row validationsRow = validationsSheet.createRow(validationRowNum);
                        validationsRow.createCell(0).setCellValue(reportParser.getRecordId());
                        validationsRow.createCell(1).setCellValue((String) assertion.get("name"));
                        validationsRow.createCell(2).setCellValue((String) assertion.get("status"));
                        validationsRow.createCell(3).setCellValue((String) assertion.get("comment"));

                        for (int i = 0; i < fieldsActedUpon.size(); i++) {
                            String field = fieldsActedUpon.get(i);
                            validationsRow.createCell(i+4).setCellValue(initialValues.get(field));
                        }

                        validationRowNum++;
                    } else if ("AMENDMENT".equalsIgnoreCase((String) assertion.get("type")) &&
                            "ENHANCEMENT".equalsIgnoreCase((String) assertion.get("stage"))) {
                        Row amendmentsRow = amendmentsSheet.createRow(amendmentRowNum);

                        amendmentsRow.createCell(0).setCellValue(reportParser.getRecordId());
                        amendmentsRow.createCell(1).setCellValue((String) assertion.get("name"));
                        amendmentsRow.createCell(2).setCellValue((String) assertion.get("status"));
                        amendmentsRow.createCell(3).setCellValue((String) assertion.get("comment"));

                        if (assertion.containsKey("result") && assertion.get("result") != null && !((Map) assertion.get("result")).isEmpty()) {
                            Map<String, String> result = (Map<String, String>) assertion.get("result");

                            for (int i = 0; i < fieldsActedUpon.size(); i++) {
                                String field = fieldsActedUpon.get(i);
                                amendmentsRow.createCell(i + 4).setCellValue(result.get(field));
                            }
                        }

                        amendmentRowNum++;
                    }
                }

                rowNum++;
                validationRowNum++; // space between blocks of validations
                amendmentRowNum++;
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
