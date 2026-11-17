
/**
 *  Response.java
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

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.Resource;

import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;

import java.net.URI;

public abstract class Response implements RdfMapped {
    private Specification specification;
    private Mechanism mechanism;

    private Result result;
    private URI dataResource;

    /**
     * <p>Getter for the field <code>dataResource</code>.</p>
     *
     * @return a {@link java.net.URI} object.
     */
    public URI getDataResource() {
        return dataResource;
    }

    /**
     * <p>Setter for the field <code>dataResource</code>.</p>
     *
     * @param dataResource a {@link java.net.URI} object.
     */
    public void setDataResource(URI dataResource) {
        this.dataResource = dataResource;
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
     * <p>Getter for the field <code>mechanism</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.Mechanism} object.
     */
    public Mechanism getMechanism() {
        return mechanism;
    }

    /**
     * <p>Setter for the field <code>mechanism</code>.</p>
     *
     * @param mechanism a {@link org.datakurator.ffdq.model.Mechanism} object.
     */
    public void setMechanism(Mechanism mechanism) {
        this.mechanism = mechanism;
    }

    /**
     * <p>Getter for the field <code>result</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.report.Result} object.
     */
    public Result getResult() {
        return result;
    }

    /**
     * <p>Setter for the field <code>result</code>.</p>
     *
     * @param result a {@link org.datakurator.ffdq.model.report.Result} object.
     */
    public void setResult(Result result) {
        this.result = result;
    }

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Response";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "bdqffdq:appliesTo", getDataResource());
        w.value(s, "prov:hadPlan", getSpecification());
        w.value(s, "prov:wasAttributedTo", getMechanism());
        w.value(s, "prov:generated", getResult());
    }

    /**
     * Read this object's own properties (and those of its superclasses) from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
        setDataResource(r.uri(s, "bdqffdq:appliesTo"));
        setSpecification(r.read(s, "prov:hadPlan", Specification::readRdf));
        setMechanism(r.read(s, "prov:wasAttributedTo", Mechanism::readRdf));
        setResult(r.read(s, "prov:generated", Result::readRdf));
    }


    /**
     * Read a ValidationResponse, IssueResponse, MeasureResponse, or AmendmentResponse from RDF, by its rdf:type.
     *
     * @param r the reader
     * @param s the subject
     * @return the Response, or null if the subject has none of those types
     */
    public static Response readRdf(RdfReader r, Resource s) {
        if (r.hasType(s, ValidationResponse.RDF_TYPE)) { return ValidationResponse.readRdf(r, s); }
        if (r.hasType(s, IssueResponse.RDF_TYPE)) { return IssueResponse.readRdf(r, s); }
        if (r.hasType(s, MeasureResponse.RDF_TYPE)) { return MeasureResponse.readRdf(r, s); }
        if (r.hasType(s, AmendmentResponse.RDF_TYPE)) { return AmendmentResponse.readRdf(r, s); }
        return null;
    }

}
