package org.datakurator.data.ffdq;

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
}
