package org.datakurator.data.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.data.ffdq.model.needs.Dimension;
import org.datakurator.data.ffdq.model.needs.InformationElement;
import org.datakurator.data.ffdq.model.needs.ResourceType;

import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:ContextualizedDimension")
public class ContextualizedDimension {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Dimension dimension;
    private List<InformationElement> informationElements;
    private ResourceType rt;

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("ffdq:hasInformationElement")
    public List<InformationElement> getInformationElements() {
        return informationElements;
    }

    public void setInformationElements(List<InformationElement> informationElements) {
        this.informationElements = informationElements;
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
