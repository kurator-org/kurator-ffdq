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

import org.datakurator.ffdq.rdf.Namespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Vocabulary {
    private Map<String, Integer> header;

    private String idTerm;
    private Map<String, URI> mapping = new HashMap<>();

    public Vocabulary(String vocabFile, String idTerm) {

        this.idTerm = idTerm;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(vocabFile).openStream()));

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
        } catch (IOException e) {
            throw new RuntimeException("Error loading mapping from darwin cloud vocab file: " + vocabFile.toString(), e);
        }
    }

    public URI getURI(String term) {
        URI uri;

        if (term.equalsIgnoreCase("id")) {
            uri = Namespace.resolvePrefixedTerm(idTerm);
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
