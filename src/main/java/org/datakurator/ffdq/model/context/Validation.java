
/**
 * Validation.java
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
package org.datakurator.ffdq.model.context;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.ActedUpon;
import org.datakurator.ffdq.model.Consulted;
import org.datakurator.ffdq.model.Criterion;
import org.datakurator.ffdq.model.Dimension;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.ResourceType;

import jdk.internal.net.http.common.Log;

import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "skos = http://www.w3.org/2004/02/skos/core#",
        "dcterms = http://purl.org/dc/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:Validation")
public class Validation extends DataQualityNeed  {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Criterion criterion;
    private Dimension dimension;
    private InformationElement ie;
    private ActedUpon actedUpon;
    private Consulted consulted;    
    private ResourceType rt;
    private String prefLabel;
    private String label;
    private String comment;

    /**
     * <p>Constructor for Validation.</p>
     */
    public Validation() {

    }

    /**
     * <p>Constructor for Validation.</p>
     *
     * @param criterion a {@link org.datakurator.ffdq.model.Criterion} object.
     * @param ie a {@link org.datakurator.ffdq.model.InformationElement} object.
     * @param rt a {@link org.datakurator.ffdq.model.ResourceType} object.
     */
    public Validation(Criterion criterion, InformationElement ie, ResourceType rt) {
        this.criterion = criterion;
        this.ie = ie;
        this.rt = rt;
        label = criterion.getLabel() +  " for " + ie.toString() + " in " + rt.getLabel();
    }
    
    /**
     * <p>Constructor for Validation.</p>
     *
     * @param criterion a {@link org.datakurator.ffdq.model.Criterion} object.
     * @param ie a {@link org.datakurator.ffdq.model.InformationElement} object.
     * @param rt a {@link org.datakurator.ffdq.model.ResourceType} object.
     * @param label a {@link java.lang.String} object.
     */
    public Validation(Criterion criterion, InformationElement ie, ResourceType rt, String label) {
        this.criterion = criterion;
        this.ie = ie;
        this.rt = rt;
        this.label = label;
    }

    /**
     * <p>Constructor for Validation.</p>
     *
     * @param criterion a {@link org.datakurator.ffdq.model.Criterion} object.
     * @param informationElement a {@link org.datakurator.ffdq.model.InformationElement} object.
     * @param actedUpon a {@link org.datakurator.ffdq.model.ActedUpon} object.
     * @param consulted a {@link org.datakurator.ffdq.model.Consulted} object.
     * @param resourceType a {@link org.datakurator.ffdq.model.ResourceType} object.
     */
    public Validation(Criterion criterion, InformationElement informationElement, ActedUpon actedUpon,
			Consulted consulted, ResourceType resourceType) {
        this.criterion = criterion;
        if (informationElement.getComposedOf().size()==0) { 
        	this.ie = null;
        } else { 
        	this.ie = informationElement;
        }
        if (actedUpon.getComposedOf().size()==0) { 
        	this.actedUpon = null;
        } else { 
        	this.actedUpon = actedUpon;
        }
        if (consulted.getComposedOf().size()==0) { 
        	this.consulted = null;
        } else { 
        	this.consulted = consulted;
        }
        this.rt = resourceType;
        StringBuilder informationElements = new StringBuilder();
        String separator = "";
        if (ie!=null) { 
        	informationElements.append(ie.toString());
        	separator = " ";
        }
        if (actedUpon!=null) { 
        	informationElements.append(separator).append(actedUpon.toString());
        	separator = " ";
        }
        if (consulted!=null) { 
        	informationElements.append(separator).append(consulted.toString());
        	separator = " ";
        }        
        label = criterion.getLabel() +  " for " + informationElements.toString() + " in " + rt.getLabel();
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
     * Override the autogenerated UUID with a provided id for this policy.
     *
     * @param id the provided id to use, ignored if null or blank
     */
    public void setId(String id) {
    	if (id!=null && id.length()>0) { 
    		this.id = id;
    	}
    }



	/**
     * <p>getInformationElements.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.InformationElement} object.
     */
    @RDF("bdqffdq:hasInformationElement")
    public InformationElement getInformationElements() {
        return ie;
    }

    /**
     * <p>setInformationElements.</p>
     *
     * @param ie a {@link org.datakurator.ffdq.model.InformationElement} object.
     */
    public void setInformationElements(InformationElement ie) {
        this.ie = ie;
    }
    
    /**
     * <p>Getter for the field <code>actedUpon</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.InformationElement} object.
     */
    @RDF("bdqffdq:hasActedUponInformationElement")
    public InformationElement getActedUpon() {
        return actedUpon;
    }

    /**
     * <p>Setter for the field <code>actedUpon</code>.</p>
     *
     * @param ie a {@link org.datakurator.ffdq.model.InformationElement} object.
     */
    public void setActedUpon(InformationElement ie) {
        this.actedUpon = (ActedUpon) ie;
    }   
    
    /**
     * <p>Getter for the field <code>consulted</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.InformationElement} object.
     */
    @RDF("bdqffdq:hasConsultedInformationElement")
    public InformationElement getConsulted() {
        return consulted;
    }

    /**
     * <p>Setter for the field <code>consulted</code>.</p>
     *
     * @param ie a {@link org.datakurator.ffdq.model.InformationElement} object.
     */
    public void setConsulted(InformationElement ie) {
        this.consulted = (Consulted) ie;
    }    

    /**
     * <p>getResourceType.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.ResourceType} object.
     */
    @RDF("bdqffdq:hasResourceType")
    public ResourceType getResourceType() {
        return rt;
    }

    /**
     * <p>setResourceType.</p>
     *
     * @param rt a {@link org.datakurator.ffdq.model.ResourceType} object.
     */
    public void setResourceType(ResourceType rt) {
        this.rt = rt;
    }

    /**
     * <p>Getter for the field <code>dimension</code>.</p>
     *
     * @return the dimension
     */
    @RDF("bdqffdq:hasDataQualityDimension")
	public Dimension getDimension() {
		return dimension;
	}

	/**
	 * <p>Setter for the field <code>dimension</code>.</p>
	 *
	 * @param dimension the dimension to set
	 */
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	/**
	 * <p>Getter for the field <code>criterion</code>.</p>
	 *
	 * @return a {@link org.datakurator.ffdq.model.Criterion} object.
	 */
	@RDF("bdqffdq:hasCriterion")
    public Criterion getCriterion() {
        return criterion;
    }

    /**
     * <p>Setter for the field <code>criterion</code>.</p>
     *
     * @param criterion a {@link org.datakurator.ffdq.model.Criterion} object.
     */
    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
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
     * <p>Getter for the field <code>comment</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDF("rdfs:comment")
	public String getComment() {
		return comment;
	}

	/**
	 * <p>Setter for the field <code>comment</code>.</p>
	 *
	 * @param comment a {@link java.lang.String} object.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * <p>Getter for the field <code>prefLabel</code>.</p>
	 *
	 * @return the prefLabel
	 */
	@RDF("skos:prefLabel")
	public String getPrefLabel() {
		return prefLabel;
	}

	/**
	 * <p>Setter for the field <code>prefLabel</code>.</p>
	 *
	 * @param prefLabel the skos:prefLabel to set
	 */
	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}
	
	/**
	 * @return the historyNote
	 */
	@RDF("skos:historyNote")
	public String getHistoryNote() {
		return super.historyNote;
	}
	
	/**
	 * @return the references as a string
	 */
	@RDF("dcterms:bibliographicCitation")
	public String getReferences() {
		return references;
	}
	
	/**
	 * @return the note
	 */
	@RDF("skos:note")
	public String getNote() {
		return note;
	}


}
