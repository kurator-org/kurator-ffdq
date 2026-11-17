
/**
 *  MeasurementResponse.java
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

import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.DataQualityNeed;
import org.datakurator.ffdq.model.solutions.DataQualityMethod;
import org.datakurator.ffdq.model.solutions.MeasurementMethod;

import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import org.datakurator.ffdq.model.context.Measure;

import java.util.UUID;

public class MeasurementResponse extends Response {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Measure dimension;
    private AmendmentResponse informedBy;

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
     * <p>Getter for the field <code>dimension</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.context.Measure} object.
     */
    public Measure getDimension() {
        return dimension;
    }

    /**
     * <p>Setter for the field <code>dimension</code>.</p>
     *
     * @param dimension a {@link org.datakurator.ffdq.model.context.Measure} object.
     */
    public void setDimension(Measure dimension) {
        this.dimension = dimension;
    }

    /**
     * <p>Getter for the field <code>informedBy</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.report.AmendmentResponse} object.
     */
    public AmendmentResponse getInformedBy() {
        return informedBy;
    }

    /**
     * <p>Setter for the field <code>informedBy</code>.</p>
     *
     * @param informedBy a {@link org.datakurator.ffdq.model.report.AmendmentResponse} object.
     */
    public void setInformedBy(AmendmentResponse informedBy) {
        this.informedBy = informedBy;
    }

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:MeasurementResponse";
    /** The RDF type kurator-ffdq wrote before 4.0.0 (bdqffdq:MeasurementResponse in the BDQ standard). */
    public static final String OLD_RDF_TYPE = "bdqffdq:MeasureResponse";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        super.writeRdfProperties(w, s);
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), null);
        if (w.begin(this)) {
            w.type(s, RDF_TYPE, "bdqffdq:Response");
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
        super.readRdfProperties(r, s);
    }

    /**
     * Read a MeasurementResponse from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the MeasurementResponse, the same instance each time for the same subject
     */
    public static MeasurementResponse readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof MeasurementResponse) { return (MeasurementResponse) cached; }
        MeasurementResponse o = r.remember(s, new MeasurementResponse());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

    /** {@inheritDoc} */
    @Override
    protected DataQualityNeed dataQualityNeed() {
        return getDimension();
    }

    /** {@inheritDoc} */
    @Override
    protected void dataQualityNeedFromRdf(DataQualityNeed need) {
        if (need instanceof Measure) {
            setDimension((Measure) need);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected DataQualityMethod newMethod(Specification specification, DataQualityNeed need) {
        return new MeasurementMethod(specification, (Measure) need);
    }

    /** {@inheritDoc} */
    @Override
    protected String methodType() {
        return MeasurementMethod.RDF_TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected DataQualityNeed needOf(DataQualityMethod method) {
        return method instanceof MeasurementMethod ? ((MeasurementMethod) method).getMeasure() : null;
    }

    /** {@inheritDoc} */
    @Override
    protected String oldTestProperty() {
        return "bdqffdq:dimensionInContext";
    }

    /** {@inheritDoc} */
    @Override
    protected DataQualityNeed readNeed(RdfReader r, Resource s) {
        return Measure.readRdf(r, s);
    }

    /** {@inheritDoc} */
    @Override
    protected AmendmentResponse informedBy() {
        return getInformedBy();
    }

    /** {@inheritDoc} */
    @Override
    protected void informedByFromRdf(AmendmentResponse amendmentResponse) {
        setInformedBy(amendmentResponse);
    }
}
