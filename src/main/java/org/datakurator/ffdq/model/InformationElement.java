
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

import org.apache.commons.math3.analysis.function.Log;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "skos = http://www.w3.org/2004/02/skos/core#",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:InformationElement")
public class InformationElement {
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
    @RDFSubject()
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
    @RDF("bdqffdq:composedOf")
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
    @RDF("rdfs:label")
	public String getLabel() {
		return label;
	}

	/**
	 * <p>Setter for the field <code>label</code>.</p>
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
    /**
     * Get the preferred label, currently, same as the rdfs;label.
     *
     * @return a skos:prefLabel
     */
    @RDF("skos:prefLabel") 
    public String getPrefLabel() { 
    	return getLabel();
    }
}
