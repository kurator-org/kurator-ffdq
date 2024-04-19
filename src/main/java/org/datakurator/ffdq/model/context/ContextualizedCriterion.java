/** ContextualizedCriterion.java
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
 */
package org.datakurator.ffdq.model.context;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.ActedUpon;
import org.datakurator.ffdq.model.Consulted;
import org.datakurator.ffdq.model.Criterion;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.ResourceType;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/bdq/ffdq/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("ffdq:ContextualizedCriterion")
public class ContextualizedCriterion {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Criterion criterion;
    private InformationElement ie;
    private ActedUpon actedUpon;
    private Consulted consulted;    
    private ResourceType rt;
    private String label;
    private String comment;

    public ContextualizedCriterion() {

    }

    public ContextualizedCriterion(Criterion criterion, InformationElement ie, ResourceType rt) {
        this.criterion = criterion;
        this.ie = ie;
        this.rt = rt;
        label = criterion.getLabel() +  " for " + ie.toString() + " in " + rt.getLabel();
    }
    
    public ContextualizedCriterion(Criterion criterion, InformationElement ie, ResourceType rt, String label) {
        this.criterion = criterion;
        this.ie = ie;
        this.rt = rt;
        this.label = label;
    }

    public ContextualizedCriterion(Criterion criterion, InformationElement informationElement, ActedUpon actedUpon,
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

    @RDF("ffdq:hasInformationElement")
    public InformationElement getInformationElements() {
        return ie;
    }

    public void setInformationElements(InformationElement ie) {
        this.ie = ie;
    }
    
    @RDF("ffdq:hasActedUponInformationElement")
    public InformationElement getActedUpon() {
        return actedUpon;
    }

    public void setActedUpon(InformationElement ie) {
        this.actedUpon = (ActedUpon) ie;
    }   
    
    @RDF("ffdq:hasConsultedInformationElement")
    public InformationElement getConsulted() {
        return consulted;
    }

    public void setConsulted(InformationElement ie) {
        this.consulted = (Consulted) ie;
    }    

    @RDF("ffdq:hasResourceType")
    public ResourceType getResourceType() {
        return rt;
    }

    public void setResourceType(ResourceType rt) {
        this.rt = rt;
    }

    @RDF("ffdq:hasCriterion")
    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }
    
    @RDF("rdfs:label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    @RDF("rdfs:comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
