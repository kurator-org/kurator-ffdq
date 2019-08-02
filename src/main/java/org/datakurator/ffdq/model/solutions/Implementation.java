/** Implementation.java
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
package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;

import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/ffdq/"
})
@RDFBean("ffdq:Implementation")
public class Implementation {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private List<Mechanism> implementedBy;
    private Specification specification;

    public Implementation() {

    }

    public Implementation(Specification specification, List<Mechanism> implementedBy) {
        this.specification = specification;
        this.implementedBy = implementedBy;
    }

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("ffdq:hasSpecification")
    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    @RDF("ffdq:implementedBy")
    public List<Mechanism> getImplementedBy() {
        return implementedBy;
    }

    public void setImplementedBy(List<Mechanism> implementedBy) {
        this.implementedBy = implementedBy;
    }
}
