
/**
 *  ValidationAssertion.java
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
package org.datakurator.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.context.Validation;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("bdqffdq:ValidationAssertion")
public class ValidationAssertion extends Assertion {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Validation criterion;
    private AmendmentAssertion informedBy;

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
     * <p>Getter for the field <code>criterion</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.context.Validation} object.
     */
    @RDF("bdqffdq:criterionInContext")
    public Validation getCriterion() {
        return criterion;
    }

    /**
     * <p>Setter for the field <code>criterion</code>.</p>
     *
     * @param criterion a {@link org.datakurator.ffdq.model.context.Validation} object.
     */
    public void setCriterion(Validation criterion) {
        this.criterion = criterion;
    }

    /**
     * <p>Getter for the field <code>informedBy</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.report.AmendmentAssertion} object.
     */
    @RDF("prov:wasInformedBy")
    public AmendmentAssertion getInformedBy() {
        return informedBy;
    }

    /**
     * <p>Setter for the field <code>informedBy</code>.</p>
     *
     * @param informedBy a {@link org.datakurator.ffdq.model.report.AmendmentAssertion} object.
     */
    public void setInformedBy(AmendmentAssertion informedBy) {
        this.informedBy = informedBy;
    }
}