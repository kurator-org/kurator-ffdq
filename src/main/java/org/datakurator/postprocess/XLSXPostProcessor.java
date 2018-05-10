/** XLSXPostProcessor.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datakurator.postprocess;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.model.DataResource;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.ContextualizedDimension;
import org.datakurator.ffdq.model.report.*;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.datakurator.ffdq.rdf.Namespace;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by lowery on 2/17/2017.
 */
public class XLSXPostProcessor {
    private FFDQModel model;

    private List<String> header;

    private SXSSFWorkbook workbook;
    private Map<String, CellStyle> styles = new HashMap<>();

    private static Map<String, Integer> actedUponCols = new HashMap<>();


    int measuresSheetRowNum = 1;
    int validationsSheetRowNum = 1;

    int initialValuesSheetRowNum = 1;
    int finalValuesSheetRowNum = 1;

    public XLSXPostProcessor(FFDQModel model) {
        this.model = model;
    }

    public void initStyles(SXSSFWorkbook wb) {
        // White font
        Font font = wb.createFont();
        font.setColor(HSSFColor.WHITE.index);

        // Compliant or complete styled with green background
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.GREEN.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put("COMPLIANT", style);
        styles.put("COMPLETE", style);

        // Not compliant or not complete styled with red background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.RED.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put("NOT_COMPLIANT", style);
        styles.put("NOT_COMPLETE", style);

        // Filled in, curated or transposed styled with yellow background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.DARK_YELLOW.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put("FILLED_IN", style);
        styles.put("CURATED", style);
        styles.put("TRANSPOSED", style);

        // Unable determine validity styled with grey background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put("DATA_PREREQUISITES_NOT_MET", style);
        styles.put("EXTERNAL_PREREQUISITES_NOT_MET", style);
    }

    public void postprocess(OutputStream out) {
        int windowSize = 100; // keep 100 rows in memory, exceeding rows will be flushed to disk

        // Create the workbook and initialize cell styles
        workbook = new SXSSFWorkbook();
        initStyles(workbook);

        // Summary sheet containing descriptive text
        String summaryText = "The sheet labeled \"Final Values\" contains data including any changes " +
                "made as part of running the workflow. The sheet labeled \"Initial Values\" contains the original data " +
                "supplied as input to the workflow.\n" +
                "\n" +
                "The \"Validations\" sheet gives a summary for each of the validation tests performed. The \"Amendments " +
                "sheet summarizes any changes made to the records. In both sheets rows indicating the test results are " +
                "grouped by record and separated by spaces.\n" +
                "\n" +
                "The 'Measures' sheet contains the value of any measurements performed (i.e. precision, completeness).";

        createSummarySheet(summaryText);

        // Get all data resources (initial values) associated with assertions in the report
        List<DataResource> dataResources = model.findDataResources();

        // Create the initial values sheet
        Sheet initialValuesSheet = createInitialValuesSheet(dataResources);

        // Create the final values sheet
        Sheet finalValuesSheet = createFinalValuesSheet(dataResources);

        // Create the measures sheet
        Sheet measuresSheet = createMeasuresSheet();

        // Create the validations sheet
        Sheet validationsSheet = createValidationsSheet();

        // Obtain headers from the first record
        // Set<String> headers = dataResources.get(0).asMap().keySet();

        // System.out.println(headers);

        for (DataResource dataResource : dataResources) {
            String recordId = dataResource.getRecordId();

            // Get the assertions for each data resource
            List<Assertion> measures = model.findAssertionsForDataResource(dataResource, Measure.class);
            List<Assertion> validations = model.findAssertionsForDataResource(dataResource, Validation.class);
            List<Assertion> amendments = model.findAssertionsForDataResource(dataResource, Amendment.class);

            // Initialize the validation states for initial and final values
            Map<String, ValidationState> initialValues = new HashMap<>();
            Map<String, ValidationState> finalValues = new HashMap<>();

            Map<String, String> dataResourceValues = dataResource.asMap();
            for (String key : dataResourceValues.keySet()) {
                ValidationState initialValue = new ValidationState();
                ValidationState finalValue = new ValidationState();

                initialValue.setValue(dataResourceValues.get(key));
                initialValues.put(key, initialValue);

                finalValue.setValue(dataResourceValues.get(key));
                finalValues.put(key, finalValue);
            }

            // TODO: add support to postprocessor for issues

            initMeasuresSheet(measuresSheet, measures, dataResource);
            initValidationsSheet(validationsSheet, validations, dataResource);

            for (Assertion assertion : measures) {
                Specification specification = assertion.getSpecification();
                String test = specification.getLabel();

                Result result = assertion.getResult();

                ResultState state = result.getState();
                Entity value = result.getEntity();

                String status = determineRowStatus(state, value);

                //System.out.println(recordId + ", " + test + ", " + status);

                Measure measure = (Measure) assertion;

                //if (value != null) {
                    //System.out.println(value.getValue() + " : " + state.getLabel());
               // }
            }

            for (Assertion assertion : validations) {
                Validation validation = (Validation) assertion;
                Result result = validation.getResult();

                Entity value = result.getEntity();
                ResultState state = result.getState();

                List<URI> composedOf = validation.getCriterion().getInformationElements().getComposedOf();
                for (URI uri : composedOf) {
                    String term = uri.getPath();
                    term = term.substring(term.lastIndexOf("/")+1);

                    if (value != null) {
                        String status = value.getValue().toString();

                        //System.out.println(term + " : " + status);
                        initialValues.get(term).setStatus(status);
                    } else {
                        initialValues.get(term).setStatus(state.getLabel());
                    }
                }

               // if (value != null) {
                //    System.out.println(value.getValue() + " : " + state.getLabel() + " : " + result.getComment());
                //}
            }

            for (Assertion assertion : amendments) {
                Amendment amendment = (Amendment) assertion;
                Result result = amendment.getResult();

                Entity value = result.getEntity();
                ResultState state = result.getState();

                List<URI> composedOf = amendment.getEnhancement().getInformationElements().getComposedOf();

                if (value != null && state != null) {
                    Map<String, String> amendedValues = model.findDataResource((URI) value.getValue()).asMap();

                    for (String term : amendedValues.keySet()) {
                        finalValues.get(term).setValue(amendedValues.get(term));
                        finalValues.get(term).setStatus(state.getLabel());
                    }

                } else if (state != null && !state.equals(ResultState.NO_CHANGE)) {
                    for (URI uri : composedOf) {
                        String term = uri.getPath();
                        term = term.substring(term.lastIndexOf("/") + 1);

                        finalValues.get(term).setStatus(state.getLabel());
                    }

                }

                //if (value != null) {
                    //System.out.println(amendment.getDataResource() + " : " + value.getValue());
                    //System.out.println(value.getValue() + " : " + state.getLabel() + " : " + result.getComment());
               // }
            }

            // Add rows to initial and final values sheets
            Row initialValuesRow = initialValuesSheet.createRow(initialValuesSheetRowNum++);

            int colNum = 0;
            for (ValidationState validationState : initialValues.values()) {
                Cell initialValuesCell = initialValuesRow.createCell(colNum);

                initialValuesCell.setCellValue(validationState.getValue());
                initialValuesCell.setCellStyle(styles.get(validationState.getStatus()));

                colNum++;
            }

            Row finalValuesRow = finalValuesSheet.createRow(finalValuesSheetRowNum++);

            colNum = 0;
            for (ValidationState validationState : finalValues.values()) {
                Cell finalValuesCell = finalValuesRow.createCell(colNum);

                finalValuesCell.setCellValue(validationState.getValue());
                finalValuesCell.setCellStyle(styles.get(validationState.getStatus()));

                colNum++;
            }
        }



        try {
            /*while(reportParser.next()) {

                if (initialValuesSummary == null) {
                    initialValuesSummary = new ReportSummary(reportParser.getDataResourceFields(), "Initial Values", workbook, styles);
                }

                if (finalValuesSummary == null) {
                    finalValuesSummary = new ReportSummary(reportParser.getDataResourceFields(), "Final Values", workbook, styles);
                }

                if (validationSummary == null) {
                    validationSummary = new AssertionSummary(reportParser.getAssertionFields(), "Validations", workbook, styles, Validation.class);
                }

                if (measureSummary == null) {
                    measureSummary = new AssertionSummary(reportParser.getAssertionFields(), "Measures", workbook, styles, Measure.class);
                }

                if (amendmentSummary == null) {
                    amendmentSummary = new AssertionSummary(reportParser.getAssertionFields(), "Amendments", workbook, styles, Improvement.class);
                }

                Map<String, String> initialValues = reportParser.getInitialValues();
                Map<String, String> finalValues = reportParser.getFinalValues();

                // TODO: Fix color coding on final values sheet
                Map<String, String> validationState = new HashMap<>();
                Map<String, String> amendmentState = reportParser.getAmendmentState();

                // process assertions and create flags
                StringBuilder rowValidationTests = new StringBuilder();

                for (Validation validation : reportParser.getValidations()) {
                    String test = validation.getTest().getName();
                    String status = validation.getStatus();

                    String flag = "[" + test + "_" + status + "] ";
                    rowValidationTests.append(flag);

                    for (String field : validation.getContext().getFieldsActedUpon()) {
                        String fieldStatus = validationState.get(field);

                        if (status.equals("COMPLIANT") && fieldStatus == null) {
                            fieldStatus = "COMPLIANT";
                        } else if (status.equals("DATA_PREREQUISITES_NOT_MET") && fieldStatus != "NON_COMPLIANT") {
                            fieldStatus = "DATA_PREREQUISITES_NOT_MET";
                        } else if (status.equals("NON_COMPLIANT")) {
                            fieldStatus = "NON_COMPLIANT";
                        }

                        validationState.put(field, fieldStatus);
                    }
                }

                StringBuilder rowAmendmentTests = new StringBuilder();
                for (Improvement improvement : reportParser.getAmendments()) {
                    String test = improvement.getTest().getName();
                    String status = improvement.getStatus();

                    String flag = "[" + test + "_" + status + "] ";
                    rowAmendmentTests.append(flag);

                    for (String field : improvement.getContext().getFieldsActedUpon()) {
                        String fieldStatus = amendmentState.get(field);

                        if (status.equals("NO_CHANGE") && fieldStatus == null) {
                            fieldStatus = "NO_CHANGE";
                        } else if (status.equals("DATA_PREREQUISITES_NOT_MET") && !fieldStatus.equals("FILLED_IN")) {
                            fieldStatus = "DATA_PREREQUISITES_NOT_MET";
                        } else if (status.equals("FILLED_IN")) {
                            fieldStatus = "FILLED_IN";
                        }

                        amendmentState.put(field, fieldStatus);
                    }

                }

                String recordId = reportParser.getRecordId();

                initialValuesSummary.postprocess(initialValues, validationState, rowValidationTests.toString());
                finalValuesSummary.postprocess(finalValues, amendmentState, rowAmendmentTests.toString());

                validationSummary.postprocess(reportParser.getValidations(), initialValues, recordId);
                measureSummary.postprocess(reportParser.getMeasures(), initialValues, recordId);
                amendmentSummary.postprocess(reportParser.getAmendments(), initialValues, recordId);

            }*/

            workbook.write(out);
            out.close();

            // dispose of temporary files backing this workbook on disk
            workbook.dispose();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Sheet createFinalValuesSheet(List<DataResource> dataResources) {
        SXSSFSheet finalValuesSheet = (SXSSFSheet) workbook.createSheet("Final Values");

        // Obtain headers from the first record
        Object[] headers = dataResources.get(0).asMap().keySet().toArray();

        Row headerRow = finalValuesSheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i].toString());
        }

        return finalValuesSheet;
    }


    private void createSummarySheet(String summaryText) {
        SXSSFSheet summarySheet = (SXSSFSheet) workbook.createSheet("Summary");

        // Wrap text on the summary page
        CellStyle summaryStyle = workbook.createCellStyle();
        summaryStyle.setWrapText(true);

        // Add the summary text to the cell
        Row summaryRow = summarySheet.createRow(1);
        Cell summaryCell = summaryRow.createCell(1);

        summaryCell.setCellValue(summaryText);
        summaryCell.setCellStyle(summaryStyle);

        // Create a merged region from (1,1) to (7,9) for the summary text
        summarySheet.addMergedRegion(new CellRangeAddress(1,7,1,9));
    }

    private SXSSFSheet createInitialValuesSheet(List<DataResource> dataResources) {
        SXSSFSheet initialValuesSheet = (SXSSFSheet) workbook.createSheet("Initial Values");

        // Obtain headers from the first record
        Object[] headers = dataResources.get(0).asMap().keySet().toArray();

        Row headerRow = initialValuesSheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i].toString());
        }

/*        int rowOffset = 1; // offset due to first row used as header

       for (int i = 0; i < dataResources.size(); i++) {
            // Create a row for each data resource
            Row initialValuesRow = initialValuesSheet.createRow(i+rowOffset);

            // Get the initial values from the data resource map and add values to cells
            DataResource dataResource = dataResources.get(i);

            int colNum = 0;
            Map<String, String> initialValues = dataResource.asMap();

            for (String field : initialValues.keySet())  {
                Cell initialValuesCell = initialValuesRow.createCell(colNum);

                initialValuesCell.setCellValue(initialValues.get(field));
                colNum++;
            }
        }*/

        return initialValuesSheet;
    }

    private SXSSFSheet createMeasuresSheet() {
        SXSSFSheet measuresSheet = (SXSSFSheet) workbook.createSheet("Measures");

        // Create the header row
        Row headerRow = measuresSheet.createRow(0);

        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Value");
        headerRow.createCell(4).setCellValue("Comment");

        return measuresSheet;
    }

    private void initMeasuresSheet(Sheet measuresSheet, List<Assertion> measures, DataResource dataResource) {
        // Get the list of fields actedUpon from the information elements
        List<String> fields = model.findFieldsByAssertionType(Measure.class);

        for (Assertion assertion : measures) {
            Measure measure = (Measure) assertion;
            String recordId = dataResource.getRecordId();

            // Measurement assertion row
            Row measuresRow = measuresSheet.createRow(measuresSheetRowNum++);

            // Get the test name from the specification
            Specification specification = measure.getSpecification();
            String test = specification.getLabel();

            // Measurement result
            Result result = measure.getResult();

            // Determine row status from state and value
            ResultState state = result.getState();
            Entity entity = result.getEntity();

            // String status = determineRowStatus(state, entity);
            String status = "";
            String value = "";

            if (entity != null && entity.getValue() != null) {
                value = entity.getValue().toString();
            }

            if (state != null) {
                status = state.getLabel();
            }

            String comment = result.getComment();
            System.out.println(comment);

            // Create assertion metadata cells
            measuresRow.createCell(0).setCellValue(recordId);
            measuresRow.createCell(1).setCellValue(test);
            measuresRow.createCell(2).setCellValue(status);
            measuresRow.createCell(3).setCellValue(value);
            measuresRow.createCell(4).setCellValue(comment);

            //ContextualizedDimension context = measure.getDimension();
            for (String field : fields) {

                // Lookup column index for field and create cell for the values
                //if (!fields.contains(field)) {
                //    fields.add(field);
                //}

                int i = fields.indexOf(field);

                Cell measuresCell = measuresRow.createCell(i+5);

                // Set cell value and style based on status
                try {
                    measuresCell.setCellValue(dataResource.get(new URI(field)));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                measuresCell.setCellStyle(styles.get(status));
            }

            System.out.println(recordId + ", " + test + ", " + status);
        }

        // empty row between blocks of measures
        measuresSheetRowNum++;

    }


    private SXSSFSheet createValidationsSheet() {
        SXSSFSheet validationsSheet = (SXSSFSheet) workbook.createSheet("Validations");

        // Create the header row
        Row headerRow = validationsSheet.createRow(0);

        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Value");
        headerRow.createCell(4).setCellValue("Comment");

        return validationsSheet;
    }

    private void initValidationsSheet(Sheet validationsSheet, List<Assertion> validations, DataResource dataResource) {
        // Get the list of fields actedUpon from the information elements
        List<String> fields = model.findFieldsByAssertionType(Validation.class);
        System.out.println(fields);

        for (Assertion assertion : validations) {
            Validation validation = (Validation) assertion;
            String recordId = dataResource.getRecordId();

            // Validation assertion row
            Row validationsRow = validationsSheet.createRow(validationsSheetRowNum++);

            // Get the test name from the specification
            Specification specification = validation.getSpecification();
            String test = specification.getLabel();

            // Validation result
            Result result = validation.getResult();

            // Determine row status from state and value
            ResultState state = result.getState();
            Entity entity = result.getEntity();

            // String status = determineRowStatus(state, entity);
            String status = "";
            String value = "";

            if (entity != null && entity.getValue() != null) {
                value = entity.getValue().toString();
            }

            if (state != null) {
                status = state.getLabel();
            }

            String comment = result.getComment();

            // Create assertion metadata cells
            validationsRow.createCell(0).setCellValue(recordId);
            validationsRow.createCell(1).setCellValue(test);
            validationsRow.createCell(2).setCellValue(status);
            validationsRow.createCell(3).setCellValue(value);
            validationsRow.createCell(4).setCellValue(comment);

            //ContextualizedDimension context = measure.getDimension();
            for (String field : fields) {

                // Lookup column index for field and create cell for the values
                //if (!fields.contains(field)) {
                //    fields.add(field);
                //}

                int i = fields.indexOf(field);

                Cell validationsCell = validationsRow.createCell(i+5);

                // Set cell value and style based on status
                try {
                    validationsCell.setCellValue(dataResource.get(new URI(field)));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                validationsCell.setCellStyle(styles.get(status));
            }

            System.out.println(recordId + ", " + test + ", " + status);
        }

        // empty row between blocks of measures
        validationsSheetRowNum++;

    }

    private List<String> fieldsFromContext(ContextualizedDimension context) {
        List<String> fields = new ArrayList<>();

        List<URI> ie = context.getInformationElements().getComposedOf();
        for (URI uri : ie) {
            String path = uri.getPath();
            String field = path.substring(path.lastIndexOf('/') + 1);

            fields.add(field);
        }

        return fields;
    }

    private String determineRowStatus(ResultState state, Entity value) {
        String status = null;

        if (state.equals(ResultState.RUN_HAS_RESULT)) {
            if (value.getValue() instanceof String) {
                // Measures or Validations with result: COMPLETE, NOT_COMPLETE, COMPLIANT, NOT_COMPLIANT
                status = (String) value.getValue();
            } else if (value.getValue() instanceof Long || value.getValue() instanceof Integer) {
                // Measurement value, use status HAS_RESULT
                status = state.getLabel();
            }
        } else if (state.equals(ResultState.INTERNAL_PREREQUISITES_NOT_MET) ||
                state.equals(ResultState.EXTERNAL_PREREQUISITES_NOT_MET) ||
                state.equals(ResultState.UNABLE_CURATE)) {
            // Use state as status, one of the error conditions: INTERNAL_PREREQUISITES_NOT_MET, EXTERNAL_PREREQUISITES_NOT_MET,
            // UNABLE_CURATE
            status = state.getLabel();
        } else {
            status = "*"+state.getLabel();
        }

        return status;
    }


    public static void main(String[] args) throws IOException {
        FFDQModel model = new FFDQModel();
        model.load(new FileInputStream("/home/lowery/IdeaProjects/event_date_qc/target/dq-report.ttl"), RDFFormat.TURTLE);

        XLSXPostProcessor postProcessor = new XLSXPostProcessor(model);
        postProcessor.postprocess(new FileOutputStream("tempsxssf.xlsx"));
    }
}
