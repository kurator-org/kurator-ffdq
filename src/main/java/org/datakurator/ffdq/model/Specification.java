
/**
 *  Specification.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "skos = http://www.w3.org/2004/02/skos/core#",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:Specification")
public class Specification {
    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;
    private String description;
    private String expectedResponse;
    private String authoritiesDefaults;
    
    private List<String> examples;

    /**
     * <p>Constructor for Specification.</p>
     */
    public Specification() { 
    	init();
    }
    
    protected void init() { 
    	this.examples = new ArrayList<String>();
    }

    /**
     * <p>Constructor for Specification.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param label a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param expectedResponse a {@link java.lang.String} object.
     * @param authoritiesDefaults a {@link java.lang.String} object.
     */
    public Specification(String id, String label, String description, String expectedResponse, String authoritiesDefaults) {
    	init();
    	if (id!=null && (id.startsWith("urn:uuid") || id.startsWith("http"))) {
    		this.id = id;
    	} else { 
    		this.id = "urn:uuid:" + id;
    	}
        this.label = label;
        this.description = description;
        this.authoritiesDefaults = authoritiesDefaults;
        this.expectedResponse = expectedResponse;
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

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDF("rdfs:comment")
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
     * <p>Getter for the field <code>expectedResponse</code>.</p>
     *
     * @return the expectedResponse
     */
    @RDF("bdqffdq:hasExpectedResponse")
	public String getExpectedResponse() {
		return expectedResponse;
	}

	/**
	 * <p>Setter for the field <code>expectedResponse</code>.</p>
	 *
	 * @param expectedResponse the expectedResponse to set
	 */
	public void setExpectedResponse(String expectedResponse) {
		this.expectedResponse = expectedResponse;
	}

    /**
     * <p>Getter for the field <code>authoritiesDefaults</code>.</p>
     *
     * @return the authoritiesDefaults
     */
    @RDF("bdqffdq:hasAuthoritiesDefaults")
	public String getAuthoritiesDefaults() {
		return authoritiesDefaults;
	}

	/**
	 * <p>Setter for the field <code>authoritiesDefaults</code>.</p>
	 *
	 * @param authoritiesDefaults the authoritiesDefaults to set
	 */
	public void setAuthoritiesDefaults(String authoritiesDefaults) {
		this.authoritiesDefaults = authoritiesDefaults;
	}

	/**
	 * @return the examples
	 */
	@RDF("skos:example")
	public List<String> getExamples() {
		return examples;
	}

	/**
	 * @param example an the examples to add to the list of examples
	 */
	public void addExamples(String example) {
		if (this.examples==null) { 
			init();
		}
		if (example.matches("^\\[.*\\],\\[.*\\]$")) { 
			String[] bits = example.replaceAll("^\\[", "").replaceAll("\\]$", "").split("\\],\\[");
			if (bits == null || bits.length<2) { 
				this.examples.add(example);
			} else { 
				for (int i=0; i<bits.length; i++) { 
					this.examples.add(bits[i]);
				}
			}
		} else { 
			this.examples.add(example);
		}
	}
}
