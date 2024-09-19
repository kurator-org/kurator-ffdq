/** Namespace.java
 *
 * Copyright 2017 President and Fellows of Harvard College
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
package org.datakurator.ffdq.rdf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Namespaces used with the FFDQModel
 *
 * @author mole
 * @version $Id: $Id
 */
public class Namespace {
    /** Constant <code>DWC="http://rs.tdwg.org/dwc/terms/"</code> */
    public static final String DWC = "http://rs.tdwg.org/dwc/terms/";
    /** Constant <code>DCTERMS="http://purl.org/dc/terms/"</code> */
    public static final String DCTERMS = "http://purl.org/dc/terms/";
    /** Constant <code>DWCIRI="http://rs.tdwg.org/dwc/iri/"</code> */
    public static final String DWCIRI = "http://rs.tdwg.org/dwc/iri/";
    /** Constant <code>DC="http://purl.org/dc/elements/1.1/"</code> */
    public static final String DC = "http://purl.org/dc/elements/1.1/";
    /** Constant <code>SKOS="http://www.w3.org/2004/02/skos/core#"</code> */
    public static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    /** Constant <code>BDQ="https://rs.tdwg.org/bdq/terms/"</code> */
    public static final String BDQ = "https://rs.tdwg.org/bdq/terms/";   // other bdq terms
    /** Constant <code>BDQDIM="https://rs.tdwg.org/bdqdim/terms/"</code> */
    public static final String BDQDIM = "https://rs.tdwg.org/bdqdim/terms/";  // dimensions   
    /** Constant <code>BDQENH="https://rs.tdwg.org/bdqenh/terms/"</code> */
    public static final String BDQENH = "https://rs.tdwg.org/bdqenh/terms/";  // enhancements   
    /** Constant <code>BDQCRIT="https://rs.tdwg.org/bdqcrit/terms/"</code> */
    public static final String BDQCRIT = "https://rs.tdwg.org/bdqcrit/terms/";  // criteria   
    /** Constant <code>BDQFFDQ="https://rs.tdwg.org/bdqffdq/terms"</code> */
    public static final String BDQFFDQ = "https://rs.tdwg.org/bdqffdq/terms";   // ontology
    /** Constant <code>BDQCORE="https://rs.tdwg.org/bdqcore/terms"</code> */
    public static final String BDQCORE = "https://rs.tdwg.org/bdqcore/terms";   // tests

    /** Constant <code>NONE="http://datakurator.org/none/"</code> */
    public static final String NONE = "http://datakurator.org/none/";

    /** Constant <code>nsPrefixes</code> */
    public static final Map<String, String> nsPrefixes;

    /**
     * Get a list of the prefixes in a form suitable for inclusion in SPARQL (PREFIX ns: namespace)
     *
     * @return a string list of prefixes used with the FFDQ model.
     */
    public static String getNamespacePrefixes() { 
        StringBuilder namespaces = new StringBuilder();
        Iterator<String> i = nsPrefixes.keySet().iterator();
        while (i.hasNext()) { 
        	String key = i.next();
        	if (!key.equalsIgnoreCase("none")) { 
        		namespaces.append("PREFIX ").append(key).append(": ").append("<").append(nsPrefixes.get(key)).append("> ");
        	}
        }
        
        return namespaces.toString();
    }
    
    static {
        nsPrefixes = new HashMap<>();

        nsPrefixes.put("dcterms", DCTERMS);
        nsPrefixes.put("dwc", DWC);
        nsPrefixes.put("dwciri", DWCIRI);
        nsPrefixes.put("dc", DC);
        
        nsPrefixes.put("bdq", BDQ);
        nsPrefixes.put("bdqdim", BDQDIM);
        nsPrefixes.put("bdqcrit", BDQCRIT);
        nsPrefixes.put("bdqenh", BDQENH);
        nsPrefixes.put("bdqcore", BDQCORE);
        nsPrefixes.put("skos", SKOS);

        nsPrefixes.put("dwcloud", NONE);
        nsPrefixes.put("none", NONE);
    }

    /**
     * <p>set.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     */
    public static void set(String prefix, String uri) {
        try {
            nsPrefixes.put(prefix, new URI(uri).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid namespace uri: " + uri, e);
        }
    }


    /**
     * <p>resolvePrefixedTerm.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a {@link java.net.URI} object.
     */
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
