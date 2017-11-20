package org.datakurator.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#",
        "rs = http://example.com/ffdq/rs/"
})
@RDFBean("ffdq:ResultState")
public class ResultState {
    private String id;
    private String label;

    public ResultState(String label) {
        this.id = label.toLowerCase();
        this.label = label;
    }

    @RDFSubject(prefix = "rs:")
    public String getId() {
        return id;
    }

    @RDF("rdfs:label")
    public String getLabel() {
        return label;
    }

    public static ResultState RUN_HAS_RESULT = new ResultState("HAS_RESULT");
    public static ResultState NOT_RUN = new ResultState("NOT_RUN");
    public static ResultState AMBIGUOUS = new ResultState("AMBIGUOUS");
    public static ResultState UNABLE_CURATE = new ResultState("UNABLE_CURATE");
    public static ResultState INTERNAL_PREREQUISITES_NOT_MET = new ResultState("DATA_PREREQUISITES_NOT_MET");
    public static ResultState EXTERNAL_PREREQUISITES_NOT_MET = new ResultState("EXTERNAL_PREREQUISITES_NOT_MET");

    public static ResultState CHANGED = new ResultState("CHANGED");
    public static ResultState FILLED_IN = new ResultState("FILLED_IN");
    public static ResultState TRANSPOSED = new ResultState("TRANSPOSED");
    public static ResultState NO_CHANGE = new ResultState("NO_CHANGE");
}
