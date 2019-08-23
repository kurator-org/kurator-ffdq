/** Vocabulary.java
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
 */
package org.datakurator.dwcloud;

import org.apache.commons.io.FileUtils;
import org.datakurator.ffdq.rdf.Namespace;
import org.datakurator.ffdq.runner.TestRunner;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Vocabulary {
    private static final String VOCAB_PROPERTY = "org.datakurator.dwcloud.vocab";
    private static final String ID_TERM_PROPERTY = "org.datakurator.dwcloud.idTerm";

    private Map<String, Integer> header;

    private String idTerm;
    private Map<String, URI> mapping = new HashMap<>();

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

    public Vocabulary(File vocabFile, String idTerm) {
        this(vocabFile.toURI().toString(), idTerm);
    }

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

    public URI getURI(String term) {
        URI uri;

        if (term.equalsIgnoreCase("id")) {
            uri = Namespace.resolvePrefixedTerm(idTerm);
        } else if (term.contains(":")){
            uri = mapping.get(term.split(":")[1]);
        } else {
            uri = mapping.get(term);
        }

        if (uri != null) {
            return uri;
        } else {
            throw new RuntimeException("Unable to map term \"" + term + "\" in input data to a uri");
        }
    }

    public String getIdTerm() {
        return idTerm.split(":")[1];
    }
}
