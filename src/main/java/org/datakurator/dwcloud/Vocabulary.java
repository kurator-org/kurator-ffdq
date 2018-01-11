package org.datakurator.dwcloud;

import org.datakurator.ffdq.rdf.Namespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
