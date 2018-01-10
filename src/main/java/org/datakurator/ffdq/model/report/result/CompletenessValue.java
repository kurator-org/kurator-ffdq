package org.datakurator.ffdq.model.report.result;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.report.ResultValue;

import java.util.UUID;

public class CompletenessValue extends ResultValue {

    private CompletenessValue(String value) {
        setValue(value);
    }

    public static CompletenessValue COMPLETE = new CompletenessValue("COMPLETE");
    public static CompletenessValue NOT_COMPLETE = new CompletenessValue("NOT_COMPLETE");
}
