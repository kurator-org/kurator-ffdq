/**  Criterion.java
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
package org.datakurator.ffdq.model;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "bdqcrit = https://rs.tdwg.org/bdqcrit/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:Criterion")
public class Criterion {
    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;

    public Criterion() { }

    public Criterion(String label) {
        this.label = label;
    }
    
    public Criterion(String label, String id) {
        this.label = label;
        this.id = id;
    }

    @RDFSubject(prefix = "bdqcrit:")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("rdfs:label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public static Criterion COMPLETE = new Criterion("Complete","https://rs.tdwg.org/bdqcrit/terms/Complete");
    public static Criterion CONSISTENT = new Criterion("Consistent","https://rs.tdwg.org/bdqcrit/terms/Consistent");
    public static Criterion FOUND = new Criterion("Found","https://rs.tdwg.org/bdqcrit/terms/Found");
    public static Criterion INRANGE = new Criterion("InRange","https://rs.tdwg.org/bdqcrit/terms/InRange");
    public static Criterion LIKELY = new Criterion("Likely","https://rs.tdwg.org/bdqcrit/terms/Likely");
    public static Criterion NOTEMPTY = new Criterion("NotEmpty","https://rs.tdwg.org/bdqcrit/terms/NotEmpty");
    public static Criterion STANDARD = new Criterion("Standard","https://rs.tdwg.org/bdqcrit/terms/Standard");
    public static Criterion UNAMBIGUOUS = new Criterion("Unambiguous","https://rs.tdwg.org/bdqcrit/terms/Unambiguous");

    public static Criterion fromString(String value) {
        if (value.equalsIgnoreCase(COMPLETE.getLabel())) return COMPLETE;
        else if (value.equalsIgnoreCase(CONSISTENT.getLabel())) return CONSISTENT;
        else if (value.equalsIgnoreCase(FOUND.getLabel())) return FOUND;
        else if (value.equalsIgnoreCase(INRANGE.getLabel())) return INRANGE;
        else if (value.equalsIgnoreCase(LIKELY.getLabel())) return LIKELY;
        else if (value.equalsIgnoreCase(NOTEMPTY.getLabel())) return NOTEMPTY;
        else if (value.equalsIgnoreCase(STANDARD.getLabel())) return STANDARD;
        else if (value.equalsIgnoreCase(UNAMBIGUOUS.getLabel())) return UNAMBIGUOUS;
        else throw new UnsupportedOperationException("Unable to find an bdqcrit: term for bdqffdq:Criterion for value: [" + value + "]");
    }

}
