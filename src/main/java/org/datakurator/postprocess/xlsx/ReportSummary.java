package org.datakurator.postprocess.xlsx;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
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
    private final SXSSFSheet sheet;

    private List<String> header;
    private int rowNum = 0;

    public ReportSummary(List<String> header, String title, Workbook workbook) {
        this.header = header;

        // Create the sheet
        this.sheet = (SXSSFSheet) workbook.createSheet(title);
        sheet.setRandomAccessWindowSize(100);

        // Create header row
        Row headerRow = sheet.createRow(rowNum);

        for (int i = 0; i < header.size(); i++) {
            headerRow.createCell(i).setCellValue(header.get(i));
        }

        headerRow.createCell(header.size()).setCellValue("Data Quality Flags");

        rowNum++;
    }

    public void postprocess(Map<String, String> values) {
        // initial and final values sheets
        Row row = sheet.createRow(rowNum);

        for (int i = 0; i < header.size(); i++) {
            String field = header.get(i);

            String value = values.get(field);

            //String initialStatus = validationState.get(field);
            //String finalStatus = amendmentState.get(field);

            Cell cell = row.createCell(i);
            cell.setCellValue(value);

            /*if (initialStatus != null) {
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
            }*/
        }

        rowNum++;
    }
}
