package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:ValidationMethod")
public class ValidationMethod {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Specification specification;
    private ContextualizedCriterion cc;

    public ValidationMethod() { }

    public ValidationMethod(String id) {
        this.id = id;
    }

    public ValidationMethod(Specification specification, ContextualizedCriterion contextualizedCriterion) {
        this.specification = specification;
        this.cc = contextualizedCriterion;
    }

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("ffdq:hasSpecification")
    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    @RDF("ffdq:criterionInContext")
    public ContextualizedCriterion getContextualizedCriterion() {
        return cc;
    }

    public void setContextualizedCriterion(ContextualizedCriterion cc) {
        this.cc = cc;
    }
}
