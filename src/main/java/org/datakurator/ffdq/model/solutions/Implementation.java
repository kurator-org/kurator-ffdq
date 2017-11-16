package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.List;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:Implementation")
public class Implementation {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private List<Mechanism> implementedBy;
    private Specification specification;

    public Implementation() {

    }

    public Implementation(Specification specification, List<Mechanism> implementedBy) {
        this.specification = specification;
        this.implementedBy = implementedBy;
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

    @RDF("ffdq:implementedBy")
    public List<Mechanism> getImplementedBy() {
        return implementedBy;
    }

    public void setImplementedBy(List<Mechanism> implementedBy) {
        this.implementedBy = implementedBy;
    }
}
