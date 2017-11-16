package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.needs.Dimension;
import org.datakurator.ffdq.model.needs.InformationElement;
import org.datakurator.ffdq.model.needs.ResourceType;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:ContextualizedDimension")
public class ContextualizedDimension {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Dimension dimension;
    private InformationElement ie;
    private ResourceType rt;

    public ContextualizedDimension() {

    }

    public ContextualizedDimension(Dimension dimension, InformationElement ie, ResourceType rt) {
        this.dimension = dimension;
        this.ie = ie;
        this.rt = rt;
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
}
