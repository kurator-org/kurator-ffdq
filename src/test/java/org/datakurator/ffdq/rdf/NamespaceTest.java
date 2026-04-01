package org.datakurator.ffdq.rdf;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link Namespace} helper methods added to support CURIE expansion.
 */
public class NamespaceTest {

    // --- localNameFor ---

    @Test
    public void testLocalNameFor_bareLocalName() {
        assertEquals("Conformance", Namespace.localNameFor("Conformance"));
    }

    @Test
    public void testLocalNameFor_curieWithKnownPrefix() {
        assertEquals("Conformance", Namespace.localNameFor("bdqdim:Conformance"));
        assertEquals("Standard",    Namespace.localNameFor("bdqcrit:Standard"));
        assertEquals("Standardized", Namespace.localNameFor("bdqenh:Standardized"));
        assertEquals("SingleRecord", Namespace.localNameFor("bdqffdq:SingleRecord"));
        assertEquals("MultiRecord",  Namespace.localNameFor("bdqffdq:MultiRecord"));
    }

    @Test
    public void testLocalNameFor_fullHttpsIri() {
        assertEquals("Conformance",
                Namespace.localNameFor("https://rs.tdwg.org/bdqdim/terms/Conformance"));
        assertEquals("Standard",
                Namespace.localNameFor("https://rs.tdwg.org/bdqcrit/terms/Standard"));
        assertEquals("Standardized",
                Namespace.localNameFor("https://rs.tdwg.org/bdqenh/terms/Standardized"));
        assertEquals("SingleRecord",
                Namespace.localNameFor("https://rs.tdwg.org/bdqffdq/terms/SingleRecord"));
    }

    @Test
    public void testLocalNameFor_fullHttpIri() {
        assertEquals("eventDate",
                Namespace.localNameFor("http://rs.tdwg.org/dwc/terms/eventDate"));
    }

    @Test
    public void testLocalNameFor_iriWithHash() {
        assertEquals("label",
                Namespace.localNameFor("http://www.w3.org/2000/01/rdf-schema#label"));
    }

    @Test
    public void testLocalNameFor_curieWithUnknownPrefix() {
        // Unknown prefix → returned unchanged
        assertEquals("unknown:Foo", Namespace.localNameFor("unknown:Foo"));
    }

    @Test
    public void testLocalNameFor_null() {
        assertNull(Namespace.localNameFor(null));
    }

    @Test
    public void testLocalNameFor_empty() {
        assertEquals("", Namespace.localNameFor(""));
    }

    // --- expandCurie ---

    @Test
    public void testExpandCurie_alreadyFullIri() {
        String full = "https://rs.tdwg.org/bdqdim/terms/Conformance";
        assertEquals(full, Namespace.expandCurie(full));
    }

    @Test
    public void testExpandCurie_knownPrefix() {
        assertEquals("https://rs.tdwg.org/bdqdim/terms/Conformance",
                Namespace.expandCurie("bdqdim:Conformance"));
        assertEquals("https://rs.tdwg.org/bdqcrit/terms/Standard",
                Namespace.expandCurie("bdqcrit:Standard"));
        assertEquals("https://rs.tdwg.org/bdqenh/terms/Standardized",
                Namespace.expandCurie("bdqenh:Standardized"));
        assertEquals("https://rs.tdwg.org/bdqffdq/terms/SingleRecord",
                Namespace.expandCurie("bdqffdq:SingleRecord"));
    }

    @Test
    public void testExpandCurie_urnIsPreserved() {
        String urn = "urn:uuid:fb80d5bf-7777-4d88-b09f-116a38ab7bfa";
        assertEquals(urn, Namespace.expandCurie(urn));
    }

    @Test
    public void testExpandCurie_unknownPrefixReturnedUnchanged() {
        assertEquals("unknown:Foo", Namespace.expandCurie("unknown:Foo"));
    }

    @Test
    public void testExpandCurie_null() {
        assertNull(Namespace.expandCurie(null));
    }
}
