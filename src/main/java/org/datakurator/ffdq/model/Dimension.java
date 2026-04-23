
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

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.rdf.Namespace;

import java.util.UUID;

/**
 * Class for data quality Dimensions.
 *
 * @author mole
 * @version $Id: $Id
 */
@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "bdqval = https://rs.tdwg.org/bdqval/terms/",
        "bdqdim = https://rs.tdwg.org/bdqdim/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:DataQualityDimension")
public class Dimension {
    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;

    /**
     * <p>Constructor for Dimension.</p>
     *
     * @param label a {@link java.lang.String} object.  May be a bare local
     *              name (e.g., {@code "Conformance"}), a CURIE (e.g.,
     *              {@code "bdqdim:Conformance"}), or a full IRI.  CURIEs and
     *              full IRIs are normalised to the bare local name so that the
     *              resulting RDF subject is a well-formed IRI.
     */
    public Dimension(String label) {
        this.label = Namespace.localNameFor(label);
    }

    /**
     * <p>Constructor for Dimension.</p>
     */
    public Dimension() {

    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDFSubject(prefix = "https://rs.tdwg.org/bdqdim/terms/")
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
    @RDF("rdfs:label")
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
    public static Dimension VALUE = new Dimension("Value");
    /** Constant <code>CONFORMANCE</code> */
    public static Dimension CONFORMANCE = new Dimension("Conformance");
    /** Constant <code>RESOLUTION</code> */
    public static Dimension RESOLUTION = new Dimension("Resolution");
    /** Constant <code>CONSISTENCY</code> */
    public static Dimension CONSISTENCY = new Dimension("Consistency");
    /** Constant <code>LIKELYNESS</code> */
    public static Dimension LIKELYNESS = new Dimension("Likeliness");
    /** Constant <code>VOCAB_MATCH</code> */
    public static Dimension VOCAB_MATCH = new Dimension("Vocab Match");
    /** Constant <code>COMPLETENESS</code> */
    public static Dimension COMPLETENESS = new Dimension("Completeness");
    /** Constant <code>ACCURACY</code> */
    public static Dimension ACCURACY = new Dimension("Accuracy");
    /** Constant <code>PRECISION</code> */
    public static Dimension PRECISION = new Dimension("Precision");
    /** Constant <code>UNIQUENESS</code> */
    public static Dimension UNIQUENESS = new Dimension("Uniqueness");
    /** Constant <code>RELIABILITY</code> */
    public static Dimension RELIABILITY = new Dimension("Reliability");

    /**
     * <p>fromString.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.datakurator.ffdq.model.Dimension} object.
     */
    public static Dimension fromString(String value) {
        String localName = Namespace.localNameFor(value);
        if (localName.equalsIgnoreCase(VALUE.getLabel())) return VALUE;
        else if (localName.equalsIgnoreCase(VOCAB_MATCH.getLabel())) return VOCAB_MATCH;
        else if (localName.equalsIgnoreCase(CONFORMANCE.getLabel())) return CONFORMANCE;
        else if (localName.equalsIgnoreCase(CONSISTENCY.getLabel())) return CONSISTENCY;
        else if (localName.equalsIgnoreCase(RESOLUTION.getLabel())) return RESOLUTION;
        else if (localName.equalsIgnoreCase(LIKELYNESS.getLabel())) return LIKELYNESS;
        else if (localName.equalsIgnoreCase(COMPLETENESS.getLabel())) return COMPLETENESS;
        else if (localName.equalsIgnoreCase(ACCURACY.getLabel())) return ACCURACY;
        else if (localName.equalsIgnoreCase(PRECISION.getLabel())) return PRECISION;
        else if (localName.equalsIgnoreCase(UNIQUENESS.getLabel())) return UNIQUENESS;
        else if (localName.equalsIgnoreCase(RELIABILITY.getLabel())) return RELIABILITY;
        else throw new UnsupportedOperationException("Unable to find an ffdq:Dimension for value: [" + value + "]");
    }
}
