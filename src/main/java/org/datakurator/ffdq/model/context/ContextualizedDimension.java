/** ContextualizedDimension.java
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
import org.datakurator.ffdq.model.Dimension;
import org.datakurator.ffdq.model.InformationElement;
import org.datakurator.ffdq.model.ResourceType;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/bdq/ffdq/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("ffdq:ContextualizedDimension")
public class ContextualizedDimension {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Dimension dimension;
    private InformationElement ie;
    private ActedUpon actedUpon;
    private Consulted consulted;
    private ResourceType rt;
    private String label;
    private String comment;

    public ContextualizedDimension() {

    }
    
    public ContextualizedDimension(Dimension dimension, InformationElement ie, ResourceType rt) {
        this.dimension = dimension;
        this.ie = ie;
        this.rt = rt;
    }    

    public ContextualizedDimension(Dimension dimension, ActedUpon actedUpon, Consulted consulted, ResourceType rt) {
        this.dimension = dimension;
        this.ie = null;
        this.rt = rt;
        this.actedUpon = actedUpon;
        this.consulted = consulted;
    }
    
    public ContextualizedDimension(Dimension dimension, InformationElement ie, ActedUpon actedUpon, Consulted consulted, ResourceType rt) {
        this.dimension = dimension;
        if (ie.getComposedOf().size()==0) {
        	this.ie = null;
        } else { 
        	this.ie = ie;
        }
        this.rt = rt;
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
    }

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @RDF("ffdq:hasDimension")
    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
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
