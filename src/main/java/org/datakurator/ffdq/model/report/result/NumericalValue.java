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
public class NumericalValue implements ResultValue {
    private UUID uuid = UUID.randomUUID();
    private long value;

    public NumericalValue(long value) {
        this.value = value;
    }

    @RDFSubject
    public String getId() {
        return "urn:uuid" + uuid.toString();
    }

    @RDF("prov:value")
    public long getValue() {
        return value;
    }
}
