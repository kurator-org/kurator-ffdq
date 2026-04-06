/** CitationUtils.java
 *
 * Copyright 2025 President and Fellows of Harvard College
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
package org.datakurator.ffdq.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.rdf.BaseModel;
import org.datakurator.ffdq.rdf.Namespace;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing bibliographic citations from HTML list strings
 * and managing stable UUID-based URIs for each unique citation.
 *
 * <p>Citations in the input CSV are provided as HTML lists, e.g.:
 * {@code <ul><li>Citation 1</li><li>Citation 2</li></ul>}
 *
 * <p>Each unique citation is assigned a stable {@code urn:uuid:...} URI that
 * is persisted to a mapping CSV file so the same URI is reused across rebuilds.
 *
 * <p>The mapping CSV file format is two columns, no header:
 * {@code "normalized citation text","urn:uuid:..."}
 *
 * @author mole
 * @version $Id: $Id
 */
public class CitationUtils {

    private static final Log logger = LogFactory.getLog(CitationUtils.class);

    /** Pattern matching a {@code <ul>...</ul>} wrapper (case-insensitive, dotall). */
    private static final Pattern UL_PATTERN = Pattern.compile(
            "^\\s*<ul>\\s*(.*?)\\s*</ul>\\s*$",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    /** Pattern extracting the content of each {@code <li>...</li>} element. */
    private static final Pattern LI_PATTERN = Pattern.compile(
            "<li>(.*?)</li>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    /**
     * Parse an HTML {@code <ul><li>...</li></ul>} string into a list of
     * individual citation strings. Each {@code <li>} element becomes one entry.
     *
     * <p>The following structural problems are logged as warnings and the method
     * falls back gracefully:
     * <ul>
     *   <li>Missing {@code <ul>}/{@code </ul>} wrapper — the entire string is
     *       returned as a single citation.</li>
     *   <li>Nested lists or unexpected markup — logged and processing continues.</li>
     *   <li>Empty {@code <li>} items — logged and skipped.</li>
     *   <li>No {@code <li>} items found inside a valid {@code <ul>} — logged.</li>
     * </ul>
     *
     * @param htmlList the HTML list string to parse; may be null or empty
     * @return list of normalized citation strings; never null, may be empty
     */
    public static List<String> parseReferences(String htmlList) {
        List<String> citations = new ArrayList<String>();

        if (htmlList == null || htmlList.trim().isEmpty()) {
            return citations;
        }

        String trimmed = htmlList.trim();

        Matcher ulMatcher = UL_PATTERN.matcher(trimmed);
        if (!ulMatcher.matches()) {
            logger.warn("References value does not have expected <ul>...</ul> wrapper. "
                    + "Treating entire value as a single citation: " + trimmed);
            String single = normalizeCitation(trimmed);
            if (!single.isEmpty()) {
                citations.add(single);
            }
            return citations;
        }

        String innerContent = ulMatcher.group(1);

        // Warn about nested lists
        if (innerContent.toLowerCase().contains("<ul")) {
            logger.warn("References value contains nested <ul> list, which is not supported: "
                    + trimmed);
        }

        // Extract <li> items
        Matcher liMatcher = LI_PATTERN.matcher(innerContent);
        boolean foundAny = false;
        while (liMatcher.find()) {
            foundAny = true;
            String content = liMatcher.group(1);

            if (content.toLowerCase().contains("<ul") || content.toLowerCase().contains("<li")) {
                logger.warn("Nested list markup found inside <li> element: " + content);
            }

            String normalized = normalizeCitation(content);
            if (normalized.isEmpty()) {
                logger.warn("Empty <li> item found in references list");
            } else {
                citations.add(normalized);
            }
        }

        if (!foundAny) {
            logger.warn("No <li>...</li> items found inside <ul> in references: " + trimmed);
        }

        return citations;
    }

    /**
     * Normalize a citation string for consistent storage and lookup.
     *
     * <p>Normalization trims leading/trailing whitespace and collapses all
     * internal whitespace sequences (spaces, tabs, newlines) to a single space.
     *
     * @param citation the raw citation string; may be null
     * @return normalized citation string; empty string if input is null
     */
    public static String normalizeCitation(String citation) {
        if (citation == null) {
            return "";
        }
        return citation.trim().replaceAll("\\s+", " ");
    }

    /**
     * Load a citation-to-UUID mapping from a CSV file.
     *
     * <p>The CSV format is two columns with no header row:
     * {@code "normalized citation text","urn:uuid:..."}
     *
     * <p>If {@code filePath} is null/empty or the file does not yet exist,
     * an empty map is returned and a message is logged.
     *
     * @param filePath path to the mapping CSV file; null or empty is allowed
     * @return mutable {@link LinkedHashMap} from normalized citation to UUID URN; never null
     */
    public static Map<String, String> loadCitationGuidMap(String filePath) {
        Map<String, String> map = new LinkedHashMap<String, String>();

        if (filePath == null || filePath.trim().isEmpty()) {
            return map;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("Citation GUID mapping file not found (will be created on save): "
                    + file.getAbsolutePath());
            return map;
        }

        try {
            Reader reader = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8);
            CSVParser parser = CSVFormat.DEFAULT.parse(reader);
            try {
                for (CSVRecord record : parser) {
                    if (record.size() >= 2) {
                        String citation = record.get(0);
                        String uri = record.get(1);
                        if (citation != null && !citation.isEmpty()
                                && uri != null && !uri.isEmpty()) {
                            map.put(citation, uri);
                        }
                    }
                }
            } finally {
                parser.close();
                reader.close();
            }
        } catch (IOException e) {
            logger.error("Could not load citation GUID map from " + filePath
                    + ": " + e.getMessage(), e);
        }

        logger.info("Loaded " + map.size() + " citation UUID mappings from " + filePath);
        return map;
    }

    /**
     * Save a citation-to-UUID mapping to a CSV file.
     *
     * <p>The CSV format is two columns with no header row:
     * {@code "normalized citation text","urn:uuid:..."}
     *
     * <p>If {@code filePath} is null or empty, this method returns without writing.
     *
     * @param map      the mapping to write; must not be null
     * @param filePath path to the output CSV file; null or empty is allowed
     */
    public static void saveCitationGuidMap(Map<String, String> map, String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return;
        }

        try {
            Writer writer = new OutputStreamWriter(
                    new FileOutputStream(filePath), StandardCharsets.UTF_8);
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            try {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    printer.printRecord(entry.getKey(), entry.getValue());
                }
            } finally {
                printer.close();
                writer.close();
            }
        } catch (IOException e) {
            logger.error("Could not save citation GUID map to " + filePath
                    + ": " + e.getMessage(), e);
        }

        logger.info("Saved " + map.size() + " citation UUID mappings to " + filePath);
    }

    /**
     * Return the UUID URN for a citation string, creating a new one if the
     * citation is not yet present in the mapping.
     *
     * <p>The citation is normalized before lookup and insertion. The mapping
     * is updated in-place when a new entry is created.
     *
     * @param citation        the citation string (will be normalized)
     * @param citationGuidMap mutable map from normalized citation to UUID URN
     * @return the existing or newly minted {@code urn:uuid:...} URI string
     */
    public static String getOrCreateCitationUri(String citation,
            Map<String, String> citationGuidMap) {
        String normalized = normalizeCitation(citation);
        if (citationGuidMap.containsKey(normalized)) {
            return citationGuidMap.get(normalized);
        }
        String newUri = "urn:uuid:" + UUID.randomUUID().toString();
        citationGuidMap.put(normalized, newUri);
        return newUri;
    }

    /**
     * Add {@code dcterms:references} and {@code dcterms:BibliographicResource}
     * statements to the RDF model for a DataQualityNeed instance.
     *
     * <p>For each citation string the following triples are added:
     * <ul>
     *   <li>{@code <needUri> dcterms:references <citationUri>}</li>
     *   <li>{@code <citationUri> a dcterms:BibliographicResource}</li>
     *   <li>{@code <citationUri> dcterms:bibliographicCitation "citation text"}</li>
     * </ul>
     *
     * <p>The citation URI is looked up (or created) in {@code citationGuidMap}
     * so that the same URI is reused for identical citation strings across
     * multiple Need instances.
     *
     * @param needUri         the URI string of the DataQualityNeed instance
     * @param citations       list of individual citation strings
     * @param citationGuidMap mutable map from normalized citation to UUID URN
     * @param model           the RDF model to which the statements are added
     */
    public static void addBibliographicResourcesToModel(
            String needUri,
            List<String> citations,
            Map<String, String> citationGuidMap,
            BaseModel model) {

        if (needUri == null || needUri.isEmpty()
                || citations == null || citations.isEmpty()) {
            return;
        }

        ValueFactory vf = SimpleValueFactory.getInstance();

        IRI needIRI = vf.createIRI(needUri);
        IRI dctermsReferences = vf.createIRI(Namespace.DCTERMS + "references");
        IRI rdfType = vf.createIRI(
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        IRI dctermsClass = vf.createIRI(Namespace.DCTERMS + "BibliographicResource");
        IRI dctermsBiblioCitation = vf.createIRI(
                Namespace.DCTERMS + "bibliographicCitation");

        List<Statement> statements = new ArrayList<Statement>();

        for (String citation : citations) {
            String citationUri = getOrCreateCitationUri(citation, citationGuidMap);
            IRI citationIRI = vf.createIRI(citationUri);
            statements.add(vf.createStatement(needIRI, dctermsReferences, citationIRI));
            statements.add(vf.createStatement(citationIRI, rdfType, dctermsClass));
            statements.add(vf.createStatement(
                    citationIRI, dctermsBiblioCitation, vf.createLiteral(citation)));
        }

        model.addStatements(statements);
    }
}
