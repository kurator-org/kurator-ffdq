
/**
 * ImprovementTarget.java
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
 *
 * @author mole
 * @version $Id: $Id
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

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDFSubject
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>targetedDimensions</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @RDF("bdqffdq:targetedDimension")
    public Set<Measure> getTargetedDimensions() {
        return targetedDimensions;
    }

    /**
     * <p>getContextualizedCriterion.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @RDF("bdqffdq:targetedCriterion")
    public Set<Validation> getContextualizedCriterion() {
        return targetedCriterion;
    }

    /**
     * <p>getContextualizedIssue.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @RDF("bdqffdq:targetedIssue")
    public Set<Issue> getContextualizedIssue() {
        return targetedIssues;
    }

    /**
     * <p>Getter for the field <code>improvedBy</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.context.Amendment} object.
     */
    @RDF("bdqffdq:improvedBy")
    public Amendment getImprovedBy() {
        return improvedBy;
    }

    /**
     * <p>Setter for the field <code>targetedDimensions</code>.</p>
     *
     * @param dimensions a {@link java.util.Set} object.
     */
    public void setTargetedDimensions(Set<Measure> dimensions) {
        targetedDimensions = dimensions;
    }

    /**
     * <p>setContextualizedCriterion.</p>
     *
     * @param criteria a {@link java.util.Set} object.
     */
    public void setContextualizedCriterion(Set<Validation> criteria) {
        targetedCriterion = criteria;
    }

    /**
     * <p>setContextualizedIssue.</p>
     *
     * @param issues a {@link java.util.Set} object.
     */
    public void setContextualizedIssue(Set<Issue> issues) {
        targetedIssues = issues;
    }

    /**
     * <p>Setter for the field <code>improvedBy</code>.</p>
     *
     * @param ce a {@link org.datakurator.ffdq.model.context.Amendment} object.
     */
    public void setImprovedBy(Amendment ce) {
        improvedBy = ce;
    }

    // Convenience methods for adding to Set
    /**
     * <p>addContextualizedDimension.</p>
     *
     * @param cd a {@link org.datakurator.ffdq.model.context.Measure} object.
     */
    public void addContextualizedDimension(Measure cd) {
        targetedDimensions.add(cd);
    }

    /**
     * <p>addContextualizedCriterion.</p>
     *
     * @param cc a {@link org.datakurator.ffdq.model.context.Validation} object.
     */
    public void addContextualizedCriterion(Validation cc) {
        targetedCriterion.add(cc);
    }

}
