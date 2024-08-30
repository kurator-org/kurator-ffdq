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
import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.context.Measure;
import org.datakurator.ffdq.model.context.Amendment;
import org.datakurator.ffdq.model.context.Issue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/"
})
@RDFBean("bdqffdq:ImprovementTarget")
public class ImprovementTarget {
    private Set<Validation> targetedCriterion = new HashSet<>();
    private Set<Issue> targetedIssues = new HashSet<>();
    private Set<Measure> targetedDimensions = new HashSet<>();

    private Amendment improvedBy;

    private String id = "urn:uuid:" + UUID.randomUUID();

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("bdqffdq:targetedDimension")
    public Set<Measure> getTargetedDimensions() {
        return targetedDimensions;
    }

    @RDF("bdqffdq:targetedCriterion")
    public Set<Validation> getContextualizedCriterion() {
        return targetedCriterion;
    }

    @RDF("bdqffdq:targetedIssue")
    public Set<Issue> getContextualizedIssue() {
        return targetedIssues;
    }

    @RDF("bdqffdq:improvedBy")
    public Amendment getImprovedBy() {
        return improvedBy;
    }

    public void setTargetedDimensions(Set<Measure> dimensions) {
        targetedDimensions = dimensions;
    }

    public void setContextualizedCriterion(Set<Validation> criteria) {
        targetedCriterion = criteria;
    }

    public void setContextualizedIssue(Set<Issue> issues) {
        targetedIssues = issues;
    }

    public void setImprovedBy(Amendment ce) {
        improvedBy = ce;
    }

    // Convenience methods for adding to Set
    public void addContextualizedDimension(Measure cd) {
        targetedDimensions.add(cd);
    }

    public void addContextualizedCriterion(Validation cc) {
        targetedCriterion.add(cc);
    }

}
