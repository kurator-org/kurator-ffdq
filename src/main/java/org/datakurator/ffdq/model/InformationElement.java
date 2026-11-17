
/**
 *  InformationElement.java
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


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InformationElement implements RdfMapped {
    private String id = "urn:uuid:" + UUID.randomUUID();
    protected List<URI> composedOf = new ArrayList<>();
    protected String label;
    

    /**
     * <p>Constructor for InformationElement.</p>
     */
    public InformationElement() { }

    /**
     * <p>Constructor for InformationElement.</p>
     *
     * @param uris a {@link java.util.List} object.
     */
    public InformationElement(List<URI> uris) {
        this.composedOf = uris;
    }

    /**
     * <p>Constructor for InformationElement.</p>
     *
     * @param uri a {@link java.net.URI} object.
     */
    public InformationElement(URI uri) {
        composedOf.add(uri);
    }

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
     * <p>Getter for the field <code>composedOf</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<URI> getComposedOf() {
        return composedOf;
    }

    /**
     * <p>Setter for the field <code>composedOf</code>.</p>
     *
     * @param composedOf a {@link java.util.List} object.
     */
    public void setComposedOf(List<URI> composedOf) {
        this.composedOf = composedOf;
    }

    /**
     * <p>addTerm.</p>
     *
     * @param uri a {@link java.net.URI} object.
     */
    public void addTerm(URI uri) {
        composedOf.add(uri);
    }

    /**
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return the label
     */
	public String getLabel() {
		return label;
	}

	/**
     * Set the preferred label, currently the rdfs;label.
	 *
	 * @param label the label to set
	 */
	public void setPrefLabel(String label) {
		this.label = label;
	}
	
    /**
     * Get the preferred label, currently, same as the rdfs;label.
     *
     * @return a skos:prefLabel
     */
    public String getPrefLabel() { 
    	return getLabel();
    }
    
	/**
	 * <p>Setter for the field <code>label</code>.</p>
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:InformationElement";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.values(s, "bdqffdq:composedOf", getComposedOf());
        w.value(s, "rdfs:label", getLabel());
        w.value(s, "skos:prefLabel", getPrefLabel());
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
        setComposedOf(new java.util.ArrayList<>(r.uris(s, "bdqffdq:composedOf")));
        setLabel(r.string(s, "rdfs:label"));
        setPrefLabel(r.string(s, "skos:prefLabel"));
    }


    /**
     * Read an information element from RDF: an ActedUpon or Consulted by its rdf:type, otherwise an InformationElement.
     *
     * @param r the reader
     * @param s the subject
     * @return the information element, the same instance each time for the same subject
     */
    public static InformationElement readRdf(RdfReader r, Resource s) {
        if (r.hasType(s, ActedUpon.RDF_TYPE)) { return ActedUpon.readRdf(r, s); }
        if (r.hasType(s, Consulted.RDF_TYPE)) { return Consulted.readRdf(r, s); }
        Object cached = r.cached(s);
        if (cached instanceof InformationElement) { return (InformationElement) cached; }
        InformationElement o = r.remember(s, new InformationElement());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
