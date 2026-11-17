
/**
 *  UseCase.java
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
package org.datakurator.ffdq.model.needs;

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;


import java.util.UUID;

public class UseCase implements RdfMapped {
    private UUID uuid;
    private String subject;
    private String label;
    private String description;

    /**
     * <p>Constructor for UseCase.</p>
     */
    public UseCase() {
        this.uuid = UUID.randomUUID();
    }

    /**
     * <p>Constructor for UseCase.</p>
     *
     * @param subject a {@link java.lang.String} object.
     * @param label a {@link java.lang.String} object.
     */
    public UseCase(String subject, String label) {
        this.uuid = UUID.randomUUID();
        this.subject = subject;
        this.label = label;
    }
    
    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    /**
     * <p>Setter for the field <code>uuid</code>.</p>
     *
     * @param uuid a {@link java.util.UUID} object.
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>subject</code>.</p>
     *
     * @return the subject
     */
	public String getSubject() {
		return subject;
	}

	/**
	 * <p>Setter for the field <code>subject</code>.</p>
	 *
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:UseCase";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "rdfs:label", getLabel());
        w.value(s, "dcterms:description", getDescription());
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        // The subject of a UseCase is its subject (e.g. bdquc:Alien-Species), not its id.
        IRI s = RdfWriter.subject(getSubject(), null);
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
        setDescription(r.string(s, "dcterms:description"));
    }

    /**
     * Read a UseCase from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the UseCase, the same instance each time for the same subject
     */
    public static UseCase readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof UseCase) { return (UseCase) cached; }
        UseCase o = r.remember(s, new UseCase());
        // The subject as a bdquc: CURIE, as UseCases are given in the BDQ Tests (e.g. bdquc:Alien-Species).
        String iri = s.stringValue();
        o.setSubject(iri.startsWith(org.datakurator.ffdq.rdf.Namespace.BDQUC)
                ? "bdquc:" + iri.substring(org.datakurator.ffdq.rdf.Namespace.BDQUC.length()) : iri);
        o.readRdfProperties(r, s);
        return o;
    }

}
