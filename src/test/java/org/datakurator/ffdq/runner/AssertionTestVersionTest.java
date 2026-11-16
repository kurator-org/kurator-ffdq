package org.datakurator.ffdq.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * The version an implementation cites in @ProvidesVersion: rs.tdwg.org's canonical version IRI of the
 * test's version on its DateLastUpdated.
 */
public class AssertionTestVersionTest {

	@Test
	public void providesVersionIsTheCanonicalVersionIri() {
		AssertionTest test = new AssertionTest();
		test.setGuid("urn:uuid:69b2efdc-6269-45a4-aecb-4cb99c2ae134");
		test.setVersion("2024-08-19");
		assertEquals("https://rs.tdwg.org/bdqtest/terms/version/69b2efdc-6269-45a4-aecb-4cb99c2ae134-2024-08-19",
				test.getProvidesVersion());
	}

	@Test
	public void versionKeyReadsEveryForm() {
		String key = "69b2efdc-6269-45a4-aecb-4cb99c2ae134|2024-08-19";
		assertEquals(key, AssertionTest.versionKey(
				"https://rs.tdwg.org/bdqtest/terms/version/69b2efdc-6269-45a4-aecb-4cb99c2ae134-2024-08-19"));
		assertEquals(key, AssertionTest.versionKey(
				"https://rs.tdwg.org/bdqtest/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134-2024-08-19"));
		assertEquals(key, AssertionTest.versionKey(
				"https://rs.tdwg.org/bdqtest/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134/2024-08-19"));
		assertEquals(key, AssertionTest.versionKey(
				"https://rs.tdwg.org/bdq/terms/69B2EFDC-6269-45A4-AECB-4CB99C2AE134/2024-08-19"));
		assertEquals(key, AssertionTest.versionKey("69b2efdc-6269-45a4-aecb-4cb99c2ae134-2024-08-19"));
		assertNull(AssertionTest.versionKey("https://rs.tdwg.org/bdqtest/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134"));
		assertNull(AssertionTest.versionKey(null));
	}

	@Test
	public void testNotInTheStandardIsIdentifiedByUrnUuid() {
		// Tests not in the BDQ standard have no resolvable versions: implementations cite urn:uuid:,
		// and the generated RDF identifies the Test by its urn:uuid:, with no dcterms:isVersionOf.
		AssertionTest test = new AssertionTest();
		test.setGuid("urn:uuid:e654f562-44f8-43fd-983b-2aaba4c6dda9");
		test.setVersion("2024-09-18");
		test.setIssued("2025-03-05");
		test.setInStandard(false);
		assertEquals("urn:uuid:e654f562-44f8-43fd-983b-2aaba4c6dda9", test.getProvidesVersion());
		assertEquals("urn:uuid:e654f562-44f8-43fd-983b-2aaba4c6dda9", test.getTestNodeIri());
		assertNull(test.getTestNodeIsVersionOf());
		test.setInStandard(true);
		assertEquals("https://rs.tdwg.org/bdqtest/terms/e654f562-44f8-43fd-983b-2aaba4c6dda9-2025-03-05", test.getTestNodeIri());
		assertEquals("https://rs.tdwg.org/bdqtest/terms/e654f562-44f8-43fd-983b-2aaba4c6dda9", test.getTestNodeIsVersionOf());
	}
}
