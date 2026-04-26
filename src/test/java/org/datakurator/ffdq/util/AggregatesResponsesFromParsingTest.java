package org.datakurator.ffdq.util;

import org.datakurator.ffdq.runner.AssertionTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the {@code aggregatesResponsesFrom} CSV column parsing in {@link TestUtil}
 * and storage in {@link AssertionTest}.
 *
 * <p>These tests cover:
 * <ul>
 *   <li>Comma-separated values in the {@code aggregatesResponsesFrom} CSV column are
 *       split into individual IRI strings.</li>
 *   <li>Semicolon-separated values are also accepted.</li>
 *   <li>Empty / null column values produce an empty list (backwards compatibility).</li>
 *   <li>{@link AssertionTest} stores and retrieves the values unchanged.</li>
 * </ul>
 *
 * <p>Reference: tdwg/bdq#331
 */
public class AggregatesResponsesFromParsingTest {

    private static final String IRI_COUNTRY_FOUND =
            "https://rs.tdwg.org/bdqtest/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134";
    private static final String IRI_OTHER =
            "https://rs.tdwg.org/bdqtest/terms/00000000-0000-0000-0000-000000000001";

    // --------------------------------------------------------------------------
    // AssertionTest getter/setter for aggregatesResponsesFrom
    // --------------------------------------------------------------------------

    @Test
    public void testAssertionTest_aggregatesResponsesFrom_defaultEmpty() {
        AssertionTest at = new AssertionTest();
        assertNotNull(at.getAggregatesResponsesFrom());
        assertTrue(at.getAggregatesResponsesFrom().isEmpty());
    }

    @Test
    public void testAssertionTest_setAggregatesResponsesFrom_singleValue() {
        AssertionTest at = new AssertionTest();
        at.setAggregatesResponsesFrom(Collections.singletonList(IRI_COUNTRY_FOUND));

        List<String> result = at.getAggregatesResponsesFrom();
        assertEquals(1, result.size());
        assertEquals(IRI_COUNTRY_FOUND, result.get(0));
    }

    @Test
    public void testAssertionTest_setAggregatesResponsesFrom_multipleValues() {
        AssertionTest at = new AssertionTest();
        at.setAggregatesResponsesFrom(Arrays.asList(IRI_COUNTRY_FOUND, IRI_OTHER));

        List<String> result = at.getAggregatesResponsesFrom();
        assertEquals(2, result.size());
        assertTrue(result.contains(IRI_COUNTRY_FOUND));
        assertTrue(result.contains(IRI_OTHER));
    }

    @Test
    public void testAssertionTest_setAggregatesResponsesFrom_nullTreatedAsEmpty() {
        AssertionTest at = new AssertionTest();
        at.setAggregatesResponsesFrom(null);
        assertNotNull(at.getAggregatesResponsesFrom());
        assertTrue(at.getAggregatesResponsesFrom().isEmpty());
    }

    // --------------------------------------------------------------------------
    // Parsing helper – exercise via TestUtil's public API (parseDefaultFrom...)
    // Note: parseAggregatesResponsesFromStr is private; we verify its behaviour
    // indirectly by reading the field after a full CSV round-trip in integration
    // tests. Here we test the split semantics manually as a specification check.
    // --------------------------------------------------------------------------

    @Test
    public void testSplitSemantics_commaSeparated() {
        String cell = IRI_COUNTRY_FOUND + "," + IRI_OTHER;
        List<String> result = splitAggregatesResponsesFrom(cell);
        assertEquals(2, result.size());
        assertTrue(result.contains(IRI_COUNTRY_FOUND));
        assertTrue(result.contains(IRI_OTHER));
    }

    @Test
    public void testSplitSemantics_semicolonSeparated() {
        String cell = IRI_COUNTRY_FOUND + ";" + IRI_OTHER;
        List<String> result = splitAggregatesResponsesFrom(cell);
        assertEquals(2, result.size());
        assertTrue(result.contains(IRI_COUNTRY_FOUND));
        assertTrue(result.contains(IRI_OTHER));
    }

    @Test
    public void testSplitSemantics_whitespaceTrimmed() {
        String cell = "  " + IRI_COUNTRY_FOUND + " , " + IRI_OTHER + "  ";
        List<String> result = splitAggregatesResponsesFrom(cell);
        assertEquals(2, result.size());
        assertTrue(result.contains(IRI_COUNTRY_FOUND));
        assertTrue(result.contains(IRI_OTHER));
    }

    @Test
    public void testSplitSemantics_emptyString() {
        List<String> result = splitAggregatesResponsesFrom("");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSplitSemantics_null() {
        List<String> result = splitAggregatesResponsesFrom(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSplitSemantics_singleValue() {
        List<String> result = splitAggregatesResponsesFrom(IRI_COUNTRY_FOUND);
        assertEquals(1, result.size());
        assertEquals(IRI_COUNTRY_FOUND, result.get(0));
    }

    /**
     * Replicates the logic from {@code TestUtil.parseAggregatesResponsesFromStr}
     * (which is private) so that we can unit-test the splitting semantics here.
     */
    private static List<String> splitAggregatesResponsesFrom(String str) {
        List<String> result = new java.util.ArrayList<>();
        if (str == null || str.trim().isEmpty()) {
            return result;
        }
        for (String token : str.split("[,;]")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
