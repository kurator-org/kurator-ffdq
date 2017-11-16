package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:MeasurementMethod")
public class MeasurementMethod {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Specification specification;
    private ContextualizedDimension cd;

    public MeasurementMethod() { }

    public MeasurementMethod(String id) {
        this.id = id;
    }

    public MeasurementMethod(Specification specification, ContextualizedDimension contextualizedDimension) {
        this.specification = specification;
        this.cd = contextualizedDimension;
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

    @RDF("ffdq:dimensionInContext")
    public ContextualizedDimension getContextualizedDimension() {
        return cd;
    }

    public void setContextualizedDimension(ContextualizedDimension cd) {
        this.cd = cd;
    }
}
