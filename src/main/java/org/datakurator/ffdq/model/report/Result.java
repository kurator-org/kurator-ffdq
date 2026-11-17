
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
import org.datakurator.ffdq.rdf.RdfReader;
import org.eclipse.rdf4j.model.Resource;

import org.datakurator.ffdq.model.Entity;
import org.datakurator.ffdq.model.ResponseQualifier;
import org.datakurator.ffdq.model.ResultState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The outcome of a Test: its Response.status ({@link ResultState}), Response.result (the value of
 * an {@link Entity}), Response.comment, and Response.qualifiers.
 *
 * In RDF these are properties of the {@link Response} (bdqffdq:hasResponseStatus,
 * bdqffdq:hasResponseResult or bdqffdq:hasResponseResultValue, bdqffdq:hasResponseComment, and
 * bdqffdq:hasResponseQualifier), as the BDQ standard has them; a Result is not written as a
 * resource of its own.  {@link #readRdf(RdfReader, Resource)} reads the bdqffdq:Result resources
 * that kurator-ffdq wrote before 4.0.0.
 */
public class Result {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private ResultState state;
    private Entity value;

    private String comment;
    private boolean isAmbiguous;
    private List<ResponseQualifier> qualifiers = new ArrayList<>();

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

    /**
     * @return the Response.qualifiers (bdqffdq:ResponseQualifier) of this result
     */
    public List<ResponseQualifier> getQualifiers() {
        return qualifiers;
    }

    /**
     * @param qualifiers the Response.qualifiers of this result
     */
    public void setQualifiers(List<ResponseQualifier> qualifiers) {
        this.qualifiers = qualifiers == null ? new ArrayList<>() : new ArrayList<>(qualifiers);
    }

    // ---- RDF: reading the form written before 4.0.0 ----

    /** The RDF type of a Result as kurator-ffdq wrote it before 4.0.0 (not a bdqffdq term). */
    public static final String OLD_RDF_TYPE = "bdqffdq:Result";

    /**
     * Read a Result in the form kurator-ffdq wrote before 4.0.0: a bdqffdq:Result resource with
     * bdqffdq:hasState, bdqffdq:hasValue, bdqffdq:hasAmbiguity, and rdfs:comment.
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
        Boolean ambiguous = r.bool(s, "bdqffdq:hasAmbiguity");
        o.isAmbiguous = ambiguous != null && ambiguous;
        o.setState(r.read(s, "bdqffdq:hasState", FfdqApiRdf::readResultState));
        o.setEntity(r.read(s, "bdqffdq:hasValue", FfdqApiRdf::readEntity));
        o.setComment(r.string(s, "rdfs:comment"));
        return o;
    }
}
