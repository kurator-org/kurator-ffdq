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
    public static ResultStatus NOT_COMPLIANT = new ResultStatus("NOT_COMPLIANT");

    public static ResultStatus COMPLETE = new ResultStatus("COMPLETE");
    public static ResultStatus NOT_COMPLETE = new ResultStatus("NOT_COMPLETE");

    public static ResultStatus CURATED = new ResultStatus("CURATED");
    public static ResultStatus FILLED_IN = new ResultStatus("FILLED_IN");
    public static ResultStatus TRANSPOSED = new ResultStatus("TRANSPOSED");
    public static ResultStatus NO_CHANGE = new ResultStatus("NO_CHANGE");

    public static ResultStatus AMBIGUOUS = new ResultStatus("AMBIGUOUS");
    public static ResultStatus DATA_PREREQUISITES_NOT_MET = new ResultStatus("DATA_PREREQUISITES_NOT_MET");
    public static ResultStatus EXTERNAL_PREREQUISITES_NOT_MET = new ResultStatus("EXTERNAL_PREREQUISITES_NOT_MET");
}
