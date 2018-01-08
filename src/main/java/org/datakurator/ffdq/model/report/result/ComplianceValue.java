package org.datakurator.ffdq.model.report.result;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.report.ResultValue;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("prov:Entity")
public class ComplianceValue implements ResultValue {
    private String id = "urn:uuid" + UUID.randomUUID();
    private String value;

    public ComplianceValue(String value) {
        this.value = value;
    }

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("prov:value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ComplianceValue COMPLIANT = new ComplianceValue("COMPLIANT");
    public static ComplianceValue NOT_COMPLIANT = new ComplianceValue("NOT_COMPLIANT");
}
