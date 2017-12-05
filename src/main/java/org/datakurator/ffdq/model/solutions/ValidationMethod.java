package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.Specification;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:ValidationMethod")
public class ValidationMethod extends AssertionMethod {
    private ContextualizedCriterion cc;

    public ValidationMethod() { }

    public ValidationMethod(Specification specification, ContextualizedCriterion contextualizedCriterion) {
        this.specification = specification;
        this.cc = contextualizedCriterion;
    }

    @RDF("ffdq:criterionInContext")
    public ContextualizedCriterion getContextualizedCriterion() {
        return cc;
    }

    public void setContextualizedCriterion(ContextualizedCriterion cc) {
        this.cc = cc;
    }
}
