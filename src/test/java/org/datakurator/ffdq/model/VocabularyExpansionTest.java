package org.datakurator.ffdq.model;

import org.datakurator.ffdq.rdf.Namespace;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Regression tests for CURIE-expansion fix in controlled-vocabulary model classes.
 *
 * <p>Each {@code fromString()} method and the {@link Dimension} constructor must
 * accept bare local names, CURIE-form strings (e.g. {@code "bdqdim:Conformance"}),
 * and full IRIs, and must produce an object whose {@code getId()} returns the
 * bare local name so that—when combined with the full-IRI {@code @RDFSubject}
 * prefix—the resulting RDF subject is the correct, fully-expanded IRI.
 */
public class VocabularyExpansionTest {

    private static final String BDQFFDQ = "https://rs.tdwg.org/bdqffdq/terms/";
    private static final String BDQDIM  = "https://rs.tdwg.org/bdqdim/terms/";
    private static final String BDQCRIT = "https://rs.tdwg.org/bdqcrit/terms/";
    private static final String BDQENH  = "https://rs.tdwg.org/bdqenh/terms/";
    private static final String BDQVAL     = "https://rs.tdwg.org/bdqval/terms/";

    // -----------------------------------------------------------------------
    // ResourceType
    // -----------------------------------------------------------------------

    @Test
    public void testResourceType_fromString_bareLocalName() {
        ResourceType rt = ResourceType.fromString("SingleRecord");
        assertEquals("SingleRecord", rt.getId());
        assertEquals(BDQFFDQ + "SingleRecord", BDQFFDQ + rt.getId());
    }

    @Test
    public void testResourceType_fromString_curie() {
        ResourceType rt = ResourceType.fromString("bdqffdq:SingleRecord");
        assertEquals("SingleRecord", rt.getId());
        assertEquals(BDQFFDQ + "SingleRecord", BDQFFDQ + rt.getId());
    }

    @Test
    public void testResourceType_fromString_fullIri() {
        ResourceType rt = ResourceType.fromString(BDQFFDQ + "SingleRecord");
        assertEquals("SingleRecord", rt.getId());
    }

    @Test
    public void testResourceType_fromString_multiRecord_curie() {
        ResourceType rt = ResourceType.fromString("bdqffdq:MultiRecord");
        assertEquals("MultiRecord", rt.getId());
        assertEquals(BDQFFDQ + "MultiRecord", BDQFFDQ + rt.getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testResourceType_fromString_unknown_throws() {
        ResourceType.fromString("Unknown");
    }

    // -----------------------------------------------------------------------
    // Dimension
    // -----------------------------------------------------------------------

    @Test
    public void testDimension_fromString_bareLocalName() {
        Dimension d = Dimension.fromString("Conformance");
        assertEquals("Conformance", d.getId());
        assertEquals(BDQDIM + "Conformance", BDQDIM + d.getId());
    }

    @Test
    public void testDimension_fromString_curie() {
        Dimension d = Dimension.fromString("bdqdim:Conformance");
        assertEquals("Conformance", d.getId());
        assertEquals(BDQDIM + "Conformance", BDQDIM + d.getId());
    }

    @Test
    public void testDimension_fromString_fullIri() {
        Dimension d = Dimension.fromString(BDQDIM + "Conformance");
        assertEquals("Conformance", d.getId());
    }

    @Test
    public void testDimension_constructor_curie() {
        Dimension d = new Dimension("bdqdim:Conformance");
        assertEquals("Conformance", d.getId());
        assertEquals(BDQDIM + "Conformance", BDQDIM + d.getId());
    }

    @Test
    public void testDimension_constructor_fullIri() {
        Dimension d = new Dimension(BDQDIM + "Conformance");
        assertEquals("Conformance", d.getId());
    }

    @Test
    public void testDimension_constructor_bareLocalName() {
        Dimension d = new Dimension("Completeness");
        assertEquals("Completeness", d.getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDimension_fromString_unknown_throws() {
        Dimension.fromString("Unknown");
    }

    // -----------------------------------------------------------------------
    // Criterion
    // -----------------------------------------------------------------------

    @Test
    public void testCriterion_fromString_bareLocalName() {
        Criterion c = Criterion.fromString("Standard");
        assertEquals("Standard", c.getId());
        assertEquals(BDQCRIT + "Standard", BDQCRIT + c.getId());
    }

    @Test
    public void testCriterion_fromString_curie() {
        Criterion c = Criterion.fromString("bdqcrit:Standard");
        assertEquals("Standard", c.getId());
        assertEquals(BDQCRIT + "Standard", BDQCRIT + c.getId());
    }

    @Test
    public void testCriterion_fromString_fullIri() {
        Criterion c = Criterion.fromString(BDQCRIT + "Standard");
        assertEquals("Standard", c.getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCriterion_fromString_unknown_throws() {
        Criterion.fromString("Unknown");
    }

    // -----------------------------------------------------------------------
    // Enhancement
    // -----------------------------------------------------------------------

    @Test
    public void testEnhancement_fromString_bareLocalName() {
        Enhancement e = Enhancement.fromString("Standardized");
        assertEquals("Standardized", e.getId());
        assertEquals(BDQENH + "Standardized", BDQENH + e.getId());
    }

    @Test
    public void testEnhancement_fromString_curie() {
        Enhancement e = Enhancement.fromString("bdqenh:Standardized");
        assertEquals("Standardized", e.getId());
        assertEquals(BDQENH + "Standardized", BDQENH + e.getId());
    }

    @Test
    public void testEnhancement_fromString_fullIri() {
        Enhancement e = Enhancement.fromString(BDQENH + "Standardized");
        assertEquals("Standardized", e.getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEnhancement_fromString_unknown_throws() {
        Enhancement.fromString("Unknown");
    }

    // -----------------------------------------------------------------------
    // Verify that the @RDFSubject prefix constant strings are correct full IRIs
    // -----------------------------------------------------------------------

    @Test
    public void testResourceType_getIdProducesCorrectIri_viaStaticConstant() {
        // SINGLE_RECORD.getId() should return "SingleRecord" and the
        // @RDFSubject prefix "https://rs.tdwg.org/bdqffdq/terms/" + getId()
        // must give the well-formed IRI.
        assertEquals(BDQFFDQ + "SingleRecord", BDQFFDQ + ResourceType.SINGLE_RECORD.getId());
        assertEquals(BDQFFDQ + "MultiRecord",  BDQFFDQ + ResourceType.MULTI_RECORD.getId());
    }

    @Test
    public void testDimension_getIdProducesCorrectIri_viaStaticConstant() {
        assertEquals(BDQDIM + "Conformance",  BDQDIM + Dimension.CONFORMANCE.getId());
        assertEquals(BDQDIM + "Completeness", BDQDIM + Dimension.COMPLETENESS.getId());
    }

    @Test
    public void testCriterion_getIdProducesCorrectIri_viaStaticConstant() {
        assertEquals(BDQCRIT + "Standard",   BDQCRIT + Criterion.STANDARD.getId());
        assertEquals(BDQCRIT + "NotEmpty",   BDQCRIT + Criterion.NOTEMPTY.getId());
    }

    @Test
    public void testEnhancement_getIdProducesCorrectIri_viaStaticConstant() {
        assertEquals(BDQENH + "Standardized", BDQENH + Enhancement.STANDARDIZED.getId());
        assertEquals(BDQENH + "Transposed",   BDQENH + Enhancement.TRANSPOSED.getId());
    }

    // -----------------------------------------------------------------------
    // Verify no spurious angle-bracket-CURIE IRIs are generated.
    // The resulting IRI should START with the namespace URI, not with the prefix label.
    // -----------------------------------------------------------------------

    @Test
    public void testResourceType_iriDoesNotContainCurieForm() {
        String iri = BDQFFDQ + ResourceType.fromString("SingleRecord").getId();
        assertFalse("IRI must not be a CURIE literal", iri.equals("bdqffdq:SingleRecord"));
        assertTrue("IRI must start with namespace", iri.startsWith(BDQFFDQ));
    }

    @Test
    public void testDimension_iriDoesNotContainCurieForm() {
        String iri = BDQDIM + Dimension.fromString("Conformance").getId();
        assertFalse("IRI must not be a CURIE literal", iri.equals("bdqdim:Conformance"));
        assertTrue("IRI must start with namespace", iri.startsWith(BDQDIM));
    }

    @Test
    public void testCriterion_iriDoesNotContainCurieForm() {
        String iri = BDQCRIT + Criterion.fromString("Standard").getId();
        assertFalse("IRI must not be a CURIE literal", iri.equals("bdqcrit:Standard"));
        assertTrue("IRI must start with namespace", iri.startsWith(BDQCRIT));
    }

    @Test
    public void testEnhancement_iriDoesNotContainCurieForm() {
        String iri = BDQENH + Enhancement.fromString("Standardized").getId();
        assertFalse("IRI must not be a CURIE literal", iri.equals("bdqenh:Standardized"));
        assertTrue("IRI must start with namespace", iri.startsWith(BDQENH));
    }

    // -----------------------------------------------------------------------
    // Parameter — CURIE-as-IRI fix (bdqval: prefix must be expanded)
    // -----------------------------------------------------------------------

    @Test
    public void testParameter_constructor_curie() {
        Parameter p = new Parameter("bdqval:sourceAuthority");
        assertEquals(BDQVAL + "sourceAuthority", p.getId());
    }

    @Test
    public void testParameter_constructor_fullIri() {
        Parameter p = new Parameter(BDQVAL + "sourceAuthority");
        assertEquals(BDQVAL + "sourceAuthority", p.getId());
    }

    @Test
    public void testParameter_iriDoesNotContainCurieForm() {
        Parameter p = new Parameter("bdqval:sourceAuthority");
        assertFalse("Parameter IRI must not be a CURIE literal", p.getId().equals("bdqval:sourceAuthority"));
        assertTrue("Parameter IRI must be a full IRI", p.getId().startsWith("https://"));
    }
}
