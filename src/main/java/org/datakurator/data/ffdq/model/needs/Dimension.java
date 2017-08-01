/**  Dimension.java
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
package org.datakurator.data.ffdq.model.needs;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("ffdq:Dimension")
public class Dimension {
    private UUID uuid;
    private String label;

    public Dimension(String label) {
        this.uuid = UUID.randomUUID();
        this.label = label;
    }

    @RDFSubject
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    @RDF("rdfs:label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static Dimension VALUE = new Dimension("Value");
    public static Dimension VOCAB_MATCH = new Dimension("Vocab Match");
    public static Dimension COMPLETENESS = new Dimension("Completeness");
    public static Dimension ACCURACY = new Dimension("Accuracy");
    public static Dimension PRECISION = new Dimension("Precision");
    public static Dimension UNIQUENESS = new Dimension("Uniqueness");
}
