package org.datakurator.ffdq.runner;

import org.datakurator.ffdq.rdf.Namespace;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by lowery on 8/21/17.
 */
public class Parameter {
    private URI namespace;
    private String term;

    public Parameter(String param) {

        // lookup namespace if prefix is present
        if (param.indexOf(':') != -1) {
            // Split string into namespace prefix and term name
            String[] str = param.split(":");

            String ns = str[0];
            this.term = str[1];

            try {
                // Lookup namespace and resolve URI for the term
                this.namespace = new URI(Namespace.nsPrefixes.get(ns));
            } catch (URISyntaxException e) {
                throw new RuntimeException("Unable to resolve term: " + param);
            }
        } else {
            this.term = param;
        }
    }

    public URI getURI() {
        return namespace.resolve(term);
    }

    public String getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return term;
    }
}
