package org.datakurator.postprocess.xlsx;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet("Final Values");
        sheet.setRandomAccessWindowSize(100);// keep 100 rows in memory, exceeding rows will be flushed to disk

        try {
            int rowNum = 0;

            while(reportParser.next()) {
                //Map<String, String> initialValues = reportParser.getInitialValues();
                Map<String, String> finalValues = reportParser.getFinalValues();

                if (header == null) {
                    header = new ArrayList<>();
                    for (String key : finalValues.keySet()) {
                        header.add(key);
                    }

                    Row row = sheet.createRow(rowNum);

                    for (int i = 0; i < header.size(); i++) {
                        row.createCell(i).setCellValue(header.get(i));
                    }

                    rowNum++;
                }

                Row row = sheet.createRow(rowNum);

                for (int i = 0; i < header.size(); i++) {
                    String value = finalValues.get(header.get(i));
                    row.createCell(i).setCellValue(value);
                }

                rowNum++;
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
        XLSXPostProcessor postProcessor = new XLSXPostProcessor(DQReportParser.class.getResourceAsStream("/dq_report.json"));
        postProcessor.postprocess();
    }
}
