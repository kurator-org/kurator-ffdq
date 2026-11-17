
/**
 *  Criterion.java
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

public class Criterion implements RdfMapped {
    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;

    /**
     * <p>Constructor for Criterion.</p>
     */
    public Criterion() { }

    /**
     * <p>Constructor for Criterion.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public Criterion(String label) {
        this.label = label;
    }
    
    /**
     * <p>Constructor for Criterion.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @param id a {@link java.lang.String} object.
     */
    public Criterion(String label, String id) {
        this.label = label;
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
    	if (id.startsWith("https://rs.tdwg.org/bdqcrit/terms/")) { 
    		return id.replace("https://rs.tdwg.org/bdqcrit/terms/", "");
    	} else { 
    		return id;
    	}
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
    
    /** Constant <code>COMPLETE</code> */
    public static Criterion COMPLETE = new Criterion("Complete","https://rs.tdwg.org/bdqcrit/terms/Complete");
    /** Constant <code>CONSISTENT</code> */
    public static Criterion CONSISTENT = new Criterion("Consistent","https://rs.tdwg.org/bdqcrit/terms/Consistent");
    /** Constant <code>FOUND</code> */
    public static Criterion FOUND = new Criterion("Found","https://rs.tdwg.org/bdqcrit/terms/Found");
    /** Constant <code>INRANGE</code> */
    public static Criterion INRANGE = new Criterion("InRange","https://rs.tdwg.org/bdqcrit/terms/InRange");
    /** Constant <code>LIKELY</code> */
    public static Criterion LIKELY = new Criterion("Likely","https://rs.tdwg.org/bdqcrit/terms/Likely");
    /** Constant <code>NOTEMPTY</code> */
    public static Criterion NOTEMPTY = new Criterion("NotEmpty","https://rs.tdwg.org/bdqcrit/terms/NotEmpty");
    /** Constant <code>STANDARD</code> */
    public static Criterion STANDARD = new Criterion("Standard","https://rs.tdwg.org/bdqcrit/terms/Standard");
    /** Constant <code>UNAMBIGUOUS</code> */
    public static Criterion UNAMBIGUOUS = new Criterion("Unambiguous","https://rs.tdwg.org/bdqcrit/terms/Unambiguous");

    /**
     * <p>fromString.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.datakurator.ffdq.model.Criterion} object.
     */
    public static Criterion fromString(String value) {
        String localName = Namespace.localNameFor(value);
        if (localName.equalsIgnoreCase(COMPLETE.getLabel())) return COMPLETE;
        else if (localName.equalsIgnoreCase(CONSISTENT.getLabel())) return CONSISTENT;
        else if (localName.equalsIgnoreCase(FOUND.getLabel())) return FOUND;
        else if (localName.equalsIgnoreCase(INRANGE.getLabel())) return INRANGE;
        else if (localName.equalsIgnoreCase(LIKELY.getLabel())) return LIKELY;
        else if (localName.equalsIgnoreCase(NOTEMPTY.getLabel())) return NOTEMPTY;
        else if (localName.equalsIgnoreCase(STANDARD.getLabel())) return STANDARD;
        else if (localName.equalsIgnoreCase(UNAMBIGUOUS.getLabel())) return UNAMBIGUOUS;
        else throw new UnsupportedOperationException("Unable to find an bdqcrit: term for bdqffdq:Criterion for value: [" + value + "]");
    }


    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Criterion";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "rdfs:label", getLabel());
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


    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), "https://rs.tdwg.org/bdqcrit/terms/");
        if (w.begin(this)) {
            w.type(s, RDF_TYPE);
            writeRdfProperties(w, s);
        }
        return s;
    }

    /**
     * Read a Criterion from RDF.
     *
     * @param r the reader
     * @param s the subject (e.g. https://rs.tdwg.org/bdqcrit/terms/Complete)
     * @return the Criterion, the same instance each time for the same subject
     */
    public static Criterion readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Criterion) { return (Criterion) cached; }
        Criterion o = r.remember(s, new Criterion());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
