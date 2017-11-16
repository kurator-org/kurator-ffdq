package org.datakurator.postprocess.xlsx;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.datakurator.postprocess.model.*;

import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 6/21/17.
 */
public class AssertionSummary {
    private static Map<String, CellStyle> styles;
    private final SXSSFSheet sheet;
    private final Class type;

    private List<String> header;
    private int rowNum = 0;
    private int columnOffset;

    public AssertionSummary(List<String> header, String title, Workbook workbook, Map<String, CellStyle> styles, Class type) {
        this.header = header;
        this.styles = styles;
        this.type = type;

        // Create the sheet
        this.sheet = (SXSSFSheet) workbook.createSheet(title);
        sheet.setRandomAccessWindowSize(100);

        // Create header row
        Row headerRow = sheet.createRow(rowNum);

        headerRow.createCell(0).setCellValue("Record Id");

        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Comment");

        if (type == Validation.class) {
            headerRow.createCell(1).setCellValue("Validations");
            columnOffset = 4;
        } else if (type == Measure.class) {
            headerRow.createCell(1).setCellValue("Measures");
            headerRow.createCell(4).setCellValue("Value");
            columnOffset = 5;
        } else if (type == Improvement.class) {
            headerRow.createCell(1).setCellValue("Amendments");
            columnOffset = 4;
        }

        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Comment");

        for (int i = 0; i < header.size(); i++) {
            headerRow.createCell(i + columnOffset).setCellValue(header.get(i));
        }

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
            Cell statusCell = row.createCell(2);
            statusCell.setCellValue(status);

            if (styles.containsKey(status)) {
                statusCell.setCellStyle(styles.get(status));
            }

            row.createCell(3).setCellValue(assertion.getComment());

            if (type == Measure.class) {
                String value = ((Measure) assertion).getValue();
                row.createCell(4).setCellValue(value != null ? value : "");
            } else if (type == Improvement.class) {
                values = ((Improvement) assertion).getResult();
            }

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
