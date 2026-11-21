
/**
 * Vocabulary.java
 *
 * Copyright 2018 President and Fellows of Harvard College
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
 *
 * @author mole
 * @version $Id: $Id
 */
package org.datakurator.dwcloud;

import org.apache.commons.io.FileUtils;
import org.datakurator.ffdq.rdf.Namespace;
import org.datakurator.ffdq.runner.TestRunner;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
public class Vocabulary {
    private static final String VOCAB_PROPERTY = "org.datakurator.dwcloud.vocab";
    private static final String ID_TERM_PROPERTY = "org.datakurator.dwcloud.idTerm";

    /** Current Darwin Core terms bundled with this library (from rs.tdwg.org), the fallback
     * for terms the darwin_cloud vocabulary does not list. */
    private static final String DWC_TERMS_RESOURCE = "/org/datakurator/dwcloud/dwc_terms.txt";
    private static final String DWC_NAMESPACE = "http://rs.tdwg.org/dwc/terms/";
    private static final Set<String> CURRENT_DWC_TERMS = loadCurrentDwcTerms();

    private Map<String, Integer> header;

    private String idTerm;
    private Map<String, URI> mapping = new HashMap<>();

    /**
     * <p>Constructor for Vocabulary.</p>
     *
     * @param vocabUrl a {@link java.lang.String} object.
     * @param idTerm a {@link java.lang.String} object.
     */
    public Vocabulary(String vocabUrl, String idTerm) {
        this.idTerm = idTerm;

        // Check to see if id term is of the form "prefix:term" and is resolvable to a valid namespace.
        // The following method call will throw a RuntimeException otherwise.
        Namespace.resolvePrefixedTerm(idTerm);

        try {
        	boolean usingLocalCopy = false;
        	URL urlVocabUrl = new URL(vocabUrl);
        	String vocabFilename = urlVocabUrl.getFile();
            InputStream vocabStream = null;
        	try { 
        		vocabStream = urlVocabUrl.openStream();
        	} catch (IOException e) { 
        		// if we can't access the remote file, try to see if we have a local copy to work with.
        		File vocabCache = new File(new File(vocabFilename).getName());
        		if (vocabCache.exists() && vocabCache.canRead()) { 
        			vocabStream = new FileInputStream(vocabCache);
        			usingLocalCopy = true;
        		} else { 
        			throw e;
        		}
        	}
            BufferedReader reader = new BufferedReader(new InputStreamReader(vocabStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");

                if (header == null) {
                    header = new HashMap<>();

                    for (int i = 0; i < parts.length; i++) {
                        header.put(parts[i], i);
                    }
                } else {
                    String term = parts[header.get("standard")];
                    String ns = parts[header.get("namespace")];

                    if (ns.isEmpty() || term.isEmpty()) {
                        // TODO: log a warning
                    } else {
                        mapping.put(term, Namespace.resolvePrefixedTerm(ns + ":" + term));
                    }
                }
                
            }
            
            reader.close();
            
            if (!usingLocalCopy) {
            	// store a local copy of the remote file.
        	    File vocabCache = new File(new File(vocabFilename).getName());
                FileUtils.copyURLToFile(urlVocabUrl, vocabCache);
            }
            
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: Incorrectly fromed URL for darwin cloud vocab file: " + vocabUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("Error loading mapping from darwin cloud vocab file: " + vocabUrl, e);
        }
    }

    /**
     * <p>Constructor for Vocabulary.</p>
     *
     * @param vocabFile a {@link java.io.File} object.
     * @param idTerm a {@link java.lang.String} object.
     */
    public Vocabulary(File vocabFile, String idTerm) {
        this(vocabFile.toURI().toString(), idTerm);
    }

    /**
     * <p>defaultInstance.</p>
     *
     * @return a {@link org.datakurator.dwcloud.Vocabulary} object.
     */
    public static Vocabulary defaultInstance() {
        try {
            Properties properties = new Properties();
            properties.load(TestRunner.class.getResourceAsStream("/config.properties"));

            // Get the dwcloud vocabulary and record id term (e.g. dwc:occurrenceID from config
            String vocabUrl = properties.getProperty(VOCAB_PROPERTY);
            String idTerm = properties.getProperty(ID_TERM_PROPERTY);

            return new Vocabulary(vocabUrl, idTerm);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize properties from file config.properties", e);
        }
    }

    /**
     * <p>getURI.</p>
     *
     * @param term a {@link java.lang.String} object.
     * @return a {@link java.net.URI} object.
     */
    public URI getURI(String term) {
        URI uri = findURI(term);
        if (uri != null) {
            return uri;
        } else {
            throw new RuntimeException("Unable to map term \"" + term + "\" in input data to a uri");
        }
    }

    /**
     * <p>Find the IRI of a term, or null if it cannot be mapped.</p>
     *
     * <p>The term is looked up, by its local name, in the darwin_cloud vocabulary; failing that,
     * a bare local name that is a current Darwin Core term (bundled list, from rs.tdwg.org) is
     * resolved in the Darwin Core namespace, and a prefixed term (e.g. oa:hasTarget) is
     * resolved with its prefix's namespace, if the prefix is known.</p>
     *
     * @param term a term name, bare (e.g. "pathway") or prefixed (e.g. "dwc:pathway")
     * @return the term's IRI, or null if it cannot be mapped
     */
    public URI findURI(String term) {
        if (term == null || term.isEmpty()) {
            return null;
        }
        if (term.equalsIgnoreCase("id")) {
            return Namespace.resolvePrefixedTerm(idTerm);
        }
        boolean prefixed = term.contains(":");
        String localName = prefixed ? term.substring(term.indexOf(':') + 1) : term;
        URI uri = mapping.get(localName);
        if (uri != null) {
            return uri;
        }
        if (!prefixed || term.startsWith("dwc:")) {
            if (CURRENT_DWC_TERMS.contains(localName)) {
                return URI.create(DWC_NAMESPACE + localName);
            }
            return null;
        }
        try {
            return Namespace.resolvePrefixedTerm(term);
        } catch (RuntimeException e) {
            // unknown prefix
            return null;
        }
    }

    private static Set<String> loadCurrentDwcTerms() {
        Set<String> terms = new HashSet<>();
        try (InputStream in = Vocabulary.class.getResourceAsStream(DWC_TERMS_RESOURCE)) {
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        terms.add(line);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read " + DWC_TERMS_RESOURCE, e);
        }
        return Collections.unmodifiableSet(terms);
    }

    /**
     * <p>Getter for the field <code>idTerm</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getIdTerm() {
        return idTerm.split(":")[1];
    }
}
