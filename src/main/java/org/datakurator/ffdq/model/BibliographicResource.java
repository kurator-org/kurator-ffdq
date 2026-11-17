/**
 * BibliographicResource.java
 *
 * Copyright 2026 President and Fellows of Harvard College
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
 */
package org.datakurator.ffdq.model;

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;


import java.util.UUID;

/**
 * Represents a {@code dcterms:BibliographicResource} — an individual bibliographic
 * citation referenced by a DataQualityNeed.
 *
 * <p>Each instance carries a stable {@code urn:uuid:} URI (persisted via
 * {@link org.datakurator.ffdq.util.CitationUtils}) and the citation text as a
 * {@code dcterms:bibliographicCitation} literal.
 *
 * @author mole
 * @version $Id: $Id
 */
public class BibliographicResource implements RdfMapped {

    private String id = "urn:uuid:" + UUID.randomUUID().toString();
    private String bibliographicCitation;

    /**
     * <p>Constructor for BibliographicResource.</p>
     */
    public BibliographicResource() { }

    /**
     * <p>Constructor for BibliographicResource.</p>
     *
     * @param id                   the stable URI for this resource (e.g. {@code urn:uuid:...})
     * @param bibliographicCitation the citation text
     */
    public BibliographicResource(String id, String bibliographicCitation) {
        if (id != null && !id.isEmpty()) {
            this.id = id;
        }
        this.bibliographicCitation = bibliographicCitation;
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
     * @param id the provided id to use, ignored if null or blank
     */
    public void setId(String id) {
        if (id != null && !id.isEmpty()) {
            this.id = id;
        }
    }

    /**
     * <p>Getter for the field <code>bibliographicCitation</code>.</p>
     *
     * @return the citation text
     */
    public String getBibliographicCitation() {
        return bibliographicCitation;
    }

    /**
     * <p>Setter for the field <code>bibliographicCitation</code>.</p>
     *
     * @param bibliographicCitation the citation text to set
     */
    public void setBibliographicCitation(String bibliographicCitation) {
        this.bibliographicCitation = bibliographicCitation;
    }

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "dcterms:BibliographicResource";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "dcterms:bibliographicCitation", getBibliographicCitation());
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
        setBibliographicCitation(r.string(s, "dcterms:bibliographicCitation"));
    }

    /**
     * Read a BibliographicResource from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the BibliographicResource, the same instance each time for the same subject
     */
    public static BibliographicResource readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof BibliographicResource) { return (BibliographicResource) cached; }
        BibliographicResource o = r.remember(s, new BibliographicResource());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
