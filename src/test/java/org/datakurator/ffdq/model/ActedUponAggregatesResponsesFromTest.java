package org.datakurator.ffdq.model;

import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the {@code bdqffdq:aggregatesResponsesFrom} support on {@link ActedUpon}.
 *
 * <p>Validates that:
 * <ul>
 *   <li>An {@link ActedUpon} instance can hold one or many upstream test term URIs via
 *       {@link ActedUpon#getAggregatesResponsesFrom()}.</li>
 *   <li>Distinct {@link ActedUpon} instances are independent (no shared list).</li>
 *   <li>Null values are ignored by {@link ActedUpon#addAggregatesResponsesFrom(URI)}.</li>
 * </ul>
 *
 * <p>Reference: tdwg/bdq#331
 */
public class ActedUponAggregatesResponsesFromTest {

    private static final URI UPSTREAM_COUNTRY_FOUND =
            URI.create("https://rs.tdwg.org/bdqtest/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134");
    private static final URI UPSTREAM_OTHER =
            URI.create("https://rs.tdwg.org/bdqtest/terms/00000000-0000-0000-0000-000000000001");

    /**
     * A freshly-created ActedUpon must have an empty aggregatesResponsesFrom list.
     */
    @Test
    public void testAggregatesResponsesFrom_initiallyEmpty() {
        ActedUpon au = new ActedUpon();
        assertNotNull(au.getAggregatesResponsesFrom());
        assertTrue(au.getAggregatesResponsesFrom().isEmpty());
    }

    /**
     * Adding a single URI via addAggregatesResponsesFrom should store and return it.
     */
    @Test
    public void testAddAggregatesResponsesFrom_singleUri() {
        ActedUpon au = new ActedUpon();
        au.addAggregatesResponsesFrom(UPSTREAM_COUNTRY_FOUND);

        List<URI> result = au.getAggregatesResponsesFrom();
        assertEquals(1, result.size());
        assertEquals(UPSTREAM_COUNTRY_FOUND, result.get(0));
    }

    /**
     * Multiple URIs can be added and all should be returned.
     */
    @Test
    public void testAddAggregatesResponsesFrom_multipleUris() {
        ActedUpon au = new ActedUpon();
        au.addAggregatesResponsesFrom(UPSTREAM_COUNTRY_FOUND);
        au.addAggregatesResponsesFrom(UPSTREAM_OTHER);

        List<URI> result = au.getAggregatesResponsesFrom();
        assertEquals(2, result.size());
        assertTrue(result.contains(UPSTREAM_COUNTRY_FOUND));
        assertTrue(result.contains(UPSTREAM_OTHER));
    }

    /**
     * A null argument to addAggregatesResponsesFrom must be silently ignored.
     */
    @Test
    public void testAddAggregatesResponsesFrom_nullIgnored() {
        ActedUpon au = new ActedUpon();
        au.addAggregatesResponsesFrom(null);
        assertTrue(au.getAggregatesResponsesFrom().isEmpty());
    }

    /**
     * setAggregatesResponsesFrom should replace any previously added values.
     */
    @Test
    public void testSetAggregatesResponsesFrom_replacesExisting() {
        ActedUpon au = new ActedUpon();
        au.addAggregatesResponsesFrom(UPSTREAM_COUNTRY_FOUND);

        List<URI> newList = Arrays.asList(UPSTREAM_OTHER);
        au.setAggregatesResponsesFrom(newList);

        List<URI> result = au.getAggregatesResponsesFrom();
        assertEquals(1, result.size());
        assertEquals(UPSTREAM_OTHER, result.get(0));
    }

    /**
     * Two distinct ActedUpon instances should have independent aggregatesResponsesFrom lists.
     */
    @Test
    public void testTwoInstancesAreIndependent() {
        ActedUpon au1 = new ActedUpon();
        ActedUpon au2 = new ActedUpon();

        au1.addAggregatesResponsesFrom(UPSTREAM_COUNTRY_FOUND);

        assertTrue("au2 should not be affected by au1's additions",
                au2.getAggregatesResponsesFrom().isEmpty());
        assertEquals(1, au1.getAggregatesResponsesFrom().size());
    }
}
