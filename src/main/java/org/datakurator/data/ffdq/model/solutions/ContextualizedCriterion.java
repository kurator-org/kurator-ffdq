package org.datakurator.data.ffdq.model.solutions;


import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.data.ffdq.model.needs.Criterion;
import org.datakurator.data.ffdq.model.needs.InformationElement;
import org.datakurator.data.ffdq.model.needs.ResourceType;
import org.datakurator.data.ffdq.model.report.Measure;

import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:ContextualizedCriterion")
public class ContextualizedCriterion {
    private UUID uuid = UUID.randomUUID();

    private Criterion criterion;
    private List<InformationElement> informationElements;
    private ResourceType rt;

    @RDFSubject
    public String getId() {
        return "urn:uuid:" + uuid.toString();
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

    @RDF("ffdq:hasCriterion")
    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }
}
