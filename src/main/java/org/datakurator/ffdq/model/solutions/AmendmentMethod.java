package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;
import org.datakurator.ffdq.model.Specification;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:AmendmentMethod")
public class AmendmentMethod extends AssertionMethod {
    private ContextualizedEnhancement ce;

    public AmendmentMethod() { }

    public AmendmentMethod(Specification specification, ContextualizedEnhancement contextualizedEnhancement) {
        this.specification = specification;
        this.ce = contextualizedEnhancement;
    }

    @RDF("ffdq:enhancementInContext")
    public ContextualizedEnhancement getContextualizedEnhancement() {
        return ce;
    }

    public void setContextualizedEnhancement(ContextualizedEnhancement ce) {
        this.ce = ce;
    }
}
