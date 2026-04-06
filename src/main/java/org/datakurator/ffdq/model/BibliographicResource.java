/**
 * BibliographicResource.java
 *
 * Copyright 2025 President and Fellows of Harvard College
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

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

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
@RDFNamespaces({
        "dcterms = http://purl.org/dc/terms/"
})
@RDFBean("dcterms:BibliographicResource")
public class BibliographicResource {

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
    @RDFSubject
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
    @RDF("dcterms:bibliographicCitation")
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
}
