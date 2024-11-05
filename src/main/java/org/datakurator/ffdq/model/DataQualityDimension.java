
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

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "bdq = https://rs.tdwg.org/bdq/terms/",
        "bdqdim = https://rs.tdwg.org/bdqdim/terms/",
        "d = https://rs.tdwg.org/bdqdim/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:DataQualityDimension")
public class DataQualityDimension {
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
    @RDFSubject(prefix = "d:")
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
}
