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
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.model.BibliographicResource;

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
     * <p>The CSV format is two quoted columns with a header row:
     * {@code "guid","citation"} followed by data rows such as
     * {@code "urn:uuid:...","normalized citation text"}
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
            CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            try {
                for (CSVRecord record : parser) {
                    String guid = record.get("guid");
                    String citation = record.get("citation");
                    if (guid != null && !guid.isEmpty()
                            && citation != null && !citation.isEmpty()) {
                        map.put(citation, guid);
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
     * <p>The CSV format is two quoted columns with a header row:
     * {@code "guid","citation"} followed by data rows such as
     * {@code "urn:uuid:...","normalized citation text"}
     * All fields are always enclosed in double quotes; any double-quote
     * characters within a field are escaped by doubling them per RFC 4180.
     *
     * <p>If {@code filePath} is null or empty, this method returns without writing.
     * If the map contains no new entries compared to {@code previousSize}, the
     * file is not overwritten and an informational message is logged instead.
     *
     * @param map          the mapping to write; must not be null
     * @param filePath     path to the output CSV file; null or empty is allowed
     * @param previousSize the number of entries that were in the map before this
     *                     run (i.e. loaded from the existing file); used to
     *                     determine whether any new entries were added
     */
    public static void saveCitationGuidMap(Map<String, String> map, String filePath,
            int previousSize) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return;
        }

        int newCount = map.size() - previousSize;
        if (newCount <= 0) {
            logger.info("Citation UUID mappings unchanged (" + map.size()
                    + " entries); file not overwritten: " + filePath);
            return;
        }

        try {
            Writer writer = new OutputStreamWriter(
                    new FileOutputStream(filePath), StandardCharsets.UTF_8);
            CSVPrinter printer = new CSVPrinter(writer,
                    CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL));
            try {
                printer.printRecord("guid", "citation");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    printer.printRecord(entry.getValue(), entry.getKey());
                }
            } finally {
                printer.close();
                writer.close();
            }
        } catch (IOException e) {
            logger.error("Could not save citation GUID map to " + filePath
                    + ": " + e.getMessage(), e);
        }

        logger.info("Saved " + map.size() + " citation UUID mappings (" + newCount
                + " new) to " + filePath);
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
     * Build a list of {@link BibliographicResource} instances from a list of
     * citation strings, assigning stable UUID URIs from the given mapping.
     *
     * <p>For each citation string, the existing URI is reused if the mapping
     * contains the normalized citation; otherwise a new {@code urn:uuid:...} URI
     * is minted and added to the mapping. The returned {@code BibliographicResource}
     * instances can be set directly on a DataQualityNeed subclass via
     * {@code setCitationResources()}, and will be serialized into the RDF model
     * as {@code dcterms:references} / {@code dcterms:BibliographicResource} nodes
     * by the normal RDFBeans annotation mechanism.
     *
     * @param citations       list of individual citation strings; null or empty
     *                        returns an empty list
     * @param citationGuidMap mutable map from normalized citation to UUID URN
     * @return list of {@link BibliographicResource} instances; never null
     */
    public static List<BibliographicResource> buildCitationResources(
            List<String> citations,
            Map<String, String> citationGuidMap) {

        List<BibliographicResource> resources = new ArrayList<BibliographicResource>();

        if (citations == null || citations.isEmpty()) {
            return resources;
        }

        for (String citation : citations) {
            String uri = getOrCreateCitationUri(citation, citationGuidMap);
            resources.add(new BibliographicResource(uri, citation));
        }

        return resources;
    }
}
