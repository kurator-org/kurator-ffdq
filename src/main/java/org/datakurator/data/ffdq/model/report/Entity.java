package org.datakurator.data.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.data.ffdq.model.needs.InformationElement;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("prov:Entity")
public class Entity {
    private UUID uuid = UUID.randomUUID();

    private String value;
    private InformationElement informationElement;

    @RDFSubject
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    @RDF("prov:value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @RDF("ffdq:hasInformationElement")
    public InformationElement getInformationElement() {
        return informationElement;
    }

    public void setInformationElement(InformationElement informationElement) {
        this.informationElement = informationElement;
    }
}
