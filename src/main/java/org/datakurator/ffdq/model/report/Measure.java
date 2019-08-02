/**  Measure.java
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
package org.datakurator.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.context.ContextualizedDimension;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/ffdq/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("ffdq:Measure")
public class Measure extends Assertion {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private ContextualizedDimension dimension;
    private Amendment informedBy;

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("ffdq:dimensionInContext")
    public ContextualizedDimension getDimension() {
        return dimension;
    }

    public void setDimension(ContextualizedDimension dimension) {
        this.dimension = dimension;
    }

    @RDF("prov:wasInformedBy")
    public Amendment getInformedBy() {
        return informedBy;
    }

    public void setInformedBy(Amendment informedBy) {
        this.informedBy = informedBy;
    }
}
