package org.datakurator.ffdq.runner;

import org.datakurator.ffdq.rdf.Namespace;

import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by lowery on 8/21/17.
 */
public class TestParam {
    private final String value;
    private URI namespace;
    private String term;

    private Parameter parameter;
    private int index;

    public TestParam(String value, int index, Parameter parameter) {
        this.parameter = parameter;
        this.index = index;
        this.value = value;

        // Lookup namespace and resolve URI for the term
        this.namespace = Namespace.resolvePrefixedTerm(value);

        // Split string into namespace prefix and term name
        String[] str = value.split(":");
        this.term = str[1];
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
