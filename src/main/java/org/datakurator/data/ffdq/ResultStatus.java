package org.datakurator.data.ffdq;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("ffdq:ResultStatus")
public class ResultStatus {
    private UUID uuid;
    private String label;

    public ResultStatus(String label) {
        this.uuid = UUID.randomUUID();
        this.label = label;
    }

    @RDFSubject
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    @RDF("rdfs:label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static ResultStatus COMPLIANT = new ResultStatus("COMPLIANT");
}
