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
public class CompletenessValue implements ResultValue {
    private String id = "urn:uuid" + UUID.randomUUID();
    private Object value;

    private CompletenessValue(String value) {
        this.value = value;
    }

    public CompletenessValue() {
        // default constructor
    }

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("prov:value")
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static CompletenessValue COMPLETE = new CompletenessValue("COMPLETE");
    public static CompletenessValue NOT_COMPLETE = new CompletenessValue("NOT_COMPLETE");
}
