
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

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import org.datakurator.ffdq.model.context.Validation;
import org.datakurator.ffdq.model.context.Measure;
import org.datakurator.ffdq.model.context.Amendment;
import org.datakurator.ffdq.model.context.Issue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ImprovementTarget implements RdfMapped {
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
    public Set<Measure> getTargetedDimensions() {
        return targetedDimensions;
    }

    /**
     * <p>getContextualizedCriterion.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Validation> getContextualizedCriterion() {
        return targetedCriterion;
    }

    /**
     * <p>getContextualizedIssue.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Issue> getContextualizedIssue() {
        return targetedIssues;
    }

    /**
     * <p>Getter for the field <code>improvedBy</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.context.Amendment} object.
     */
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


    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:ImprovementTarget";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.values(s, "bdqffdq:targetedMeasure", getTargetedDimensions());
        w.values(s, "bdqffdq:targetedValidation", getContextualizedCriterion());
        w.values(s, "bdqffdq:targetedIssue", getContextualizedIssue());
        // bdqffdq:improvedBy relates a bdqffdq:Amendment (subject) to a bdqffdq:ImprovementTarget (object)
        if (getImprovedBy() != null) {
            w.getModel().add((Resource) w.toValue(getImprovedBy()), RdfWriter.iri("bdqffdq:improvedBy"), s);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), null);
        if (w.begin(this)) {
            w.type(s, RDF_TYPE);
            writeRdfProperties(w, s);
        }
        return s;
    }

    /**
     * Read this object's own properties (and those of its superclasses) from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
        setTargetedDimensions(new java.util.LinkedHashSet<>(r.readAll(s, "bdqffdq:targetedMeasure", Measure::readRdf)));
        setContextualizedCriterion(new java.util.LinkedHashSet<>(r.readAll(s, "bdqffdq:targetedValidation", Validation::readRdf)));
        setContextualizedIssue(new java.util.LinkedHashSet<>(r.readAll(s, "bdqffdq:targetedIssue", Issue::readRdf)));
        Resource amendment = r.subject("bdqffdq:improvedBy", s);
        setImprovedBy(amendment == null ? null : Amendment.readRdf(r, amendment));
    }

    /**
     * Read a ImprovementTarget from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the ImprovementTarget, the same instance each time for the same subject
     */
    public static ImprovementTarget readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof ImprovementTarget) { return (ImprovementTarget) cached; }
        ImprovementTarget o = r.remember(s, new ImprovementTarget());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
