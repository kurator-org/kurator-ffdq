
/**
 *  ResourceType.java
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
package org.datakurator.ffdq.model;

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import org.datakurator.ffdq.rdf.Namespace;

import java.util.UUID;

public class ResourceType implements RdfMapped {
    /** Constant <code>SINGLE_RECORD</code> */
    public static final ResourceType SINGLE_RECORD = new ResourceType("SingleRecord");
    /** Constant <code>MULTI_RECORD</code> */
    public static final ResourceType MULTI_RECORD = new ResourceType("MultiRecord");

    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return label;
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
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLabel() {
        return label;
    }

    /**
     * <p>Setter for the field <code>label</code>.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * <p>Constructor for ResourceType.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public ResourceType(String label) {
        this.label = label;
    }

    /**
     * <p>Constructor for ResourceType.</p>
     */
    public ResourceType() { }

    /**
     * <p>fromString.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.datakurator.ffdq.model.ResourceType} object.
     */
    public static ResourceType fromString(String value) {
        String localName = Namespace.localNameFor(value);
        if (localName.equalsIgnoreCase(SINGLE_RECORD.label)) return SINGLE_RECORD;
        else if (localName.equalsIgnoreCase(MULTI_RECORD.label)) return MULTI_RECORD;
        else throw new UnsupportedOperationException("Unable to find an ffdq:ResourceType for value: " + value);
    }


    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:ResourceType";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "rdfs:label", getLabel());
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), "https://rs.tdwg.org/bdqffdq/terms/");
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
        setLabel(r.string(s, "rdfs:label"));
    }

    /**
     * Read a ResourceType from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the ResourceType, the same instance each time for the same subject
     */
    public static ResourceType readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof ResourceType) { return (ResourceType) cached; }
        ResourceType o = r.remember(s, new ResourceType());
        o.setId(RdfReader.localId(s, "https://rs.tdwg.org/bdqffdq/terms/"));
        o.readRdfProperties(r, s);
        return o;
    }

}
