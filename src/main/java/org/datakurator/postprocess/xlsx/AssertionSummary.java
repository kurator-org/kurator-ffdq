package org.datakurator.postprocess.xlsx;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.datakurator.data.provenance.CurationStatus;
import org.datakurator.postprocess.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 6/21/17.
 */
public class AssertionSummary {
    private static Map<String, CellStyle> styles;
    private final SXSSFSheet sheet;

    private List<String> header;
    private int rowNum = 0;

    public AssertionSummary(List<String> header, String title, Workbook workbook, Map<String, CellStyle> styles, Class type) {
        this.header = header;
        this.styles = styles;

        // Create the sheet
        this.sheet = (SXSSFSheet) workbook.createSheet(title);
        sheet.setRandomAccessWindowSize(100);

        // Create header row
        Row headerRow = sheet.createRow(rowNum);

        int columnOffset = 4;
        for (int i = 0; i < header.size(); i++) {
            headerRow.createCell(i + columnOffset).setCellValue(header.get(i));
        }

        headerRow.createCell(0).setCellValue("Record Id");

        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Comment");

        if (type == Validation.class) {
            headerRow.createCell(1).setCellValue("Validations");
        } else if (type == Measure.class) {
            headerRow.createCell(1).setCellValue("Measures");
        } else if (type == Improvement.class) {
            headerRow.createCell(1).setCellValue("Amendments");
            headerRow.createCell(4).setCellValue("Value");
        }

        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Comment");

        rowNum++;
    }

    public void postprocess(List<? extends Assertion> assertions, Map<String, String> values, String recordId) {
        for (Assertion assertion : assertions) {
            Row row = sheet.createRow(rowNum);

            if (recordId != null) {
                row.createCell(0).setCellValue(recordId);
            }

            Test test = assertion.getTest();
            row.createCell(1).setCellValue(test.getLabel());

            String status = assertion.getStatus();
            System.out.println(status);

            Cell statusCell = row.createCell(2);
            statusCell.setCellValue(status);

            if (styles.containsKey(status)) {
                statusCell.setCellStyle(styles.get(status));
            }

            row.createCell(3).setCellValue(assertion.getComment());

            int columnOffset = 4;
            for (int colNum = 0; colNum < header.size(); colNum++) {
                String field = header.get(colNum);

                String value = values.get(field);
                Cell cell = row.createCell(columnOffset + colNum);
                cell.setCellValue(value != null ? value : "");

                if (assertion.getContext().getFieldsActedUpon().contains(field) && styles.containsKey(status)) {
                    cell.setCellStyle(styles.get(status));
                }
            }

            rowNum++;
        }

        // Skip one row before next set of assertions
        rowNum++;
    }
}
