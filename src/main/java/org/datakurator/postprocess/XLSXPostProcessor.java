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
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.CompletenessValue;
import org.datakurator.ffdq.model.*;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.context.Measure;
import org.datakurator.ffdq.model.context.Amendment;
import org.datakurator.ffdq.model.report.*;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.datakurator.ffdq.rdf.Namespace;
import org.eclipse.rdf4j.rio.RDFFormat;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by lowery on 2/17/2017.
 *
 * @author mole
 * @version $Id: $Id
 */
public class XLSXPostProcessor {
    private FFDQModel model;

    private SXSSFWorkbook workbook;
    private Map<String, CellStyle> styles = new HashMap<>();

    private List<String> fields;

    private int measuresSheetRowNum = 1;
    private int validationsSheetRowNum = 1;
    private int amendmentsSheetRowNum = 1;

    private int initialValuesSheetRowNum = 1;
    private int finalValuesSheetRowNum = 1;

    /**
     * <p>Constructor for XLSXPostProcessor.</p>
     *
     * @param model a {@link org.datakurator.ffdq.rdf.FFDQModel} object.
     */
    public XLSXPostProcessor(FFDQModel model) {
        this.model = model;
    }

    /**
     * <p>initStyles.</p>
     *
     * @param wb a {@link org.apache.poi.xssf.streaming.SXSSFWorkbook} object.
     */
    public void initStyles(SXSSFWorkbook wb) {
        // White font
        Font font = wb.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());

        // Compliant or complete styled with green background
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put("COMPLIANT", style);
        styles.put("COMPLETE", style);

        // Not compliant or not complete styled with red background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put("NOT_COMPLIANT", style);
        styles.put("NOT_COMPLETE", style);

        // Filled in, curated or transposed styled with yellow background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put("FILLED_IN", style);
        styles.put("CURATED", style);
        styles.put("TRANSPOSED", style);

        // Unable determine validity styled with grey background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);

        styles.put("INTERNAL_PREREQUISITES_NOT_MET", style);
        styles.put("EXTERNAL_PREREQUISITES_NOT_MET", style);
    }

    /**
     * <p>postprocess.</p>
     *
     * @param out a {@link java.io.OutputStream} object.
     */
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

        // Get fields from first data resource
        fields = new ArrayList<String>(dataResources.get(0).asMap().keySet());

        // Create the initial values sheet
        Sheet initialValuesSheet = createInitialValuesSheet(dataResources);

        // Create the final values sheet
        Sheet finalValuesSheet = createFinalValuesSheet(dataResources);

        // Create the measures sheet
        Sheet measuresSheet = createMeasuresSheet(fields);

        // Create the validations sheet
        Sheet validationsSheet = createValidationsSheet(fields);

        // Create the amendments sheet
        Sheet amendmentsSheet = createAmendmentsSheet(fields);

        // Obtain headers from the first record
        // Set<String> headers = dataResources.get(0).asMap().keySet();

        // System.out.println(headers);

        for (DataResource dataResource : dataResources) {
            // Get the assertions for each data resource
            List<Assertion> measures = model.findAssertionsForDataResource(dataResource, MeasureAssertion.class);
            List<Assertion> validations = model.findAssertionsForDataResource(dataResource, ValidationAssertion.class);
            List<Assertion> amendments = model.findAssertionsForDataResource(dataResource, AmendmentAssertion.class);

            // TODO: add support to postprocessor for issues

            initMeasuresSheet(measuresSheet, measures, dataResource);
            initValidationsSheet(validationsSheet, initialValuesSheet, validations, dataResource);
            initAmendmentsSheet(amendmentsSheet, finalValuesSheet, amendments, dataResource);
        }

        try {
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

        headerRow.createCell(headers.length).setCellValue("Flags");

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

        return initialValuesSheet;
    }

    private SXSSFSheet createMeasuresSheet(List<String> fields) {
        SXSSFSheet measuresSheet = (SXSSFSheet) workbook.createSheet("Measures");

        // Create the header row
        Row headerRow = measuresSheet.createRow(0);

        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Value");
        headerRow.createCell(4).setCellValue("Comment");

        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i+5).setCellValue(fields.get(i));
        }

        return measuresSheet;
    }

    private void initMeasuresSheet(Sheet measuresSheet, List<Assertion> measures, DataResource dataResource) {
        // Get the list of fields actedUpon from the information elements
        //List<String> fields = model.findFieldsByAssertionType(MeasureAssertion.class);

        for (Assertion assertion : measures) {
            MeasureAssertion measure = (MeasureAssertion) assertion;
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

            // Create assertion metadata cells
            measuresRow.createCell(0).setCellValue(recordId);
            measuresRow.createCell(1).setCellValue(test);
            measuresRow.createCell(2).setCellValue(status);
            measuresRow.createCell(3).setCellValue(value);
            measuresRow.createCell(4).setCellValue(comment);

            // Get values from measure and add color coding based on status to fields acted upon
            Map<String, String> values = dataResource.asMap();
            List<String> actedUpon = fieldsFromMeasureContext(measure.getDimension());

            for (int i = 0; i < fields.size(); i++) {
                Cell cell = measuresRow.createCell(i+5);
                cell.setCellValue(values.get(fields.get(i)));

                if (actedUpon.contains(fields.get(i)) &&
                        (value.equals("COMPLETE") || value.equals("NOT_COMPLETE"))) {
                    cell.setCellStyle(styles.get(value));
                }
            }
        }

        // empty row between blocks of measures
        measuresSheetRowNum++;
    }

    private SXSSFSheet createValidationsSheet(List<String> fields) {
        SXSSFSheet validationsSheet = (SXSSFSheet) workbook.createSheet("Validations");

        // Create the header row
        Row headerRow = validationsSheet.createRow(0);

        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Value");
        headerRow.createCell(4).setCellValue("Comment");

        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i+5).setCellValue(fields.get(i));
        }

        return validationsSheet;
    }

    private void initValidationsSheet(Sheet validationsSheet, Sheet initialValuesSheet, List<Assertion> validations, DataResource dataResource) {
        // Get the list of fields actedUpon from the information elements
        //List<String> fields = model.findFieldsByAssertionType(ValidationAssertion.class);

        List<Map<String, ValidationState>> allInitialValues = new ArrayList<>();

        List<String> allFlags = new ArrayList<>();

        for (Assertion assertion : validations) {
            // Initialize the validation states for initial and final values
            Map<String, ValidationState> initialValues = new HashMap<>();

            ValidationAssertion validation = (ValidationAssertion) assertion;
            String recordId = dataResource.getRecordId();

            // ValidationAssertion assertion row
            Row validationsRow = validationsSheet.createRow(validationsSheetRowNum++);

            // Get the test name from the specification
            Specification specification = validation.getSpecification();
            String test = specification.getLabel();

            // ValidationAssertion result
            Result result = validation.getResult();

            // Determine row status from state and value
            ResultState state = result.getState();
            Entity entity = result.getEntity();

            // String status = determineRowStatus(state, entity);
            String assertionStatus = "";
            String assertionValue = "";

            if (entity != null && entity.getValue() != null) {
                assertionValue = entity.getValue().toString();
            }

            if (state != null) {
                assertionStatus = state.getLabel();
            }

            // Add flag string
            if (assertionValue.equals("NOT_COMPLIANT")) {
                allFlags.add(test + "_" + assertionValue);
            }

            String comment = result.getComment();

            // Create assertion metadata cells
            validationsRow.createCell(0).setCellValue(recordId);
            validationsRow.createCell(1).setCellValue(test);
            validationsRow.createCell(2).setCellValue(assertionStatus);
            validationsRow.createCell(3).setCellValue(assertionValue);
            validationsRow.createCell(4).setCellValue(comment);

            // Get values from validation and add color coding based on status to fields acted upon
            Map<String, String> values = dataResource.asMap();
            List<String> actedUpon = fieldsFromValidationContext(validation.getCriterion());

            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                String value = values.get(field);

                if (!initialValues.containsKey(field)) {
                    initialValues.put(field, new ValidationState());
                }

                Cell cell = validationsRow.createCell(i+5);
                cell.setCellValue(value);

                ValidationState initialValue = initialValues.get(field);

                // Put validation state for each field into initialValues map
                initialValues.put(field, initialValue);
                initialValue.setValue(value);

                if (actedUpon.contains(fields.get(i))) {
                    if (assertionValue.equals("COMPLIANT") || assertionValue.equals("NOT_COMPLIANT")) {
                        // If the value COMPLIANT or NOT_COMPLIANT is present use value for color coding
                        cell.setCellStyle(styles.get(assertionValue));
                        initialValue.setStatus(assertionValue);
                    } else {
                        // Otherwise use status
                        cell.setCellStyle(styles.get(assertionStatus));
                        initialValue.setStatus(assertionStatus);
                    }
                }
            }

            allInitialValues.add(initialValues);
        }

        // Add rows to initial values sheets
        Row initialValuesRow = initialValuesSheet.createRow(initialValuesSheetRowNum++);

        int colNum = 0;
        for (String field : fields) {

            boolean validFlag = false;
            boolean failureFlag = false;
            boolean prerequisitesNotMetFlag = false;

            for (Map<String, ValidationState> initialValues : allInitialValues) {
                ValidationState initialValue = initialValues.get(field);

                if (initialValue.getStatus() != null) {
                    if (initialValue.getStatus().equals("COMPLIANT")) {
                        validFlag = true;
                    }

                    if (initialValue.getStatus().equals("NOT_COMPLIANT")) {
                        failureFlag = true;
                    }

                    if (initialValue.getStatus().equals("UNABLE_CURATE") ||
                            initialValue.getStatus().equals("INTERNAL_PREREQUISITES_NOT_MET") ||
                            initialValue.getStatus().equals("EXTERNAL_PREREQUISITES_NOT_MET")) {

                        prerequisitesNotMetFlag = true;
                    }
                }
            }

            String value = dataResource.asMap().get(field);
            Cell initialValuesCell = initialValuesRow.createCell(colNum);
            initialValuesCell.setCellValue(value);

            if (value != null && !value.isEmpty()) {
                if (prerequisitesNotMetFlag) {
                    initialValuesCell.setCellStyle(styles.get("UNABLE_CURATE"));
                }

                if (validFlag) {
                    initialValuesCell.setCellStyle(styles.get("COMPLIANT"));
                }

                if (failureFlag) {
                    initialValuesCell.setCellStyle(styles.get("NOT_COMPLIANT"));
                }
            }

            colNum++;
        }

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell flagsCell = initialValuesRow.createCell(colNum);
        flagsCell.setCellValue(allFlags.toString());
        flagsCell.setCellStyle(style);

        // empty row between blocks of measures
        validationsSheetRowNum++;
    }

    private SXSSFSheet createAmendmentsSheet(List<String> fields) {
        SXSSFSheet amendmentsSheet = (SXSSFSheet) workbook.createSheet("Amendments");

        // Create the header row
        Row headerRow = amendmentsSheet.createRow(0);

        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Comment");

        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i+4).setCellValue(fields.get(i));
        }

        return amendmentsSheet;
    }

    private void initAmendmentsSheet(Sheet amendmentsSheet, Sheet finalValuesSheet, List<Assertion> amendments, DataResource dataResource) {
        Map<String, ValidationState> finalValues = new HashMap<>();
        Map<String, String> prevValues = dataResource.asMap();

        // initialize the final values
        for (String field : fields) {
            ValidationState validationState = new ValidationState();
            validationState.setValue(prevValues.get(field));

            finalValues.put(field, validationState);
        }

        // All amendment flags
        List<String> allFlags = new ArrayList<>();

        for (Assertion assertion : amendments) {
            AmendmentAssertion amendment = (AmendmentAssertion) assertion;
            String recordId = dataResource.getRecordId();

            // AmendmentAssertion assertion row
            Row amendmentsRow = amendmentsSheet.createRow(amendmentsSheetRowNum++);

            // Get the test name from the specification
            Specification specification = amendment.getSpecification();
            String test = specification.getLabel();

            // ValidationAssertion result
            Result result = amendment.getResult();

            // Determine row status from state and value
            ResultState state = result.getState();
            Entity entity = result.getEntity();

            Map<String, String> amendedValues = new HashMap<>();

            if (entity != null && entity.getValue() != null) {
                amendedValues = model.findDataResource((URI) entity.getValue()).asMap();
            }

            String status = "";

            if (state != null) {
                status = state.getLabel();
            }

            // Append any status but NO_CHANGE to flags string
            if (!status.equals("NO_CHANGE")) {
                allFlags.add(test + "_" + status);
            }

            String comment = result.getComment();

            // Create assertion metadata cells
            amendmentsRow.createCell(0).setCellValue(recordId);
            amendmentsRow.createCell(1).setCellValue(test);
            amendmentsRow.createCell(2).setCellValue(status);
            amendmentsRow.createCell(3).setCellValue(comment);

            List<String> actedUpon = fieldsFromAmendmentContext(amendment.getEnhancement());

            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);

                Cell cell = amendmentsRow.createCell(i+4);

                if (amendedValues.keySet().contains(field)) {
                    cell.setCellValue("was: " + (!prevValues.get(field).isEmpty() ? prevValues.get(field)  : "EMPTY ") +
                            " changed to: " + amendedValues.get(field));
                    cell.setCellStyle(styles.get(status));

                    ValidationState validationState = finalValues.get(field);

                    validationState.setValue(amendedValues.get(field));
                    validationState.setStatus(status);
                } else if (actedUpon.contains(field)) {
                    cell.setCellValue(prevValues.get(field));
                    cell.setCellStyle(styles.get(status));
                }
            }
        }

        // Summarize all amendments for this record on the final values sheet
        Row finalValuesRow = finalValuesSheet.createRow(finalValuesSheetRowNum++);

        int colNum = 0;
        for (ValidationState validationState : finalValues.values()) {
            Cell finalValuesCell = finalValuesRow.createCell(colNum);

            finalValuesCell.setCellValue(validationState.getValue());
            finalValuesCell.setCellStyle(styles.get(validationState.getStatus()));

            colNum++;
        }

        // Last column for flags
        // Not compliant or not complete styled with red background
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell flagSummary = finalValuesRow.createCell(colNum);
        flagSummary.setCellValue(allFlags.toString());
        flagSummary.setCellStyle(style);

        // empty row between blocks of measures
        amendmentsSheetRowNum++;
    }

    private List<String> fieldsFromMeasureContext(Measure context) {
        List<String> fields = new ArrayList<>();

        List<URI> ie = context.getInformationElements().getComposedOf();
        for (URI uri : ie) {
            String path = uri.getPath();
            String field = path.substring(path.lastIndexOf('/') + 1);

            fields.add(field);
        }

        return fields;
    }

    private List<String> fieldsFromValidationContext(Validation context) {
        List<String> fields = new ArrayList<>();

        List<URI> ie = context.getInformationElements().getComposedOf();
        for (URI uri : ie) {
            String path = uri.getPath();
            String field = path.substring(path.lastIndexOf('/') + 1);

            fields.add(field);
        }

        return fields;
    }

    private List<String> fieldsFromAmendmentContext(Amendment context) {
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
                // Measurement value, use status RUN_HAS_RESULT
                status = state.getLabel();
            }
        } else if (state.equals(ResultState.AMENDED) ||
                state.equals(ResultState.FILLED_IN) ||
                state.equals(ResultState.TRANSPOSED)){
            // Use state as status, one of the amended states: AMENDED, FILLED_IN, TRANSPOSED
            status = state.getLabel();
        } else if (state.equals(ResultState.NOT_AMENDED) || 
        		state.equals(ResultState.AMBIGUOUS)) { 
        	status = state.getLabel();
        } else if (state.equals(ResultState.INTERNAL_PREREQUISITES_NOT_MET) ||
                state.equals(ResultState.EXTERNAL_PREREQUISITES_NOT_MET) ) {
            // Use state as status, one of the error conditions: INTERNAL_PREREQUISITES_NOT_MET, EXTERNAL_PREREQUISITES_NOT_MET.
            status = state.getLabel();
        }

        return status;
    }


    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException if any.
     */
    public static void main(String[] args) throws IOException {
        FFDQModel model = new FFDQModel();
        model.load(new FileInputStream("/home/lowery/Downloads/dq_report.rdf"), RDFFormat.TURTLE);

        XLSXPostProcessor postProcessor = new XLSXPostProcessor(model);
        postProcessor.postprocess(new FileOutputStream("tempsxssf.xlsx"));
    }
}
