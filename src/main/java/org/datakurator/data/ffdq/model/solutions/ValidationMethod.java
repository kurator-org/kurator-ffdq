package org.datakurator.data.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.data.ffdq.model.needs.Criterion;

import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:ValidationMethod")
public class ValidationMethod {
    private UUID uuid = UUID.randomUUID();

    private List<Specification> specifications;
    private ContextualizedCriterion cc;

    @RDFSubject
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    @RDF("ffdq:hasSpecification")
    public List<Specification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }

    @RDF("ffdq:hasContextualizedCriterion")
    public ContextualizedCriterion getContextualizedCriterion() {
        return cc;
    }

    public void setContextualizedCriterion(ContextualizedCriterion cc) {
        this.cc = cc;
    }
}
