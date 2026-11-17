
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

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Implementation implements RdfMapped {
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

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Implementation";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "bdqffdq:usesSpecification", getSpecification());
        w.values(s, "bdqffdq:implementedBy", getImplementedBy());
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), null);
        if (w.begin(this)) {
            w.type(s, RDF_TYPE);
            writeRdfProperties(w, s);
        }
        return s;
    }

    /**
     * Read this object's own properties (and those of its superclasses) from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
        setSpecification(r.read(s, "bdqffdq:usesSpecification", Specification::readRdf));
        setImplementedBy(new java.util.ArrayList<>(r.readAll(s, "bdqffdq:implementedBy", Mechanism::readRdf)));
    }

    /**
     * Read a Implementation from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the Implementation, the same instance each time for the same subject
     */
    public static Implementation readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Implementation) { return (Implementation) cached; }
        Implementation o = r.remember(s, new Implementation());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
