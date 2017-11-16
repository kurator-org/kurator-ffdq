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

    public static final Map<String, String> nsPrefixes;

    static {
        nsPrefixes = new HashMap<>();

        nsPrefixes.put("dcterms", DCTERMS);
        nsPrefixes.put("dwc", DWC);
    }

    public void set(String prefix, String uri) {
        try {
            nsPrefixes.put(prefix, new URI(uri).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid namespace uri: " + uri, e);
        }
    }
}
