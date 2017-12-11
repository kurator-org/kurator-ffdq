package org.datakurator.ffdq.runner;

import org.datakurator.ffdq.rdf.Namespace;

import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by lowery on 8/21/17.
 */
public class TestParam {
    private URI namespace;
    private String term;

    private Parameter parameter;
    private int index;

    public TestParam(String value, int index, Parameter parameter) {
        this.parameter = parameter;
        this.index = index;

        // lookup namespace if prefix is present
        if (value.indexOf(':') != -1) {
            // Split string into namespace prefix and term name
            String[] str = value.split(":");

            String ns = str[0];
            this.term = str[1];

            try {
                // Lookup namespace and resolve URI for the term
                this.namespace = new URI(Namespace.nsPrefixes.get(ns));
            } catch (URISyntaxException e) {
                throw new RuntimeException("Unable to resolve term: " + value);
            }
        } else {
            this.term = value;
        }
    }

    public URI getURI() {
        return namespace.resolve(term);
    }

    public String getTerm() {
        return term;
    }

    public String getName() {
        return parameter.getName();
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return term;
    }
}
