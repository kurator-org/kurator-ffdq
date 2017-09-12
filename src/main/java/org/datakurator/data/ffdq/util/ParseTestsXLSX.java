package org.datakurator.data.ffdq.util;

import org.apache.commons.cli.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lowery on 9/12/17.
 */
public class ParseTestsXLSX {

    public static void main(String[] args) throws ParseException, IOException {
        Options options = new Options();

        options.addOption("i", "in", true, "XLSX file that contains the \"Tests-current\" sheet.");
        options.addOption("o", "out", true, "Output file for the rdf representation of the tests.");
        options.addOption("f", "format", true, "Output format (RDFXML, TURTLE, JSON-LD)");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("i") && cmd.hasOption("o")) {

            // Get option values
            String input = cmd.getOptionValue("i");
            String output = cmd.getOptionValue("o");

            // Default output format is turtle
            RDFFormat format = RDFFormat.TURTLE;

            if (cmd.hasOption("f")) {
                String value = cmd.getOptionValue("f");

                switch (value) {
                    case "RDFXML":
                        format = RDFFormat.RDFXML;
                        break;
                    case "TURTLE":
                        format = RDFFormat.TURTLE;
                        break;
                    case "JSON-LD":
                        format = RDFFormat.JSONLD;
                        break;
                }
            }

            parseXlsx(new FileInputStream(new File(input)));
        } else {

            // Print usage
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ffdq", options);
        }
    }

    private static void parseXlsx(InputStream input) throws IOException {
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

            String label = row.getCell(LABEL_IDX).getStringCellValue().trim();
            String guid = row.getCell(GUID_IDX).getStringCellValue().trim();
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

                System.out.println(label + " - " + guid + ": " + specification);
            }
        }
    }

}
