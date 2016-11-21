package org.datakurator.postprocess;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.datakurator.data.ffdq.DataResource;
import org.datakurator.data.ffdq.assertions.Result;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lowery on 11/21/16.
 */
public class DataTable {
    private List<DataResource> data;

    public DataTable(List<DataResource> data) {
        this.data = data;
    }

    public void drawXls(OutputStream out) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Curated Values");

        List<String> fieldNames = new ArrayList<>(data.get(0).getFields());

        // create header
        Row header = sheet.createRow(0);

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            Cell cell = header.createCell(i);

            cell.setCellValue(fieldName);
        }

        int rowNum = 1;

        // create rows
        for (DataResource record : data) {
            Row row = sheet.createRow(rowNum++);

            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);

                Cell cell = row.createCell(i);
                cell.setCellValue(record.getCuratedValues().get(fieldName));
            }
        }

        workbook.write(out);
        out.close();
        workbook.close();
    }
}
