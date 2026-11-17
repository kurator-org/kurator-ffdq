
/**
 *  Result.java
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
package org.datakurator.ffdq.model.report;

import org.datakurator.ffdq.rdf.FfdqApiRdf;
import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import org.datakurator.ffdq.model.Entity;
import org.datakurator.ffdq.model.ResultState;

import java.util.UUID;

public class Result implements RdfMapped {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private ResultState state;
    private Entity value;

    private String comment;
    private boolean isAmbiguous;

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
     * <p>isAmbiguous.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean isAmbiguous() {
        return isAmbiguous;
    }

    /**
     * <p>setAmbiguous.</p>
     *
     * @param ambiguous a boolean.
     */
    public void setAmbiguous(boolean ambiguous) {
        isAmbiguous = ambiguous;
    }

    /**
     * <p>Getter for the field <code>state</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.ResultState} object.
     */
    public ResultState getState() {
        return state;
    }

    /**
     * <p>Setter for the field <code>state</code>.</p>
     *
     * @param state a {@link org.datakurator.ffdq.model.ResultState} object.
     */
    public void setState(ResultState state) {
        this.state = state;
    }

    /**
     * <p>getEntity.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.Entity} object.
     */
    public Entity getEntity() {
        return this.value;
    }

    /**
     * <p>setEntity.</p>
     *
     * @param value a {@link org.datakurator.ffdq.model.Entity} object.
     */
    public void setEntity(Entity value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>comment</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getComment() {
        return comment;
    }

    /**
     * <p>Setter for the field <code>comment</code>.</p>
     *
     * @param comment a {@link java.lang.String} object.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Result";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "bdqffdq:hasAmbiguity", isAmbiguous());
        w.value(s, "bdqffdq:hasState", getState());
        w.value(s, "bdqffdq:hasValue", getEntity());
        w.value(s, "rdfs:comment", getComment());
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
        this.isAmbiguous = r.bool(s, "bdqffdq:hasAmbiguity");
        setState(r.read(s, "bdqffdq:hasState", FfdqApiRdf::readResultState));
        setEntity(r.read(s, "bdqffdq:hasValue", FfdqApiRdf::readEntity));
        setComment(r.string(s, "rdfs:comment"));
    }

    /**
     * Read a Result from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the Result, the same instance each time for the same subject
     */
    public static Result readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Result) { return (Result) cached; }
        Result o = r.remember(s, new Result());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
