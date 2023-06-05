/** ImprovementTarget.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;
import org.datakurator.ffdq.model.context.ContextualizedDimension;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;
import org.datakurator.ffdq.model.context.ContextualizedIssue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/bdq/ffdq/"
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
