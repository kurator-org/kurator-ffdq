package org.datakurator.ffdq.model.report.result;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.report.ResultValue;

import java.util.UUID;

public class ComplianceValue extends ResultValue {

    public ComplianceValue(String value) {
        setValue(value);
    }

    public static ComplianceValue COMPLIANT = new ComplianceValue("COMPLIANT");
    public static ComplianceValue NOT_COMPLIANT = new ComplianceValue("NOT_COMPLIANT");
}
