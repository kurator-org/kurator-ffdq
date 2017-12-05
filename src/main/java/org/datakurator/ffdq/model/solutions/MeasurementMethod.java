package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.context.ContextualizedDimension;
import org.datakurator.ffdq.model.Specification;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:MeasurementMethod")
public class MeasurementMethod extends AssertionMethod {
    private ContextualizedDimension cd;

    public MeasurementMethod() { }

    public MeasurementMethod(Specification specification, ContextualizedDimension contextualizedDimension) {
        this.specification = specification;
        this.cd = contextualizedDimension;
    }

    @RDF("ffdq:dimensionInContext")
    public ContextualizedDimension getContextualizedDimension() {
        return cd;
    }

    public void setContextualizedDimension(ContextualizedDimension cd) {
        this.cd = cd;
    }
}
