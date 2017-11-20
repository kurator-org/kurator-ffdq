package org.datakurator.ffdq.model.result.validation;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.result.ResultValue;
import org.datakurator.ffdq.model.result.measure.CompletenessValue;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("prov:Entity")
public class ComplianceValue implements ResultValue {
    private UUID uuid = UUID.randomUUID();
    private String value;

    public ComplianceValue(String value) {
        this.value = value;
    }

    @RDFSubject
    public String getId() {
        return "urn:uuid" + uuid.toString();
    }

    @RDF("prov:value")
    public String getValue() {
        return value;
    }

    public static ComplianceValue COMPLIANT = new ComplianceValue("COMPLIANT");
    public static ComplianceValue NOT_COMPLIANT = new ComplianceValue("NOT_COMPLIANT");
}
