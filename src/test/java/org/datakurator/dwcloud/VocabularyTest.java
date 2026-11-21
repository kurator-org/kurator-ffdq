/**
 * VocabularyTest.java
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
package org.datakurator.dwcloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import org.datakurator.ffdq.model.DataResource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Term mapping in {@link Vocabulary}: the darwin_cloud vocabulary first, then the bundled list
 * of current Darwin Core terms, then known namespace prefixes.
 */
public class VocabularyTest {

    private static final String VOCAB_NAME = "vocabulary-test-darwin_cloud.txt";
    private static File vocabFile;
    private static Vocabulary vocab;

    @BeforeClass
    public static void setUp() throws Exception {
        File dir = Files.createTempDirectory("vocabulary-test").toFile();
        vocabFile = new File(dir, VOCAB_NAME);
        // A darwin_cloud vocabulary that, like the published one, lacks newer Darwin Core terms.
        Files.write(vocabFile.toPath(), String.join("\n",
                "fieldname\tstandard\tvetted\tnamespace\tsource\tcomment",
                "SCIENTIFICNAME\tscientificName\t1\tdwc\t\t",
                "OCCURRENCEID\toccurrenceID\t1\tdwc\t\t",
                "TYPE\ttype\t1\tdcterms\t\t",
                "").getBytes(StandardCharsets.UTF_8));
        vocab = new Vocabulary(vocabFile, "dwc:occurrenceID");
    }

    @AfterClass
    public static void tearDown() {
        // Vocabulary keeps a copy of the vocabulary file in the working directory.
        new File(VOCAB_NAME).delete();
        vocabFile.delete();
        vocabFile.getParentFile().delete();
    }

    @Test
    public void termInDarwinCloud() {
        assertEquals(URI.create("http://rs.tdwg.org/dwc/terms/scientificName"), vocab.getURI("scientificName"));
        assertEquals(URI.create("http://rs.tdwg.org/dwc/terms/scientificName"), vocab.getURI("dwc:scientificName"));
    }

    @Test
    public void currentDarwinCoreTermsNotInDarwinCloud() {
        for (String term : new String[] {"cultivarEpithet", "degreeOfEstablishment", "genericName",
                "infragenericEpithet", "pathway", "subfamily", "superfamily", "tribe", "subtribe"}) {
            assertEquals(URI.create("http://rs.tdwg.org/dwc/terms/" + term), vocab.getURI(term));
            assertEquals(URI.create("http://rs.tdwg.org/dwc/terms/" + term), vocab.getURI("dwc:" + term));
        }
    }

    @Test
    public void prefixedTermInKnownNamespace() {
        assertEquals(URI.create("http://www.w3.org/ns/oa#hasTarget"), vocab.getURI("oa:hasTarget"));
    }

    @Test
    public void unknownTerms() {
        assertNull(vocab.findURI("taxon"));          // a class name, not a Darwin Core property
        assertNull(vocab.findURI("dwc:taxon"));
        assertNull(vocab.findURI("nosuchprefix:term"));
        assertNull(vocab.findURI(""));
        assertNull(vocab.findURI(null));
    }

    @Test(expected = RuntimeException.class)
    public void getURIThrowsForUnknownTerm() {
        vocab.getURI("taxon");
    }

    @Test
    public void dataResourceKeepsNewTermsAndLeavesOutUnknownOnes() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("scientificName", "Chicoreus palmarosae");
        record.put("pathway", "releaseForUse");
        record.put("oa:hasTarget", "");
        record.put("taxon", "x");
        DataResource resource = new DataResource(vocab, record);
        assertEquals("Chicoreus palmarosae", resource.get("scientificName"));
        assertEquals("releaseForUse", resource.get("pathway"));
        assertEquals("", resource.get("oa:hasTarget"));
        // two rdf:type statements and the three mapped terms; "taxon" is left out
        assertEquals(5, resource.asModel().size());
    }
}
