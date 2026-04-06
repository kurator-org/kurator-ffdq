package org.datakurator.ffdq.util;

import org.datakurator.ffdq.model.BibliographicResource;
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.rdf.FFDQModel;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CitationUtils}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Correct parsing of a valid {@code <ul><li>…</li></ul>} string.</li>
 *   <li>Detection / graceful handling of malformed HTML list strings.</li>
 *   <li>Stable UUID mapping: same citation string gets the same UUID when the
 *       mapping map (or file) is reused.</li>
 *   <li>RDF graph output: {@code dcterms:references} triples from a Need to
 *       BibliographicResource nodes; {@code dcterms:bibliographicCitation} on
 *       those nodes; <em>no</em> {@code dcterms:bibliographicCitation} literal
 *       directly on the Need.</li>
 * </ol>
 */
public class CitationUtilsTest {

    // -----------------------------------------------------------------------
    // parseReferences – valid input
    // -----------------------------------------------------------------------

    @Test
    public void testParseReferences_validHtmlList() {
        String html = "<ul>"
                + "<li>ISO (n.dat.) ISO 3166 Country Codes. https://www.iso.org/</li> "
                + "<li>DataHub (2018) List of all countries. https://datahub.io/</li> "
                + "</ul>";
        List<String> result = CitationUtils.parseReferences(html);
        assertEquals(2, result.size());
        assertEquals("ISO (n.dat.) ISO 3166 Country Codes. https://www.iso.org/", result.get(0));
        assertEquals("DataHub (2018) List of all countries. https://datahub.io/", result.get(1));
    }

    @Test
    public void testParseReferences_fullExampleFromProblemStatement() {
        String html = "<ul>"
                + "<li>ISO (n.dat.) ISO 3166 Country Codes. https://www.iso.org/iso-3166-country-codes.html</li> "
                + "<li>ISO (n.dat) 3166-1 alpha-2. https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</li> "
                + "<li>DataHub (2018) List of all countries with their two digit codes (ISO 3166-1). https://datahub.io/core/country-list</li> "
                + "<li>Chapman AD and Wieczorek JR (2020) Georeferencing Best Practices. Copenhagen: GBIF Secretariat. https://doi.org/10.15468/doc-gg7h-s853</li> "
                + "</ul>";
        List<String> result = CitationUtils.parseReferences(html);
        assertEquals(4, result.size());
        assertTrue(result.get(0).startsWith("ISO (n.dat.) ISO 3166"));
        assertTrue(result.get(3).startsWith("Chapman AD"));
    }

    @Test
    public void testParseReferences_singleItem() {
        String html = "<ul><li>Single citation text</li></ul>";
        List<String> result = CitationUtils.parseReferences(html);
        assertEquals(1, result.size());
        assertEquals("Single citation text", result.get(0));
    }

    @Test
    public void testParseReferences_trimmingWhitespace() {
        String html = "<ul><li>  Citation with extra spaces  </li></ul>";
        List<String> result = CitationUtils.parseReferences(html);
        assertEquals(1, result.size());
        assertEquals("Citation with extra spaces", result.get(0));
    }

    @Test
    public void testParseReferences_internalWhitespaceCollapsed() {
        String html = "<ul><li>Citation   with\ttabs\nand newlines</li></ul>";
        List<String> result = CitationUtils.parseReferences(html);
        assertEquals(1, result.size());
        assertEquals("Citation with tabs and newlines", result.get(0));
    }

    // -----------------------------------------------------------------------
    // parseReferences – null / empty input
    // -----------------------------------------------------------------------

    @Test
    public void testParseReferences_null() {
        List<String> result = CitationUtils.parseReferences(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testParseReferences_emptyString() {
        List<String> result = CitationUtils.parseReferences("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testParseReferences_whitespaceOnly() {
        List<String> result = CitationUtils.parseReferences("   \t\n  ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -----------------------------------------------------------------------
    // parseReferences – malformed / structural errors
    // -----------------------------------------------------------------------

    @Test
    public void testParseReferences_missingUlWrapper_fallsBackToSingleCitation() {
        // No <ul>...</ul> – the method should fall back to treating the whole
        // string as a single citation (and log a warning).
        String value = "<li>Citation 1</li><li>Citation 2</li>";
        List<String> result = CitationUtils.parseReferences(value);
        // Should not throw; should return the raw value as single citation (non-empty)
        assertNotNull(result);
        assertFalse("Expected fallback to produce at least one entry", result.isEmpty());
    }

    @Test
    public void testParseReferences_missingClosingUl_fallsBackToSingleCitation() {
        String value = "<ul><li>Citation A</li>";
        List<String> result = CitationUtils.parseReferences(value);
        assertNotNull(result);
        // Falls back gracefully
        assertFalse(result.isEmpty());
    }

    @Test
    public void testParseReferences_emptyUlList() {
        // Valid <ul> but no <li> items
        String value = "<ul></ul>";
        List<String> result = CitationUtils.parseReferences(value);
        assertNotNull(result);
        assertTrue("Expected empty list when there are no <li> items", result.isEmpty());
    }

    // -----------------------------------------------------------------------
    // normalizeCitation
    // -----------------------------------------------------------------------

    @Test
    public void testNormalizeCitation_trims() {
        assertEquals("hello world", CitationUtils.normalizeCitation("  hello world  "));
    }

    @Test
    public void testNormalizeCitation_collapsesInternalWhitespace() {
        assertEquals("a b c", CitationUtils.normalizeCitation("a  b\tc"));
    }

    @Test
    public void testNormalizeCitation_null() {
        assertEquals("", CitationUtils.normalizeCitation(null));
    }

    @Test
    public void testNormalizeCitation_empty() {
        assertEquals("", CitationUtils.normalizeCitation(""));
    }

    // -----------------------------------------------------------------------
    // getOrCreateCitationUri – stable UUID mapping
    // -----------------------------------------------------------------------

    @Test
    public void testGetOrCreateCitationUri_sameCitationReturnsSameUri() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        String uri1 = CitationUtils.getOrCreateCitationUri("ISO 3166", map);
        String uri2 = CitationUtils.getOrCreateCitationUri("ISO 3166", map);
        assertNotNull(uri1);
        assertEquals("Same citation must always return the same URI", uri1, uri2);
        assertTrue("URI must be a urn:uuid:", uri1.startsWith("urn:uuid:"));
    }

    @Test
    public void testGetOrCreateCitationUri_differentCitationsGetDifferentUris() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        String uri1 = CitationUtils.getOrCreateCitationUri("Citation A", map);
        String uri2 = CitationUtils.getOrCreateCitationUri("Citation B", map);
        assertNotEquals("Different citations must get different URIs", uri1, uri2);
    }

    @Test
    public void testGetOrCreateCitationUri_preexistingMappingIsHonoured() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        String stableUri = "urn:uuid:aaaabbbb-1111-2222-3333-ccccddddeeee";
        map.put("Citation A", stableUri);
        String result = CitationUtils.getOrCreateCitationUri("Citation A", map);
        assertEquals("Pre-existing mapping must be returned unchanged", stableUri, result);
    }

    @Test
    public void testGetOrCreateCitationUri_normalizesBeforeLookup() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        // Insert via normalized form
        String uri = CitationUtils.getOrCreateCitationUri("Citation A", map);
        // Lookup with extra whitespace – should still find the same URI after normalization
        String uri2 = CitationUtils.getOrCreateCitationUri("  Citation A  ", map);
        assertEquals("Whitespace variants should resolve to the same URI", uri, uri2);
    }

    // -----------------------------------------------------------------------
    // loadCitationGuidMap / saveCitationGuidMap – persistence round-trip
    // -----------------------------------------------------------------------

    @Test
    public void testLoadSaveCitationGuidMap_roundTrip() throws Exception {
        File tempFile = File.createTempFile("citation-test", ".csv");
        tempFile.deleteOnExit();

        Map<String, String> original = new LinkedHashMap<String, String>();
        original.put("Citation Alpha", "urn:uuid:11111111-1111-1111-1111-111111111111");
        original.put("Citation Beta",  "urn:uuid:22222222-2222-2222-2222-222222222222");

        CitationUtils.saveCitationGuidMap(original, tempFile.getAbsolutePath(), 0);

        Map<String, String> loaded = CitationUtils.loadCitationGuidMap(tempFile.getAbsolutePath());
        assertEquals(2, loaded.size());
        assertEquals("urn:uuid:11111111-1111-1111-1111-111111111111",
                loaded.get("Citation Alpha"));
        assertEquals("urn:uuid:22222222-2222-2222-2222-222222222222",
                loaded.get("Citation Beta"));
    }

    @Test
    public void testLoadCitationGuidMap_missingFileReturnsEmptyMap() {
        Map<String, String> map = CitationUtils.loadCitationGuidMap(
                "/tmp/does-not-exist-citation-map.csv");
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    public void testLoadCitationGuidMap_nullPathReturnsEmptyMap() {
        Map<String, String> map = CitationUtils.loadCitationGuidMap(null);
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    public void testSaveCitationGuidMap_nullPathIsNoop() {
        // Should not throw
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("Citation", "urn:uuid:test");
        CitationUtils.saveCitationGuidMap(map, null, 0);
    }

    @Test
    public void testLoadSaveCitationGuidMap_stableUuidsAcrossRuns() throws Exception {
        File tempFile = File.createTempFile("citation-stable", ".csv");
        tempFile.deleteOnExit();

        // First run: mint new UUID
        Map<String, String> map1 = CitationUtils.loadCitationGuidMap(tempFile.getAbsolutePath());
        String uri1 = CitationUtils.getOrCreateCitationUri("My stable citation", map1);
        CitationUtils.saveCitationGuidMap(map1, tempFile.getAbsolutePath(), 0);

        // Second run: load the file and verify the same UUID is returned
        Map<String, String> map2 = CitationUtils.loadCitationGuidMap(tempFile.getAbsolutePath());
        String uri2 = CitationUtils.getOrCreateCitationUri("My stable citation", map2);

        assertEquals("UUID must be stable across save/load cycles", uri1, uri2);
    }

    @Test
    public void testSaveCitationGuidMap_fileFormatHasHeaderGuidFirst() throws Exception {
        File tempFile = File.createTempFile("citation-format", ".csv");
        tempFile.deleteOnExit();

        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("ISO 3166 Country Codes. https://www.iso.org/", "urn:uuid:aaaabbbb-1111-2222-3333-ccccddddeeee");
        map.put("Citation with a \"quoted\" word.", "urn:uuid:ffffffff-5555-6666-7777-888899990000");

        CitationUtils.saveCitationGuidMap(map, tempFile.getAbsolutePath(), 0);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(tempFile), StandardCharsets.UTF_8));
        try {
            // First line must be the header
            String header = reader.readLine();
            assertEquals("Header row must be \"guid\",\"citation\"",
                    "\"guid\",\"citation\"", header);

            // First data row: guid first, citation text second; both double-quoted
            String row1 = reader.readLine();
            assertNotNull("Expected first data row", row1);
            assertTrue("First data row must start with the quoted UUID",
                    row1.startsWith("\"urn:uuid:aaaabbbb-1111-2222-3333-ccccddddeeee\""));
            assertTrue("First data row must contain the quoted citation text",
                    row1.contains("\"ISO 3166 Country Codes. https://www.iso.org/\""));

            // Second data row: embedded quotes must be escaped by doubling
            String row2 = reader.readLine();
            assertNotNull("Expected second data row", row2);
            assertTrue("Embedded quotes must be escaped by doubling (RFC 4180)",
                    row2.contains("\"\"quoted\"\""));
        } finally {
            reader.close();
        }
    }

    // -----------------------------------------------------------------------
    // buildCitationResources – list structure
    // -----------------------------------------------------------------------

    @Test
    public void testBuildCitationResources_returnsList() {
        Map<String, String> guidMap = new LinkedHashMap<String, String>();
        List<BibliographicResource> resources = CitationUtils.buildCitationResources(
                Arrays.asList("Citation A", "Citation B"), guidMap);
        assertEquals(2, resources.size());
        assertEquals(2, guidMap.size());
    }

    @Test
    public void testBuildCitationResources_eachResourceHasCitationText() {
        Map<String, String> guidMap = new LinkedHashMap<String, String>();
        List<BibliographicResource> resources = CitationUtils.buildCitationResources(
                Arrays.asList("My citation text"), guidMap);
        assertEquals(1, resources.size());
        assertEquals("My citation text", resources.get(0).getBibliographicCitation());
    }

    @Test
    public void testBuildCitationResources_eachResourceHasStableUri() {
        Map<String, String> guidMap = new LinkedHashMap<String, String>();
        List<BibliographicResource> r1 = CitationUtils.buildCitationResources(
                Arrays.asList("Stable citation"), guidMap);
        List<BibliographicResource> r2 = CitationUtils.buildCitationResources(
                Arrays.asList("Stable citation"), guidMap);
        assertEquals("Same citation must produce same URI across calls",
                r1.get(0).getId(), r2.get(0).getId());
    }

    @Test
    public void testBuildCitationResources_nullInputReturnsEmptyList() {
        Map<String, String> guidMap = new LinkedHashMap<String, String>();
        List<BibliographicResource> resources =
                CitationUtils.buildCitationResources(null, guidMap);
        assertNotNull(resources);
        assertTrue(resources.isEmpty());
        assertTrue(guidMap.isEmpty());
    }

    @Test
    public void testBuildCitationResources_emptyInputReturnsEmptyList() {
        Map<String, String> guidMap = new LinkedHashMap<String, String>();
        List<BibliographicResource> resources = CitationUtils.buildCitationResources(
                java.util.Collections.<String>emptyList(), guidMap);
        assertNotNull(resources);
        assertTrue(resources.isEmpty());
    }

    // -----------------------------------------------------------------------
    // RDF graph assertions using @RDF-annotated BibliographicResource
    // -----------------------------------------------------------------------

    /**
     * Helper: create a minimal Validation with the given ID and citation
     * resources, save it to the model, and return the model.
     */
    private FFDQModel saveValidationWithCitations(
            String needUri, List<String> citations, Map<String, String> guidMap) {
        FFDQModel model = new FFDQModel();
        Validation v = new Validation();
        v.setId(needUri);
        List<BibliographicResource> resources =
                CitationUtils.buildCitationResources(citations, guidMap);
        v.setCitationResources(resources);
        model.save(v);
        return model;
    }

    @Test
    public void testRdf_referencesTriplesPresentOnNeed() {
        String needUri = "urn:uuid:test-need-00000000-0000-0000-0000-000000000001";
        Map<String, String> guidMap = new LinkedHashMap<String, String>();

        FFDQModel model = saveValidationWithCitations(
                needUri, Arrays.asList("Citation One", "Citation Two"), guidMap);

        assertEquals("guidMap must contain 2 entries", 2, guidMap.size());

        String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "SELECT ?resource WHERE { <" + needUri + "> dcterms:references ?resource }";
        TupleQueryResult result = model.executeQuery(sparql);
        int count = 0;
        while (result.hasNext()) {
            result.next();
            count++;
        }
        assertEquals("Exactly 2 dcterms:references triples expected", 2, count);
    }

    @Test
    public void testRdf_bibliographicCitationOnResourceNode() {
        String needUri = "urn:uuid:test-need-00000000-0000-0000-0000-000000000002";
        String citationText = "Unique citation for resource test";
        Map<String, String> guidMap = new LinkedHashMap<String, String>();

        FFDQModel model = saveValidationWithCitations(
                needUri, Arrays.asList(citationText), guidMap);

        String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "SELECT ?citation WHERE { "
                + "<" + needUri + "> dcterms:references ?res . "
                + "?res dcterms:bibliographicCitation ?citation }";
        TupleQueryResult result = model.executeQuery(sparql);
        assertTrue("Expected a dcterms:bibliographicCitation on the resource", result.hasNext());
        String actual = result.next().getValue("citation").stringValue();
        assertEquals(citationText, actual);
    }

    @Test
    public void testRdf_bibliographicCitationNotDirectlyOnNeed() {
        String needUri = "urn:uuid:test-need-00000000-0000-0000-0000-000000000003";
        Map<String, String> guidMap = new LinkedHashMap<String, String>();

        FFDQModel model = saveValidationWithCitations(
                needUri, Arrays.asList("Some citation"), guidMap);

        String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "SELECT ?citation WHERE { "
                + "<" + needUri + "> dcterms:bibliographicCitation ?citation }";
        TupleQueryResult result = model.executeQuery(sparql);
        assertFalse(
                "Need instance must NOT have dcterms:bibliographicCitation directly",
                result.hasNext());
    }

    @Test
    public void testRdf_resourceTypedAsBibliographicResource() {
        String needUri = "urn:uuid:test-need-00000000-0000-0000-0000-000000000004";
        Map<String, String> guidMap = new LinkedHashMap<String, String>();

        FFDQModel model = saveValidationWithCitations(
                needUri, Arrays.asList("Type check citation"), guidMap);

        String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "SELECT ?res WHERE { "
                + "<" + needUri + "> dcterms:references ?res . "
                + "?res rdf:type dcterms:BibliographicResource }";
        TupleQueryResult result = model.executeQuery(sparql);
        assertTrue("Resource node must be typed as dcterms:BibliographicResource",
                result.hasNext());
    }

    @Test
    public void testRdf_sharedCitationUsesSharedNode() {
        String needUri1 = "urn:uuid:test-need-00000000-0000-0000-0000-000000000005";
        String needUri2 = "urn:uuid:test-need-00000000-0000-0000-0000-000000000006";
        String sharedCitation = "Shared citation text for both needs";
        Map<String, String> guidMap = new LinkedHashMap<String, String>();

        // Save both needs to the same model so we can cross-query them
        FFDQModel model = new FFDQModel();

        Validation v1 = new Validation();
        v1.setId(needUri1);
        v1.setCitationResources(
                CitationUtils.buildCitationResources(Arrays.asList(sharedCitation), guidMap));
        model.save(v1);

        Validation v2 = new Validation();
        v2.setId(needUri2);
        v2.setCitationResources(
                CitationUtils.buildCitationResources(Arrays.asList(sharedCitation), guidMap));
        model.save(v2);

        // Both Needs reference the same citation URI
        String sharedUri = guidMap.get(CitationUtils.normalizeCitation(sharedCitation));
        assertNotNull(sharedUri);

        String sparql = "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "SELECT ?need WHERE { ?need dcterms:references <" + sharedUri + "> }";
        TupleQueryResult result = model.executeQuery(sparql);
        int count = 0;
        while (result.hasNext()) {
            result.next();
            count++;
        }
        assertEquals("Both Need instances should reference the same citation node", 2, count);
    }
}
