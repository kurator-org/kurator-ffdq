
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

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Specification implements RdfMapped {
    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;
    private String description;
    private String expectedResponse;
    private String authoritiesDefaults;
    
    private List<String> examples;
    private List<Argument> arguments;

    /**
     * <p>Constructor for Specification.</p>
     */
    public Specification() { 
    	init();
    }
    
    protected void init() { 
    	this.examples = new ArrayList<String>();
    	this.arguments = new ArrayList<Argument>();
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
     * <p>Getter for the field <code>expectedResponse</code>.</p>
     *
     * @return the expectedResponse
     */
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
	
	/**
	 * @return the arguments
	 */
	public List<Argument> getArguments() {
		if (arguments == null || arguments.size()==0) {
			return null;
		} else { 
			return arguments;
		}
	}
	
	/**
	 * Add a parameter to the specification.
	 * @param argument to add 
	 */
	public void addArgument(Argument argument) { 
		if (this.arguments==null) { 
			init();
		}
		if (argument.getLabel()!=null && argument.getLabel().length()>0) { 
			this.arguments.add(argument);
		}
	}

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Specification";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "rdfs:label", getLabel());
        w.value(s, "dcterms:description", getDescription());
        w.value(s, "bdqffdq:hasExpectedResponse", getExpectedResponse());
        w.value(s, "bdqffdq:hasAuthoritiesDefaults", getAuthoritiesDefaults());
        w.values(s, "skos:example", getExamples());
        w.values(s, "bdqffdq:hasArgument", getArguments());
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
        setLabel(r.string(s, "rdfs:label"));
        setDescription(r.string(s, "dcterms:description"));
        setExpectedResponse(r.string(s, "bdqffdq:hasExpectedResponse"));
        setAuthoritiesDefaults(r.string(s, "bdqffdq:hasAuthoritiesDefaults"));
        this.examples = new java.util.ArrayList<>(r.strings(s, "skos:example"));
        this.arguments = new java.util.ArrayList<>(r.readAll(s, "bdqffdq:hasArgument", Argument::readRdf));
    }

    /**
     * Read a Specification from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the Specification, the same instance each time for the same subject
     */
    public static Specification readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Specification) { return (Specification) cached; }
        Specification o = r.remember(s, new Specification());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
