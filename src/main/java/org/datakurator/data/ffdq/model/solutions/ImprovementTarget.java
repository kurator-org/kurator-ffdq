package org.datakurator.data.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/"
})
@RDFBean("ffdq:ImprovementTarget")
public class ImprovementTarget {
    private Set<ContextualizedCriterion> targetedCriterion = new HashSet<>();
    private Set<ContextualizedIssue> targetedIssues = new HashSet<>();
    private Set<ContextualizedDimension> targetedDimensions = new HashSet<>();

    private ContextualizedEnhancement improvedBy;

    private String id = "urn:uuid:" + UUID.randomUUID();

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("ffdq:targetedDimension")
    public Set<ContextualizedDimension> getTargetedDimensions() {
        return targetedDimensions;
    }

    @RDF("ffdq:targetedCriterion")
    public Set<ContextualizedCriterion> getContextualizedCriterion() {
        return targetedCriterion;
    }

    @RDF("ffdq:targetedIssue")
    public Set<ContextualizedIssue> getContextualizedIssue() {
        return targetedIssues;
    }

    @RDF("ffdq:improvedBy")
    public ContextualizedEnhancement getImprovedBy() {
        return improvedBy;
    }

    public void setTargetedDimensions(Set<ContextualizedDimension> dimensions) {
        targetedDimensions = dimensions;
    }

    public void setContextualizedCriterion(Set<ContextualizedCriterion> criteria) {
        targetedCriterion = criteria;
    }

    public void setContextualizedIssue(Set<ContextualizedIssue> issues) {
        targetedIssues = issues;
    }

    public void setImprovedBy(ContextualizedEnhancement ce) {
        improvedBy = ce;
    }

    // Convenience methods for adding to Set
    public void addContextualizedDimension(ContextualizedDimension cd) {
        targetedDimensions.add(cd);
    }

    public void addContextualizedCriterion(ContextualizedCriterion cc) {
        targetedCriterion.add(cc);
    }

}
