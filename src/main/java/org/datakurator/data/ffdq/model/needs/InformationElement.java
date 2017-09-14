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
package org.datakurator.data.ffdq.model.needs;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "ie = http://example.com/ffdq/ie"
})
@RDFBean("ffdq:InformationElement")
public class InformationElement {
    private String id = "urn:uuid:" + UUID.randomUUID();
    private URI composedOf;

    public InformationElement() { }

    public InformationElement(String uri) throws URISyntaxException {
        this.composedOf = new URI(uri);
    }

    public InformationElement(URI uri) {
        this.composedOf = uri;
    }


    @RDFSubject(prefix = "ie:")
    public String getId() {
        String path = composedOf.getPath();
        return path;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("ffdq:composedOf")
    public URI getComposedOf() {
        return composedOf;
    }

    public void setComposedOf(URI composedOf) {
        this.composedOf = composedOf;
    }
}