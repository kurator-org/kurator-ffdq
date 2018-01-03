package org.datakurator.ffdq.util;

import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

public class XLSXConverter {

    // List of test guids to include
    public static String[] guids = new String[]{

            "b0753f69-08c1-45f5-a5ca-48d24e76d813",
            "0a59e03f-ebb5-4df3-a802-2e444de525b5",
            "da63f836-1fc6-4e96-a612-fa76678cfd6a",
            "6d0a0c10-5e4a-4759-b448-88932f399812",
            "8cdd4f44-e7ed-4484-a1b8-4e6407a491e2",
            "e4ddf9bc-cd10-46cc-b307-d6c7233a240a",
            "62a9c256-43e4-41ee-8938-d2d2e99479ef",
            "134c7b4f-1261-41ec-acb5-69cd4bc8556f",
            "367bf43f-9cb6-45b2-b45f-b8152f1d334a",
            "39bb2280-1215-447b-9221-fd13bc990641",
            "48aa7d66-36d1-4662-a503-df170f11b03f",
            "01c6dafa-0886-4b7e-9881-2c3018c98bdc",
            "fd00e6be-45e4-4ced-9f3d-5cde30b21b69",
            "31d463b4-2a1c-4b90-b6c7-73459d1bad6d",
            "5618f083-d55a-4ac2-92b5-b9fb227b832f",
            "f98a54eb-59e7-44c7-b96f-200e6af1c895"

    };

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        options.addRequiredOption("in", null, true, "Input CSV file containing list of tests");
        options.addRequiredOption("out", null, true, "Output file for the markdown representation of the tests");

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            File csvFile = new File(cmd.getOptionValue("in"));

            if (!csvFile.exists()) {
                throw new FileNotFoundException("CSV input file not found: " + csvFile.getAbsolutePath());
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(cmd.getOptionValue("out")));
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("GUID", "Label", "Description", "Specification", "Type", "Resource Type", "Dimension",
                            "Information Element", "Source", "Example Implementation"));

            Reader reader = new InputStreamReader(new FileInputStream(csvFile));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            for (CSVRecord record : records) {
                if (Arrays.asList(guids).contains(record.get("GUID"))) {
                    printer.printRecord(record.get("GUID"), record.get("Variable"), record.get("Description (test - PASS)"),
                            record.get("Specification (Technical Description)"), record.get("Output Type"),
                            record.get("Record Resolution"), record.get("DQ Dimension"), record.get("Darwin Core Terms"),
                            record.get("Source"), record.get("Link to Specification Source Code"));

                    printer.flush();
                }
            }

            printer.close();
        } catch (ParseException e) {
            System.out.println("ERROR: " + e.getMessage() + "\n");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar ffdq-util.jar", options);
        }
    }


    private void parseXlsx(InputStream input) throws IOException {
        // TODO: Update to support latest version of the spreadsheet

        int LABEL_IDX = -1, GUID_IDX = -1, DESCRIPTION_IDX = -1, SPECIFICATION_IDX = -1,
                RESOURCE_TYPE_IDX = -1, ASSERTION_TYPE_IDX = -1, INFORMATION_ELEMENTS_IDX = -1,
                DIMENSION_IDX = -1;

        Workbook workbook = new XSSFWorkbook(input);
        Sheet sheet = workbook.getSheet("Tests-current");

        // Header cell iterator
        Iterator<Cell> header = sheet.getRow(0).cellIterator();

        // Find column numbers from the headers relevant to ffdq
        for (int i = 0; header.hasNext(); i++) {
            Cell cell = header.next();
            String value = cell.getStringCellValue();
            if (value.equals("Label")) {
                LABEL_IDX = i;
            } else if (value.equals("GUID")) {
                GUID_IDX = i;
            } else if (value.equals("Description (test - PASS)")) {
                DESCRIPTION_IDX = i;
            } else if (value.equals("Specification (Technical Description)")) {
                SPECIFICATION_IDX = i;
            } else if (value.equals("Resource Type")) {
                RESOURCE_TYPE_IDX = i;
            } else if (value.equals("Output Type")) {
                ASSERTION_TYPE_IDX = i;
            } else if (value.equals("Information Element")) {
                INFORMATION_ELEMENTS_IDX = i;
            } else if (value.equals("DQ Dimension")) {
                DIMENSION_IDX = i;
            }
        }

        Iterator<Row> rows = sheet.rowIterator();
        rows.next(); // skip header row

        while (rows.hasNext()) {
            Row row = rows.next();

            Cell labelCell = row.getCell(LABEL_IDX);
            Cell guidCell = row.getCell(GUID_IDX);

            if (labelCell != null && guidCell != null) {
                String label = labelCell.getStringCellValue().trim();
                String guid = "urn:uuid:" + guidCell.getStringCellValue().trim();
                String description = row.getCell(DESCRIPTION_IDX).getStringCellValue().trim();
                String specification = row.getCell(SPECIFICATION_IDX).getStringCellValue().trim();
                String resourceType = row.getCell(RESOURCE_TYPE_IDX).getStringCellValue().trim();
                String assertionType = row.getCell(ASSERTION_TYPE_IDX).getStringCellValue().trim();
                String informationElements = row.getCell(INFORMATION_ELEMENTS_IDX).getStringCellValue().trim();
                String dimension = row.getCell(DIMENSION_IDX).getStringCellValue().trim();

                boolean errors = false;
                if (!label.isEmpty() && label != null) {

                    if (guid.isEmpty() || guid == null) {
                        errors = true;
                        System.err.println("ERROR: Test " + label + " does not have an associated guid");
                    }

                }
            }
        }
    }

}
