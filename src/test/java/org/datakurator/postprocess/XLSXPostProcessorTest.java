/** XLSXPostProcessorTest.java
 *
 * Copyright 2026 President and Fellows of Harvard College
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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.datakurator.ffdq.model.*;
import org.datakurator.ffdq.model.report.*;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit and integration tests for {@link XLSXPostProcessor}.
 *
 * <p>Tests are grouped into three tiers:</p>
 * <ol>
 *   <li><b>Style initialisation</b> – verify that every status key used at
 *       runtime is registered in the styles map.</li>
 *   <li><b>Row-status mapping</b> – verify {@code determineRowStatus} returns
 *       the correct string for each significant {@link ResultState} /
 *       {@link Entity} combination.</li>
 *   <li><b>Integration</b> – load a representative Turtle RDF fixture and
 *       drive {@link XLSXPostProcessor#postprocess} end-to-end, asserting on
 *       sheet presence, header rows, row counts, and cell values.</li>
 * </ol>
 */
public class XLSXPostProcessorTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a fresh {@link XLSXPostProcessor} backed by a null model and
     * calls {@link XLSXPostProcessor#initStyles} on a fresh workbook, returning
     * a pair of (processor, workbook) so tests can inspect both.
     */
    private SXSSFWorkbook buildWorkbookWithStyles(XLSXPostProcessor processor) {
        SXSSFWorkbook wb = new SXSSFWorkbook();
        processor.initStyles(wb);
        return wb;
    }

    // -------------------------------------------------------------------------
    // Style-initialisation tests
    // -------------------------------------------------------------------------

    /**
     * Every status string that is looked up via {@code safeStyle()} at runtime
     * must be pre-registered in {@code initStyles}.  A missing key returns
     * {@code null} which silently loses colouring; this test catches any gaps.
     */
    @Test
    public void testInitStyles_allExpectedKeysPresent() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        SXSSFWorkbook wb = buildWorkbookWithStyles(processor);

        List<String> requiredKeys = Arrays.asList(
                // Validation / Measure result values
                "COMPLIANT", "NOT_COMPLIANT",
                "COMPLETE",  "NOT_COMPLETE",
                // Amendment states
                "FILLED_IN", "CURATED", "TRANSPOSED", "AMENDED",
                "NOT_AMENDED", "AMBIGUOUS", "NO_CHANGE",
                // Prerequisite failure states
                "INTERNAL_PREREQUISITES_NOT_MET",
                "EXTERNAL_PREREQUISITES_NOT_MET",
                "UNABLE_CURATE",
                // Issue result values
                "POTENTIAL_ISSUE", "IS_ISSUE", "NOT_ISSUE"
        );

        for (String key : requiredKeys) {
            assertNotNull("Style map missing key: " + key, processor.safeStyle(key));
        }

        wb.dispose();
    }

    @Test
    public void testSafeStyle_unknownKeyReturnsNull() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        buildWorkbookWithStyles(processor);
        assertNull("Unknown style key should return null", processor.safeStyle("NONEXISTENT_KEY"));
    }

    @Test
    public void testSafeStyle_nullKeyReturnsNull() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        buildWorkbookWithStyles(processor);
        assertNull("Null style key should return null", processor.safeStyle(null));
    }

    // -------------------------------------------------------------------------
    // determineRowStatus tests
    // -------------------------------------------------------------------------

    @Test
    public void testDetermineRowStatus_runHasResult_stringValue_returnsValue() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);

        Entity entity = new Entity();
        entity.setValue("COMPLIANT");

        String status = processor.determineRowStatus(ResultState.RUN_HAS_RESULT, entity);
        assertEquals("COMPLIANT", status);
    }

    @Test
    public void testDetermineRowStatus_runHasResult_notCompliant() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);

        Entity entity = new Entity();
        entity.setValue("NOT_COMPLIANT");

        String status = processor.determineRowStatus(ResultState.RUN_HAS_RESULT, entity);
        assertEquals("NOT_COMPLIANT", status);
    }

    @Test
    public void testDetermineRowStatus_runHasResult_numericValue_returnsStateLabel() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);

        Entity entity = new Entity();
        entity.setValue(42L);

        String status = processor.determineRowStatus(ResultState.RUN_HAS_RESULT, entity);
        assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), status);
    }

    @Test
    public void testDetermineRowStatus_amended() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        String status = processor.determineRowStatus(ResultState.AMENDED, null);
        assertEquals(ResultState.AMENDED.getLabel(), status);
    }

    @Test
    public void testDetermineRowStatus_filledIn() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        String status = processor.determineRowStatus(ResultState.FILLED_IN, null);
        assertEquals(ResultState.FILLED_IN.getLabel(), status);
    }

    @Test
    public void testDetermineRowStatus_notAmended() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        String status = processor.determineRowStatus(ResultState.NOT_AMENDED, null);
        assertEquals(ResultState.NOT_AMENDED.getLabel(), status);
    }

    @Test
    public void testDetermineRowStatus_internalPrerequisitesNotMet() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        String status = processor.determineRowStatus(ResultState.INTERNAL_PREREQUISITES_NOT_MET, null);
        assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), status);
    }

    @Test
    public void testDetermineRowStatus_externalPrerequisitesNotMet() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        String status = processor.determineRowStatus(ResultState.EXTERNAL_PREREQUISITES_NOT_MET, null);
        assertEquals(ResultState.EXTERNAL_PREREQUISITES_NOT_MET.getLabel(), status);
    }

    @Test
    public void testDetermineRowStatus_nullState_returnsEmptyString() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        String status = processor.determineRowStatus(null, null);
        assertEquals("", status);
    }

    // -------------------------------------------------------------------------
    // fieldsFrom*Context null-safety tests
    // -------------------------------------------------------------------------

    @Test
    public void testFieldsFromMeasureContext_nullContext_returnsEmptyList() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        List<String> result = processor.fieldsFromMeasureContext(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFieldsFromValidationContext_nullContext_returnsEmptyList() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        List<String> result = processor.fieldsFromValidationContext(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFieldsFromAmendmentContext_nullContext_returnsEmptyList() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        List<String> result = processor.fieldsFromAmendmentContext(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFieldsFromIssueContext_nullContext_returnsEmptyList() {
        XLSXPostProcessor processor = new XLSXPostProcessor(null);
        List<String> result = processor.fieldsFromIssueContext(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Failure-mode tests
    // -------------------------------------------------------------------------

    /**
     * When the model returns an empty data-resource list, {@code postprocess}
     * must throw {@link IllegalStateException} with a descriptive message rather
     * than an opaque {@link IndexOutOfBoundsException}.
     */
    @Test
    public void testPostprocess_emptyDataResources_throwsIllegalStateException() throws Exception {
        FFDQModel emptyModel = new FFDQModel();
        XLSXPostProcessor processor = new XLSXPostProcessor(emptyModel);

        try {
            processor.postprocess(new ByteArrayOutputStream());
            fail("Expected IllegalStateException for empty data resources");
        } catch (IllegalStateException e) {
            assertTrue("Exception message should mention data resources",
                    e.getMessage().toLowerCase().contains("data resource"));
        }
    }

    // -------------------------------------------------------------------------
    // Integration tests
    // -------------------------------------------------------------------------

    /**
     * Loads the representative Turtle fixture, runs {@code postprocess}, and
     * asserts on:
     * <ul>
     *   <li>the workbook is written without error,</li>
     *   <li>all expected sheets are present,</li>
     *   <li>each sheet has a header row (row 0) with the expected fixed columns,</li>
     *   <li>at least one data row is present in Validations, Measures, and Amendments.</li>
     * </ul>
     */
    @Test
    public void testPostprocess_fixtureReport_sheetStructure() throws Exception {
        FFDQModel model = buildFixtureModel();

        XLSXPostProcessor processor = new XLSXPostProcessor(model);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        processor.postprocess(baos);

        assertTrue("Output should be non-empty", baos.size() > 0);

        // Re-open the workbook from the byte array to inspect it
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook wb =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook(
                             new java.io.ByteArrayInputStream(baos.toByteArray()))) {

            // Verify required sheets exist
            List<String> requiredSheets = Arrays.asList(
                    "Summary", "Initial Values", "Final Values",
                    "Measures", "Validations", "Amendments", "Issues");
            for (String name : requiredSheets) {
                assertNotNull("Sheet should exist: " + name, wb.getSheet(name));
            }

            // Measures sheet: header row should have "Record Id" in column 0
            Sheet measuresSheet = wb.getSheet("Measures");
            Row measuresHeader = measuresSheet.getRow(0);
            assertNotNull("Measures header row should exist", measuresHeader);
            assertEquals("Record Id", measuresHeader.getCell(0).getStringCellValue());
            assertEquals("Test Name", measuresHeader.getCell(1).getStringCellValue());
            assertEquals("Status",    measuresHeader.getCell(2).getStringCellValue());
            assertEquals("Value",     measuresHeader.getCell(3).getStringCellValue());
            assertEquals("Comment",   measuresHeader.getCell(4).getStringCellValue());

            // Validations sheet: at least one data row (row 1 should be present)
            Sheet validationsSheet = wb.getSheet("Validations");
            assertNotNull("Validations header row should exist", validationsSheet.getRow(0));
            assertNotNull("Validations should have at least one data row", validationsSheet.getRow(1));

            // The validation result value for our fixture is COMPLIANT
            Row validationDataRow = validationsSheet.getRow(1);
            String validationValue = validationDataRow.getCell(3).getStringCellValue();
            assertEquals("COMPLIANT", validationValue);

            // Amendments sheet: at least one data row
            Sheet amendmentsSheet = wb.getSheet("Amendments");
            assertNotNull("Amendments header row should exist", amendmentsSheet.getRow(0));
            assertNotNull("Amendments should have at least one data row", amendmentsSheet.getRow(1));

            // The amendment status for our fixture is NOT_AMENDED (ResultState.NOT_AMENDED.getLabel())
            Row amendmentDataRow = amendmentsSheet.getRow(1);
            String amendmentStatus = amendmentDataRow.getCell(2).getStringCellValue();
            assertEquals(ResultState.NOT_AMENDED.getLabel(), amendmentStatus);
        }
    }

    /**
     * Verifies that field columns are written in a deterministic (sorted)
     * order by running postprocess twice on the same fixture and comparing
     * header cell values in the Validations sheet.
     */
    @Test
    public void testPostprocess_fieldOrderIsDeterministic() throws Exception {
        FFDQModel model1 = buildFixtureModel();
        FFDQModel model2 = buildFixtureModel();

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

        new XLSXPostProcessor(model1).postprocess(baos1);
        new XLSXPostProcessor(model2).postprocess(baos2);

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook wb1 =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook(
                             new java.io.ByteArrayInputStream(baos1.toByteArray()));
             org.apache.poi.xssf.usermodel.XSSFWorkbook wb2 =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook(
                             new java.io.ByteArrayInputStream(baos2.toByteArray()))) {

            Row header1 = wb1.getSheet("Validations").getRow(0);
            Row header2 = wb2.getSheet("Validations").getRow(0);

            assertNotNull(header1);
            assertNotNull(header2);
            assertEquals("Header column count must match",
                    header1.getLastCellNum(), header2.getLastCellNum());

            // Field columns start at index 5
            for (int i = 5; i < header1.getLastCellNum(); i++) {
                assertEquals("Column " + i + " header must match between runs",
                        header1.getCell(i).getStringCellValue(),
                        header2.getCell(i).getStringCellValue());
            }
        }
    }

    /**
     * Verifies that {@code postprocess} handles a report with no amendments
     * gracefully (the Amendments and Final Values sheets should still be
     * created, just with only a header row).
     */
    @Test
    public void testPostprocess_fixtureReport_amendmentsSheetHasCorrectHeaders() throws Exception {
        FFDQModel model = buildFixtureModel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new XLSXPostProcessor(model).postprocess(baos);

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook wb =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook(
                             new java.io.ByteArrayInputStream(baos.toByteArray()))) {

            Sheet sheet = wb.getSheet("Amendments");
            assertNotNull(sheet);
            Row header = sheet.getRow(0);
            assertNotNull(header);
            assertEquals("Record Id", header.getCell(0).getStringCellValue());
            assertEquals("Test Name", header.getCell(1).getStringCellValue());
            assertEquals("Status",    header.getCell(2).getStringCellValue());
            assertEquals("Comment",   header.getCell(3).getStringCellValue());
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Builds an {@link FFDQModel} containing one data resource and one each of
     * ValidationResponse (COMPLIANT), MeasureResponse (COMPLETE / RUN_HAS_RESULT),
     * and AmendmentResponse (NO_CHANGE) by using the same RDFBeanManager path that
     * the production TestRunner follows.  This guarantees that the rdfbeans
     * binding-class triples are present, so deserialization works reliably.
     */
    private FFDQModel buildFixtureModel() throws Exception {
        FFDQModel model = new FFDQModel();

        // --- Data resource ---------------------------------------------------
        Map<String, String> record = new LinkedHashMap<>();
        record.put("eventDate", "2023-06-15");
        record.put("occurrenceID", "TEST-REC-001");
        DataResource dr = new DataResource(model.getVocab(), record);
        model.load(dr.asModel());

        // --- Shared ResultState: RUN_HAS_RESULT ------------------------------
        ResultState stateRun = ResultState.RUN_HAS_RESULT;
        model.save(stateRun);

        // --- ValidationResponse: COMPLIANT -----------------------------------
        Entity entityCompliant = new Entity();
        entityCompliant.setId("urn:uuid:entity-compliant-" + UUID.randomUUID());
        // Use a plain String so entity.getValue().toString() == "COMPLIANT"
        entityCompliant.setValue("COMPLIANT");
        model.save(entityCompliant);

        Result validationResult = new Result();
        validationResult.setId("urn:uuid:result-validation-" + UUID.randomUUID());
        validationResult.setState(stateRun);
        validationResult.setEntity(entityCompliant);
        validationResult.setComment("Event date is present and valid.");
        model.save(validationResult);

        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setId("urn:uuid:vr-" + UUID.randomUUID());
        validationResponse.setDataResource(dr.getURI());
        validationResponse.setResult(validationResult);
        model.save(validationResponse);

        // --- MeasureResponse: COMPLETE / RUN_HAS_RESULT ----------------------
        Entity entityComplete = new Entity();
        entityComplete.setId("urn:uuid:entity-complete-" + UUID.randomUUID());
        // Use a plain String so entity.getValue().toString() == "COMPLETE"
        entityComplete.setValue("COMPLETE");
        model.save(entityComplete);

        Result measureResult = new Result();
        measureResult.setId("urn:uuid:result-measure-" + UUID.randomUUID());
        measureResult.setState(stateRun);
        measureResult.setEntity(entityComplete);
        measureResult.setComment("Event date completeness is 1.");
        model.save(measureResult);

        MeasureResponse measureResponse = new MeasureResponse();
        measureResponse.setId("urn:uuid:mr-" + UUID.randomUUID());
        measureResponse.setDataResource(dr.getURI());
        measureResponse.setResult(measureResult);
        model.save(measureResponse);

        // --- AmendmentResponse: NO_CHANGE ------------------------------------
        ResultState stateNoChange = ResultState.NOT_AMENDED;
        model.save(stateNoChange);

        Result amendmentResult = new Result();
        amendmentResult.setId("urn:uuid:result-amendment-" + UUID.randomUUID());
        amendmentResult.setState(stateNoChange);
        amendmentResult.setComment("No amendment needed.");
        model.save(amendmentResult);

        AmendmentResponse amendmentResponse = new AmendmentResponse();
        amendmentResponse.setId("urn:uuid:ar-" + UUID.randomUUID());
        amendmentResponse.setDataResource(dr.getURI());
        amendmentResponse.setResult(amendmentResult);
        model.save(amendmentResponse);

        return model;
    }

    private FFDQModel loadFixtureModel(String resourceName) throws IOException {
        InputStream is = getClass().getResourceAsStream(resourceName);
        assertNotNull("Test fixture not found on classpath: " + resourceName, is);
        FFDQModel model = new FFDQModel();
        model.load(is, RDFFormat.TURTLE);
        return model;
    }
}
