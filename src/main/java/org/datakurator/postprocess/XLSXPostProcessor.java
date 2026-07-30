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

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.datakurator.ffdq.model.*;
import org.datakurator.ffdq.model.context.Amendment;
import org.datakurator.ffdq.model.context.Issue;
import org.datakurator.ffdq.model.context.Measure;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.report.*;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Post-processes an {@link FFDQModel} that contains assertion responses and
 * writes the results to an XLSX workbook.  The workbook contains the following
 * sheets:
 * <ul>
 *   <li><b>Summary</b> – human-readable description of the report structure.</li>
 *   <li><b>Initial Values</b> – the original field values for each record, with
 *       colour-coded cells indicating per-field validation status.</li>
 *   <li><b>Final Values</b> – field values after applying amendments, with
 *       colour-coded cells and a trailing "Flags" column.</li>
 *   <li><b>Measures</b> – one row per measurement assertion.</li>
 *   <li><b>Validations</b> – one row per validation assertion.</li>
 *   <li><b>Amendments</b> – one row per amendment assertion.</li>
 *   <li><b>Issues</b> – one row per issue assertion (if any are present).</li>
 * </ul>
 *
 * <p>Usage via {@code test-runner}:</p>
 * <pre>
 *   # 1. Run tests and produce an RDF report:
 *   ./test-runner.sh -in data.tsv -informat tsv -out report.ttl -rdf tests.rdf -cls com.example.MyDQClass
 *
 *   # 2. Convert the RDF report to XLSX:
 *   java -cp target/kurator-ffdq-*.jar org.datakurator.postprocess.XLSXPostProcessor report.ttl output.xlsx
 * </pre>
 *
 * @author lowery (original), mole
 * @version $Id: $Id
 */
public class XLSXPostProcessor {

    private static final Logger logger = Logger.getLogger(XLSXPostProcessor.class.getName());

    private final FFDQModel model;

    private SXSSFWorkbook workbook;
    private final Map<String, CellStyle> styles = new HashMap<>();

    /** Ordered list of field names, derived from the first data resource and sorted for determinism. */
    private List<String> fields;

    private int measuresSheetRowNum = 1;
    private int validationsSheetRowNum = 1;
    private int amendmentsSheetRowNum = 1;
    private int issuesSheetRowNum = 1;

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
     * Initialises all cell styles used for colour-coded assertion results.
     * Every key that may later be passed to {@link #safeStyle(String)} must
     * be registered here.
     *
     * @param wb the workbook in which to create the styles.
     */
    public void initStyles(SXSSFWorkbook wb) {
        // White font used for coloured background cells
        Font font = wb.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());

        // COMPLIANT / COMPLETE – green background
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        styles.put("COMPLIANT", style);
        styles.put("COMPLETE", style);

        // NOT_COMPLIANT / NOT_COMPLETE – red background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        styles.put("NOT_COMPLIANT", style);
        styles.put("NOT_COMPLETE", style);

        // FILLED_IN / CURATED / TRANSPOSED / AMENDED – dark-yellow background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        styles.put("FILLED_IN", style);
        styles.put("CURATED", style);
        styles.put("TRANSPOSED", style);
        styles.put("AMENDED", style);

        // INTERNAL_PREREQUISITES_NOT_MET / EXTERNAL_PREREQUISITES_NOT_MET /
        // UNABLE_CURATE / NOT_AMENDED / AMBIGUOUS – grey background
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        styles.put("INTERNAL_PREREQUISITES_NOT_MET", style);
        styles.put("EXTERNAL_PREREQUISITES_NOT_MET", style);
        // UNABLE_CURATE is a legacy/alternate label used in initial-values coloring
        styles.put("UNABLE_CURATE", style);
        styles.put("NOT_AMENDED", style);
        styles.put("AMBIGUOUS", style);

        // NO_CHANGE – plain (no fill)
        style = wb.createCellStyle();
        styles.put("NO_CHANGE", style);

        // POTENTIAL_ISSUE / IS_ISSUE / NOT_ISSUE (Issue responses) – orange background
        Font blackFont = wb.createFont();
        blackFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(blackFont);
        styles.put("POTENTIAL_ISSUE", style);
        styles.put("IS_ISSUE", style);

        style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(blackFont);
        styles.put("NOT_ISSUE", style);
    }

    /**
     * Returns the style registered for {@code key}, or {@code null} if the key is
     * unknown.  Using {@code null} with {@link Cell#setCellStyle(CellStyle)} is
     * safe in Apache POI – it resets the cell to the default style.
     *
     * @param key the status/value string (e.g. {@code "COMPLIANT"}).
     * @return the registered {@link CellStyle}, or {@code null}.
     */
    CellStyle safeStyle(String key) {
        if (key == null) {
            return null;
        }
        return styles.get(key);
    }

    /**
     * Processes the loaded model and writes the XLSX workbook to {@code out}.
     *
     * @param out the output stream to write to; closed on completion.
     * @throws IllegalStateException if the model contains no data resources.
     * @throws IOException           if writing fails.
     */
    public void postprocess(OutputStream out) throws IOException {
        // Create the workbook and initialize cell styles
        workbook = new SXSSFWorkbook();
        initStyles(workbook);

        // Summary sheet containing descriptive text
        String summaryText = "The sheet labeled \"Final Values\" contains data including any changes " +
                "made as part of running the workflow. The sheet labeled \"Initial Values\" contains the original data " +
                "supplied as input to the workflow.\n" +
                "\n" +
                "The \"Validations\" sheet gives a summary for each of the validation tests performed. The \"Amendments\" " +
                "sheet summarizes any changes made to the records. In both sheets rows indicating the test results are " +
                "grouped by record and separated by spaces.\n" +
                "\n" +
                "The \"Measures\" sheet contains the value of any measurements performed (i.e. precision, completeness).\n" +
                "\n" +
                "The \"Issues\" sheet contains the results of any issue tests performed.";

        createSummarySheet(summaryText);

        // Get all data resources (initial values) associated with assertions in the report
        List<DataResource> dataResources = model.findDataResources();

        if (dataResources == null || dataResources.isEmpty()) {
            throw new IllegalStateException(
                    "No data resources found in the model. The report contains no records to process.");
        }

        // Derive a deterministic, sorted field list from the first data resource
        fields = new ArrayList<>(dataResources.get(0).asMap().keySet());
        Collections.sort(fields);

        // Create sheets
        Sheet initialValuesSheet = createInitialValuesSheet(dataResources);
        Sheet finalValuesSheet   = createFinalValuesSheet(dataResources);
        Sheet measuresSheet      = createMeasuresSheet(fields);
        Sheet validationsSheet   = createValidationsSheet(fields);
        Sheet amendmentsSheet    = createAmendmentsSheet(fields);
        Sheet issuesSheet        = createIssuesSheet(fields);

        for (DataResource dataResource : dataResources) {
            List<Response> measures    = model.findAssertionsForDataResource(dataResource, MeasureResponse.class);
            List<Response> validations = model.findAssertionsForDataResource(dataResource, ValidationResponse.class);
            List<Response> amendments  = model.findAssertionsForDataResource(dataResource, AmendmentResponse.class);
            List<Response> issues      = model.findAssertionsForDataResource(dataResource, IssueResponse.class);

            initMeasuresSheet(measuresSheet, measures, dataResource);
            initValidationsSheet(validationsSheet, initialValuesSheet, validations, dataResource);
            initAmendmentsSheet(amendmentsSheet, finalValuesSheet, amendments, dataResource);
            initIssuesSheet(issuesSheet, issues, dataResource);
        }

        try {
            workbook.write(out);
        } finally {
            try { out.close(); } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error closing output stream", ioe);
            }
            // Dispose temporary files backing this workbook on disk
            workbook.dispose();
        }
    }

    private Sheet createFinalValuesSheet(List<DataResource> dataResources) {
        SXSSFSheet finalValuesSheet = (SXSSFSheet) workbook.createSheet("Final Values");

        Row headerRow = finalValuesSheet.createRow(0);
        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i).setCellValue(fields.get(i));
        }
        headerRow.createCell(fields.size()).setCellValue("Flags");

        return finalValuesSheet;
    }

    private void createSummarySheet(String summaryText) {
        SXSSFSheet summarySheet = (SXSSFSheet) workbook.createSheet("Summary");

        CellStyle summaryStyle = workbook.createCellStyle();
        summaryStyle.setWrapText(true);

        Row summaryRow = summarySheet.createRow(1);
        Cell summaryCell = summaryRow.createCell(1);
        summaryCell.setCellValue(summaryText);
        summaryCell.setCellStyle(summaryStyle);

        summarySheet.addMergedRegion(new CellRangeAddress(1, 7, 1, 9));
    }

    private SXSSFSheet createInitialValuesSheet(List<DataResource> dataResources) {
        SXSSFSheet initialValuesSheet = (SXSSFSheet) workbook.createSheet("Initial Values");

        Row headerRow = initialValuesSheet.createRow(0);
        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i).setCellValue(fields.get(i));
        }

        return initialValuesSheet;
    }

    private SXSSFSheet createMeasuresSheet(List<String> fields) {
        SXSSFSheet measuresSheet = (SXSSFSheet) workbook.createSheet("Measures");

        Row headerRow = measuresSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Value");
        headerRow.createCell(4).setCellValue("Comment");

        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i + 5).setCellValue(fields.get(i));
        }

        return measuresSheet;
    }

    private void initMeasuresSheet(Sheet measuresSheet, List<Response> measures, DataResource dataResource) {
        for (Response response : measures) {
            MeasureResponse measure = (MeasureResponse) response;
            String recordId = dataResource.getRecordId();

            Row measuresRow = measuresSheet.createRow(measuresSheetRowNum++);

            Specification specification = measure.getSpecification();
            String test = specification != null ? specification.getLabel() : "";

            Result result = measure.getResult();
            ResultState state  = result != null ? result.getState()  : null;
            Entity    entity   = result != null ? result.getEntity() : null;

            String value = "";
            if (entity != null && entity.getValue() != null) {
                value = entity.getValue().toString();
            }

            String status = state != null ? state.getLabel() : "";
            String comment = result != null ? result.getComment() : "";

            measuresRow.createCell(0).setCellValue(recordId);
            measuresRow.createCell(1).setCellValue(test);
            measuresRow.createCell(2).setCellValue(status);
            measuresRow.createCell(3).setCellValue(value);
            measuresRow.createCell(4).setCellValue(comment != null ? comment : "");

            Map<String, String> values    = dataResource.asMap();
            List<String>        actedUpon = fieldsFromMeasureContext(measure.getDimension());

            for (int i = 0; i < fields.size(); i++) {
                Cell cell = measuresRow.createCell(i + 5);
                cell.setCellValue(nullToEmpty(values.get(fields.get(i))));

                if (actedUpon.contains(fields.get(i)) &&
                        (value.equals("COMPLETE") || value.equals("NOT_COMPLETE"))) {
                    CellStyle cs = safeStyle(value);
                    if (cs != null) {
                        cell.setCellStyle(cs);
                    }
                }
            }
        }

        // Blank spacer row between records
        measuresSheetRowNum++;
    }

    private SXSSFSheet createValidationsSheet(List<String> fields) {
        SXSSFSheet validationsSheet = (SXSSFSheet) workbook.createSheet("Validations");

        Row headerRow = validationsSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Value");
        headerRow.createCell(4).setCellValue("Comment");

        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i + 5).setCellValue(fields.get(i));
        }

        return validationsSheet;
    }

    private void initValidationsSheet(Sheet validationsSheet, Sheet initialValuesSheet,
            List<Response> validations, DataResource dataResource) {

        List<Map<String, ValidationState>> allInitialValues = new ArrayList<>();
        List<String> allFlags = new ArrayList<>();

        for (Response response : validations) {
            Map<String, ValidationState> initialValues = new LinkedHashMap<>();

            ValidationResponse validation = (ValidationResponse) response;
            String recordId = dataResource.getRecordId();

            Row validationsRow = validationsSheet.createRow(validationsSheetRowNum++);

            Specification specification = validation.getSpecification();
            String test = specification != null ? specification.getLabel() : "";

            Result result = validation.getResult();
            ResultState state  = result != null ? result.getState()  : null;
            Entity    entity   = result != null ? result.getEntity() : null;

            String assertionValue = "";
            if (entity != null && entity.getValue() != null) {
                assertionValue = entity.getValue().toString();
            }

            String assertionStatus = state != null ? state.getLabel() : "";
            String comment = result != null ? result.getComment() : "";

            if (assertionValue.equals("NOT_COMPLIANT")) {
                allFlags.add(test + "_" + assertionValue);
            }

            validationsRow.createCell(0).setCellValue(recordId);
            validationsRow.createCell(1).setCellValue(test);
            validationsRow.createCell(2).setCellValue(assertionStatus);
            validationsRow.createCell(3).setCellValue(assertionValue);
            validationsRow.createCell(4).setCellValue(comment != null ? comment : "");

            Map<String, String> values    = dataResource.asMap();
            List<String>        actedUpon = fieldsFromValidationContext(validation.getCriterion());

            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                String value = nullToEmpty(values.get(field));

                if (!initialValues.containsKey(field)) {
                    initialValues.put(field, new ValidationState());
                }

                Cell cell = validationsRow.createCell(i + 5);
                cell.setCellValue(value);

                ValidationState initialValue = initialValues.get(field);
                initialValue.setValue(value);

                if (actedUpon.contains(field)) {
                    String styleKey;
                    if (assertionValue.equals("COMPLIANT") || assertionValue.equals("NOT_COMPLIANT")) {
                        styleKey = assertionValue;
                    } else {
                        styleKey = assertionStatus;
                    }
                    initialValue.setStatus(styleKey);
                    CellStyle cs = safeStyle(styleKey);
                    if (cs != null) {
                        cell.setCellStyle(cs);
                    }
                }
            }

            allInitialValues.add(initialValues);
        }

        // Write one summary row per record to the Initial Values sheet
        Row initialValuesRow = initialValuesSheet.createRow(initialValuesSheetRowNum++);

        int colNum = 0;
        for (String field : fields) {
            boolean validFlag             = false;
            boolean failureFlag           = false;
            boolean prerequisitesNotMetFlag = false;

            for (Map<String, ValidationState> initialValues : allInitialValues) {
                ValidationState initialValue = initialValues.get(field);
                if (initialValue != null && initialValue.getStatus() != null) {
                    switch (initialValue.getStatus()) {
                        case "COMPLIANT":
                            validFlag = true;
                            break;
                        case "NOT_COMPLIANT":
                            failureFlag = true;
                            break;
                        case "UNABLE_CURATE":
                        case "INTERNAL_PREREQUISITES_NOT_MET":
                        case "EXTERNAL_PREREQUISITES_NOT_MET":
                            prerequisitesNotMetFlag = true;
                            break;
                        default:
                            break;
                    }
                }
            }

            String value = nullToEmpty(dataResource.asMap().get(field));
            Cell initialValuesCell = initialValuesRow.createCell(colNum);
            initialValuesCell.setCellValue(value);

            if (!value.isEmpty()) {
                if (prerequisitesNotMetFlag) {
                    CellStyle cs = safeStyle("UNABLE_CURATE");
                    if (cs != null) {
                        initialValuesCell.setCellStyle(cs);
                    }
                }
                if (validFlag) {
                    CellStyle cs = safeStyle("COMPLIANT");
                    if (cs != null) {
                        initialValuesCell.setCellStyle(cs);
                    }
                }
                if (failureFlag) {
                    CellStyle cs = safeStyle("NOT_COMPLIANT");
                    if (cs != null) {
                        initialValuesCell.setCellStyle(cs);
                    }
                }
            }

            colNum++;
        }

        // Flags cell at end of row (yellow background)
        CellStyle flagStyle = workbook.createCellStyle();
        flagStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        flagStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell flagsCell = initialValuesRow.createCell(colNum);
        flagsCell.setCellValue(allFlags.toString());
        flagsCell.setCellStyle(flagStyle);

        // Spacer row
        validationsSheetRowNum++;
    }

    private SXSSFSheet createAmendmentsSheet(List<String> fields) {
        SXSSFSheet amendmentsSheet = (SXSSFSheet) workbook.createSheet("Amendments");

        Row headerRow = amendmentsSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Comment");

        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i + 4).setCellValue(fields.get(i));
        }

        return amendmentsSheet;
    }

    private void initAmendmentsSheet(Sheet amendmentsSheet, Sheet finalValuesSheet,
            List<Response> amendments, DataResource dataResource) {

        // finalValues preserves the insertion order of `fields` for deterministic column output
        Map<String, ValidationState> finalValues = new LinkedHashMap<>();
        Map<String, String> prevValues = dataResource.asMap();

        for (String field : fields) {
            ValidationState vs = new ValidationState();
            vs.setValue(nullToEmpty(prevValues.get(field)));
            finalValues.put(field, vs);
        }

        List<String> allFlags = new ArrayList<>();

        for (Response response : amendments) {
            AmendmentResponse amendment = (AmendmentResponse) response;
            String recordId = dataResource.getRecordId();

            Row amendmentsRow = amendmentsSheet.createRow(amendmentsSheetRowNum++);

            Specification specification = amendment.getSpecification();
            String test = specification != null ? specification.getLabel() : "";

            Result result = amendment.getResult();
            ResultState state  = result != null ? result.getState()  : null;
            Entity    entity   = result != null ? result.getEntity() : null;

            Map<String, String> amendedValues = new HashMap<>();
            if (entity != null && entity.getValue() instanceof URI) {
                URI uri = (URI) entity.getValue();
                try {
                    DataResource amended = model.findDataResource(uri);
                    if (amended != null) {
                        amendedValues = amended.asMap();
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING,
                            "Could not retrieve amended data resource for URI: " + uri, e);
                }
            }

            String status  = state  != null ? state.getLabel()  : "";
            String comment = result != null ? result.getComment() : "";

            if (!status.equals("NO_CHANGE")) {
                allFlags.add(test + "_" + status);
            }

            amendmentsRow.createCell(0).setCellValue(recordId);
            amendmentsRow.createCell(1).setCellValue(test);
            amendmentsRow.createCell(2).setCellValue(status);
            amendmentsRow.createCell(3).setCellValue(comment != null ? comment : "");

            List<String> actedUpon = fieldsFromAmendmentContext(amendment.getEnhancement());

            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                Cell cell = amendmentsRow.createCell(i + 4);

                if (amendedValues.containsKey(field)) {
                    String prev = nullToEmpty(prevValues.get(field));
                    cell.setCellValue("was: " + (prev.isEmpty() ? "EMPTY" : prev) +
                            " changed to: " + nullToEmpty(amendedValues.get(field)));
                    CellStyle cs = safeStyle(status);
                    if (cs != null) {
                        cell.setCellStyle(cs);
                    }

                    ValidationState vs = finalValues.get(field);
                    if (vs != null) {
                        vs.setValue(nullToEmpty(amendedValues.get(field)));
                        vs.setStatus(status);
                    }
                } else if (actedUpon.contains(field)) {
                    cell.setCellValue(nullToEmpty(prevValues.get(field)));
                    CellStyle cs = safeStyle(status);
                    if (cs != null) {
                        cell.setCellStyle(cs);
                    }
                }
            }
        }

        // Summarise all amendments for this record on the Final Values sheet
        Row finalValuesRow = finalValuesSheet.createRow(finalValuesSheetRowNum++);

        int colNum = 0;
        for (String field : fields) {
            ValidationState vs = finalValues.get(field);
            Cell finalValuesCell = finalValuesRow.createCell(colNum);
            if (vs != null) {
                finalValuesCell.setCellValue(nullToEmpty(vs.getValue()));
                CellStyle cs = safeStyle(vs.getStatus());
                if (cs != null) {
                    finalValuesCell.setCellStyle(cs);
                }
            }
            colNum++;
        }

        // Flags column
        CellStyle flagStyle = workbook.createCellStyle();
        flagStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        flagStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell flagSummary = finalValuesRow.createCell(colNum);
        flagSummary.setCellValue(allFlags.toString());
        flagSummary.setCellStyle(flagStyle);

        // Spacer row
        amendmentsSheetRowNum++;
    }

    private SXSSFSheet createIssuesSheet(List<String> fields) {
        SXSSFSheet issuesSheet = (SXSSFSheet) workbook.createSheet("Issues");

        Row headerRow = issuesSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Record Id");
        headerRow.createCell(1).setCellValue("Test Name");
        headerRow.createCell(2).setCellValue("Status");
        headerRow.createCell(3).setCellValue("Value");
        headerRow.createCell(4).setCellValue("Comment");

        for (int i = 0; i < fields.size(); i++) {
            headerRow.createCell(i + 5).setCellValue(fields.get(i));
        }

        return issuesSheet;
    }

    private void initIssuesSheet(Sheet issuesSheet, List<Response> issues, DataResource dataResource) {
        for (Response response : issues) {
            IssueResponse issue = (IssueResponse) response;
            String recordId = dataResource.getRecordId();

            Row issuesRow = issuesSheet.createRow(issuesSheetRowNum++);

            Specification specification = issue.getSpecification();
            String test = specification != null ? specification.getLabel() : "";

            Result result = issue.getResult();
            ResultState state  = result != null ? result.getState()  : null;
            Entity    entity   = result != null ? result.getEntity() : null;

            String value = "";
            if (entity != null && entity.getValue() != null) {
                value = entity.getValue().toString();
            }

            String status  = state  != null ? state.getLabel()  : "";
            String comment = result != null ? result.getComment() : "";

            issuesRow.createCell(0).setCellValue(recordId);
            issuesRow.createCell(1).setCellValue(test);
            issuesRow.createCell(2).setCellValue(status);
            issuesRow.createCell(3).setCellValue(value);
            issuesRow.createCell(4).setCellValue(comment != null ? comment : "");

            Map<String, String> values = dataResource.asMap();
            List<String> actedUpon = fieldsFromIssueContext(issue.getIssueInContext());

            for (int i = 0; i < fields.size(); i++) {
                Cell cell = issuesRow.createCell(i + 5);
                cell.setCellValue(nullToEmpty(values.get(fields.get(i))));

                if (actedUpon.contains(fields.get(i))) {
                    String styleKey = (value.equals("IS_ISSUE") || value.equals("NOT_ISSUE") ||
                                       value.equals("POTENTIAL_ISSUE")) ? value : status;
                    CellStyle cs = safeStyle(styleKey);
                    if (cs != null) {
                        cell.setCellStyle(cs);
                    }
                }
            }
        }

        if (!issues.isEmpty()) {
            issuesSheetRowNum++;
        }
    }

    // -------------------------------------------------------------------------
    // Context field-extraction helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the list of DWC field names acted upon by a Measure context.
     *
     * @param context the {@link Measure} context; may be {@code null}.
     * @return non-null list of field names (may be empty).
     */
    List<String> fieldsFromMeasureContext(Measure context) {
        List<String> result = new ArrayList<>();
        if (context == null || context.getInformationElements() == null) {
            return result;
        }
        for (URI uri : context.getInformationElements().getComposedOf()) {
            result.add(localNameFromUri(uri));
        }
        return result;
    }

    /**
     * Returns the list of DWC field names acted upon by a Validation context.
     *
     * @param context the {@link Validation} context; may be {@code null}.
     * @return non-null list of field names (may be empty).
     */
    List<String> fieldsFromValidationContext(Validation context) {
        List<String> result = new ArrayList<>();
        if (context == null || context.getInformationElements() == null) {
            return result;
        }
        for (URI uri : context.getInformationElements().getComposedOf()) {
            result.add(localNameFromUri(uri));
        }
        return result;
    }

    /**
     * Returns the list of DWC field names acted upon by an Amendment context.
     *
     * @param context the {@link Amendment} context; may be {@code null}.
     * @return non-null list of field names (may be empty).
     */
    List<String> fieldsFromAmendmentContext(Amendment context) {
        List<String> result = new ArrayList<>();
        if (context == null || context.getInformationElements() == null) {
            return result;
        }
        for (URI uri : context.getInformationElements().getComposedOf()) {
            result.add(localNameFromUri(uri));
        }
        return result;
    }

    /**
     * Returns the list of DWC field names acted upon by an Issue context.
     *
     * @param context the {@link Issue} context; may be {@code null}.
     * @return non-null list of field names (may be empty).
     */
    List<String> fieldsFromIssueContext(Issue context) {
        List<String> result = new ArrayList<>();
        if (context == null || context.getInformationElements() == null) {
            return result;
        }
        for (URI uri : context.getInformationElements().getComposedOf()) {
            result.add(localNameFromUri(uri));
        }
        return result;
    }

    /**
     * Determines the spreadsheet row status string from a result state and entity value.
     * This maps the FFDQ response model onto the style keys used by {@link #initStyles}.
     *
     * @param state the {@link ResultState}; must not be {@code null}.
     * @param value the {@link Entity} holding the result value; may be {@code null}.
     * @return a non-null status string (may be empty if unrecognised).
     */
    String determineRowStatus(ResultState state, Entity value) {
        if (state == null) {
            return "";
        }
        if (state.equals(ResultState.RUN_HAS_RESULT)) {
            if (value != null && value.getValue() instanceof String) {
                return (String) value.getValue();
            } else if (value != null &&
                    (value.getValue() instanceof Long || value.getValue() instanceof Integer)) {
                return state.getLabel();
            }
        } else if (state.equals(ResultState.AMENDED)
                || state.equals(ResultState.FILLED_IN)
                || state.equals(ResultState.TRANSPOSED)
                || state.equals(ResultState.NOT_AMENDED)
                || state.equals(ResultState.AMBIGUOUS)) {
            return state.getLabel();
        } else if (state.equals(ResultState.INTERNAL_PREREQUISITES_NOT_MET)
                || state.equals(ResultState.EXTERNAL_PREREQUISITES_NOT_MET)) {
            return state.getLabel();
        }
        return state.getLabel();
    }

    // -------------------------------------------------------------------------
    // Private utilities
    // -------------------------------------------------------------------------

    private static String localNameFromUri(URI uri) {
        if (uri == null) {
            return "";
        }
        String path = uri.getPath();
        if (path != null && path.contains("/")) {
            return path.substring(path.lastIndexOf('/') + 1);
        }
        return uri.toString();
    }

    /** Returns {@code ""} if {@code s} is {@code null}, otherwise {@code s}. */
    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    /**
     * Command-line entry point.  Accepts two arguments: the path to the Turtle
     * RDF report and the path for the output XLSX file.
     *
     * <pre>
     *   java -cp kurator-ffdq.jar org.datakurator.postprocess.XLSXPostProcessor report.ttl output.xlsx
     * </pre>
     *
     * @param args {@code [rdfReportPath, xlsxOutputPath]}
     * @throws IOException if reading or writing fails.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: XLSXPostProcessor <rdf-report.ttl> <output.xlsx>");
            System.exit(1);
        }

        String rdfPath  = args[0];
        String xlsxPath = args[1];

        FFDQModel model = new FFDQModel();
        model.load(new FileInputStream(rdfPath), RDFFormat.TURTLE);

        XLSXPostProcessor postProcessor = new XLSXPostProcessor(model);
        postProcessor.postprocess(new FileOutputStream(xlsxPath));
        logger.log(Level.INFO, "XLSX report written to: " + xlsxPath);
    }
}
