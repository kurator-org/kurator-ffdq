package org.datakurator.ffdq.rdf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lowery on 8/1/17.
 */
public class Namespace {
    public static final String DWC = "http://rs.tdwg.org/dwc/terms/";
    public static final String DCTERMS = "http://purl.org/dc/terms/";
    public static final String DWCIRI = "http://rs.tdwg.org/dwc/iri/";

    public static final String NONE = "http://datakurator.org/none/";

    public static final Map<String, String> nsPrefixes;

    static {
        nsPrefixes = new HashMap<>();

        nsPrefixes.put("dcterms", DCTERMS);
        nsPrefixes.put("dwc", DWC);
        nsPrefixes.put("dwciri", DWCIRI);

        nsPrefixes.put("dwcloud", NONE);
        nsPrefixes.put("none", NONE);
    }

    public void set(String prefix, String uri) {
        try {
            nsPrefixes.put(prefix, new URI(uri).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid namespace uri: " + uri, e);
        }
    }


    public static URI resolvePrefixedTerm(String value) {
        if (value.indexOf(':') != -1) {
            // Split string into namespace prefix and term name
            String[] str = value.split(":");

            String ns = str[0];
            String term = str[1];

            // lookup namespace if prefix is present
            String uri = nsPrefixes.get(ns);

            if (uri != null) {
                try {
                    return new URI(uri).resolve(term);
                } catch (URISyntaxException e) {
                    throw new RuntimeException("Invalid namespace uri for information element \"" + value + ": " + uri);
                }
            } else {
                throw new RuntimeException("Could not find namespace uri for prefix: " + ns);
            }
        } else {
            throw new RuntimeException("Invalid information element \"" + value + "\". Must be of the form prefix:term ");
        }
    }
}
