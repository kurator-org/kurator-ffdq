
/**
 *  Dimension.java
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


import java.util.UUID;

public class DataQualityDimension implements RdfMapped {
    private String id = "bdqdim:Conformance";
    private String label;

    /**
     * <p>Constructor for DataQualityDimension.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public DataQualityDimension(String label) {
        this.label = label;
    }

    /**
     * <p>Constructor for DataQualityDimension.</p>
     */
    public DataQualityDimension() {

    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return label.toLowerCase();
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

    /** Constant <code>VALUE</code> */
    public static DataQualityDimension VALUE = new DataQualityDimension("Value");
    /** Constant <code>CONFORMANCE</code> */
    public static DataQualityDimension CONFORMANCE = new DataQualityDimension("Conformance");
    /** Constant <code>RESOLUTION</code> */
    public static DataQualityDimension RESOLUTION = new DataQualityDimension("Resolution");
    /** Constant <code>CONSISTENCY</code> */
    public static DataQualityDimension CONSISTENCY = new DataQualityDimension("Consistency");
    /** Constant <code>LIKELINESS</code> */
    public static DataQualityDimension LIKELYHOOD = new DataQualityDimension("Likelyhood");
    /** Constant <code>VOCAB_MATCH</code> */
    public static DataQualityDimension VOCAB_MATCH = new DataQualityDimension("Vocab Match");
    /** Constant <code>COMPLETENESS</code> */
    public static DataQualityDimension COMPLETENESS = new DataQualityDimension("Completeness");
    /** Constant <code>ACCURACY</code> */
    public static DataQualityDimension ACCURACY = new DataQualityDimension("Accuracy");
    /** Constant <code>PRECISION</code> */
    public static DataQualityDimension PRECISION = new DataQualityDimension("Precision");
    /** Constant <code>UNIQUENESS</code> */
    public static DataQualityDimension UNIQUENESS = new DataQualityDimension("Uniqueness");
    /** Constant <code>RELIABILITY</code> */
    public static DataQualityDimension RELIABILITY = new DataQualityDimension("Reliability");

    /**
     * <p>fromString.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.datakurator.ffdq.model.DataQualityDimension} object.
     */
    public static DataQualityDimension fromString(String value) {
        if (value.equalsIgnoreCase(VALUE.getLabel())) return VALUE;
        else if (value.equalsIgnoreCase(VOCAB_MATCH.getLabel())) return VOCAB_MATCH;
        else if (value.equalsIgnoreCase(CONFORMANCE.getLabel())) return CONFORMANCE;
        else if (value.equalsIgnoreCase(CONSISTENCY.getLabel())) return CONSISTENCY;
        else if (value.equalsIgnoreCase(RESOLUTION.getLabel())) return RESOLUTION;
        else if (value.equalsIgnoreCase(LIKELYHOOD.getLabel())) return LIKELYHOOD;
        else if (value.equalsIgnoreCase(COMPLETENESS.getLabel())) return COMPLETENESS;
        else if (value.equalsIgnoreCase(ACCURACY.getLabel())) return ACCURACY;
        else if (value.equalsIgnoreCase(PRECISION.getLabel())) return PRECISION;
        else if (value.equalsIgnoreCase(UNIQUENESS.getLabel())) return UNIQUENESS;
        else if (value.equalsIgnoreCase(RELIABILITY.getLabel())) return RELIABILITY;
        else throw new UnsupportedOperationException("Unable to find an ffdq:Dimension for value: [" + value + "]");
    }

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:DataQualityDimension";

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
        IRI s = RdfWriter.subject(getId(), "d:");
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
     * Read a DataQualityDimension from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the DataQualityDimension, the same instance each time for the same subject
     */
    public static DataQualityDimension readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof DataQualityDimension) { return (DataQualityDimension) cached; }
        DataQualityDimension o = r.remember(s, new DataQualityDimension());
        o.setId(RdfReader.localId(s, "d:"));
        o.readRdfProperties(r, s);
        if (o.getLabel() == null) {
            // no rdfs:label (e.g. the bdqffdq vocabulary is not loaded): the id is the label
            o.setLabel(RdfReader.localId(s, "d:"));
        }
        return o;
    }

}
