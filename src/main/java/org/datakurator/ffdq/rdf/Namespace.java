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
import java.util.Map;

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

    public static void set(String prefix, String uri) {
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
