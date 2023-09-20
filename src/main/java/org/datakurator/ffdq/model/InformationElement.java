/**  InformationElement.java
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

import org.apache.commons.math3.analysis.function.Log;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/bdq/ffdq/"
})
@RDFBean("ffdq:InformationElement")
public class InformationElement {
    private String id = "urn:uuid:" + UUID.randomUUID();
    protected List<URI> composedOf = new ArrayList<>();

    public InformationElement() { }

    public InformationElement(List<URI> uris) {
        this.composedOf = uris;
    }

    public InformationElement(URI uri) {
        composedOf.add(uri);
    }

    @RDFSubject()
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("ffdq:composedOf")
    public List<URI> getComposedOf() {
        return composedOf;
    }

    public void setComposedOf(List<URI> composedOf) {
        this.composedOf = composedOf;
    }

    public void addTerm(URI uri) {
        composedOf.add(uri);
    }
}