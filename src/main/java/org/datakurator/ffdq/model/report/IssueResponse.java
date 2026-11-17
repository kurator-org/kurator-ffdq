
/**
 *  IssueResponse.java
 *
 * Copyright 2022 President and Fellows of Harvard College
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
package org.datakurator.ffdq.model.report;

import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import org.datakurator.ffdq.model.context.Issue;

import java.util.UUID;

public class IssueResponse extends Response {
   private String id = "urn:uuid:" + UUID.randomUUID();

   private Issue issueInContext;
   private AmendmentResponse informedBy;

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
    * <p>Getter for the field <code>issueInContext</code>.</p>
    *
    * @return a {@link org.datakurator.ffdq.model.context.Issue} object.
    */
   public Issue getIssueInContext() {
       return issueInContext;
   }

   /**
    * <p>Setter for the field <code>issueInContext</code>.</p>
    *
    * @param issueInContext a {@link org.datakurator.ffdq.model.context.Issue} object.
    */
   public void setIssueInContext(Issue issueInContext) {
       this.issueInContext = issueInContext;
   }

   /**
    * <p>Getter for the field <code>informedBy</code>.</p>
    *
    * @return a {@link org.datakurator.ffdq.model.report.AmendmentResponse} object.
    */
   public AmendmentResponse getInformedBy() {
       return informedBy;
   }

   /**
    * <p>Setter for the field <code>informedBy</code>.</p>
    *
    * @param informedBy a {@link org.datakurator.ffdq.model.report.AmendmentResponse} object.
    */
   public void setInformedBy(AmendmentResponse informedBy) {
       this.informedBy = informedBy;
   }

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:IssueResponse";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        super.writeRdfProperties(w, s);
        w.value(s, "bdqffdq:hasIssue", getIssueInContext());
        w.value(s, "prov:wasInformedBy", getInformedBy());
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), null);
        if (w.begin(this)) {
            w.type(s, RDF_TYPE, "bdqffdq:Response");
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
        super.readRdfProperties(r, s);
        setIssueInContext(r.read(s, "bdqffdq:hasIssue", Issue::readRdf));
        setInformedBy(r.read(s, "prov:wasInformedBy", AmendmentResponse::readRdf));
    }

    /**
     * Read a IssueResponse from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the IssueResponse, the same instance each time for the same subject
     */
    public static IssueResponse readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof IssueResponse) { return (IssueResponse) cached; }
        IssueResponse o = r.remember(s, new IssueResponse());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
