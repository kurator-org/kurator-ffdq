
/**
 * Implementation.java
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
 *
 * @author mole
 * @version $Id: $Id
 */
package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/"
})
@RDFBean("bdqffdq:Implementation")
public class Implementation {
    private String id = "urn:uuid:" + UUID.randomUUID();

    /**
     * Mechanism implementing this specification.
     * 
     * The implementedBy list SHOULD contain only one mechanism.
     */
    private List<Mechanism> implementedBy;
    private Specification specification;

    /**
     * <p>Constructor for Implementation.</p>
     */
    public Implementation() {

    }

    /**
     * <p>Constructor for Implementation.</p>
     *
     * @param specification a {@link org.datakurator.ffdq.model.Specification} object.
     * @param implementedBy a {@link java.util.List} object which should be created with Collections.singletonList(mechanism) 
     */
    public Implementation(Specification specification, List<Mechanism> implementedBy) {
        this.specification = specification;
        this.implementedBy = implementedBy;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDFSubject
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>specification</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.Specification} object.
     */
    @RDF("bdqffdq:usesSpecification")
    public Specification getSpecification() {
        return specification;
    }

    /**
     * <p>Setter for the field <code>specification</code>.</p>
     *
     * @param specification a {@link org.datakurator.ffdq.model.Specification} object.
     */
    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    /**
     * <p>Getter for the field <code>implementedBy</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    @RDF("bdqffdq:implementedBy")
    public List<Mechanism> getImplementedBy() {
        return implementedBy;
    }

    /**
     * Setter for the field implementedBy, specifying the
     * Mechanism implementing the implementation. 
     * The provided list of mechanisms should be created with
     * Collections.singletonList(mechanism). 
     *
     * @param implementedBy a {@link java.util.List} object.
     */
    public void setImplementedBy(List<Mechanism> implementedBy) {
        this.implementedBy = implementedBy;
    }
}
