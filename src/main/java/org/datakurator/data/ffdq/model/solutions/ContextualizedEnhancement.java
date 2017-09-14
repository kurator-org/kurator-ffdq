package org.datakurator.data.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.data.ffdq.model.needs.Enhancement;
import org.datakurator.data.ffdq.model.needs.InformationElement;
import org.datakurator.data.ffdq.model.needs.ResourceType;

import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:ContextualizedEnhancement")
public class ContextualizedEnhancement {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Enhancement enhancement;
    private InformationElement ie;
    private ResourceType rt;

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

    @RDF("ffdq:hasEnhancement")
    public Enhancement getEnhancement() {
        return enhancement;
    }

    public void setEnhancement(Enhancement enhancement) {
        this.enhancement = enhancement;
    }
}
