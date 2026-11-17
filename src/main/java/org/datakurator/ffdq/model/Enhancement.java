
/**
 *  Enhancement.java
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

public class Enhancement implements RdfMapped {
	
    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;

    /**
     * <p>Constructor for Enhancement.</p>
     */
    public Enhancement() { }

    /**
     * <p>Constructor for Enhancement.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public Enhancement(String label) {
        this.label = label;
    }
    
    /**
     * <p>Constructor for Enhancement.</p>
     *
     * @param label a {@link java.lang.String} object.
     * @param id a {@link java.lang.String} object.
     */
    public Enhancement(String label, String id) { 
    	this.label= label;
    	this.id = id;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
    	if (id.startsWith("https://rs.tdwg.org/bdqenh/terms/")) { 
    		return id.replace("https://rs.tdwg.org/bdqenh/terms/", "");
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
    
    /** Constant <code>ASSUMEDDEFAULT</code> */
    public static Enhancement ASSUMEDDEFAULT = new Enhancement("AssumedDefault","https://rs.tdwg.org/bdqenh/terms/AssumedDefault");
    /** Constant <code>CONVERTED</code> */
    public static Enhancement CONVERTED = new Enhancement("Converted","https://rs.tdwg.org/bdqenh/terms/Converted");
    /** Constant <code>FILLEDINFROM</code> */
    public static Enhancement FILLEDINFROM = new Enhancement("FilledInFrom","https://rs.tdwg.org/bdqenh/terms/FilledInFrom");
    /** Constant <code>STANDARDIZED</code> */
    public static Enhancement STANDARDIZED = new Enhancement("Standardized","https://rs.tdwg.org/bdqenh/terms/Standardized");
    /** Constant <code>TRANSPOSED</code> */
    public static Enhancement TRANSPOSED = new Enhancement("Transposed","https://rs.tdwg.org/bdqenh/terms/Transposed");

    /**
     * <p>fromString.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.datakurator.ffdq.model.Enhancement} object.
     */
    public static Enhancement fromString(String value) {
        String localName = Namespace.localNameFor(value);
        if (localName.equalsIgnoreCase(ASSUMEDDEFAULT.getLabel())) return ASSUMEDDEFAULT;
        else if (localName.equalsIgnoreCase(CONVERTED.getLabel())) return CONVERTED;
        else if (localName.equalsIgnoreCase(FILLEDINFROM.getLabel())) return FILLEDINFROM;
        else if (localName.equalsIgnoreCase(TRANSPOSED.getLabel())) return TRANSPOSED;
        else if (localName.equalsIgnoreCase(STANDARDIZED.getLabel())) return STANDARDIZED;
        else throw new UnsupportedOperationException("Unable to find an bdqenh: term for bdqffdq:Enhancement for value: [" + value + "]");
    }
    


    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Enhancement";

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
        IRI s = RdfWriter.subject(getId(), "https://rs.tdwg.org/bdqenh/terms/");
        if (w.begin(this)) {
            w.type(s, RDF_TYPE);
            writeRdfProperties(w, s);
        }
        return s;
    }

    /**
     * Read a Enhancement from RDF.
     *
     * @param r the reader
     * @param s the subject (e.g. https://rs.tdwg.org/bdqenh/terms/AssumedDefault)
     * @return the Enhancement, the same instance each time for the same subject
     */
    public static Enhancement readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Enhancement) { return (Enhancement) cached; }
        Enhancement o = r.remember(s, new Enhancement());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
