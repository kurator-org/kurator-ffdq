
/**
 *  Assertion.java
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
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;

import java.net.URI;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("bdqffdq:Assertion")
public abstract class Assertion {
    private Specification specification;
    private Mechanism mechanism;

    private Result result;
    private URI dataResource;

    /**
     * <p>Getter for the field <code>dataResource</code>.</p>
     *
     * @return a {@link java.net.URI} object.
     */
    @RDF("bdqffdq:appliesTo")
    public URI getDataResource() {
        return dataResource;
    }

    /**
     * <p>Setter for the field <code>dataResource</code>.</p>
     *
     * @param dataResource a {@link java.net.URI} object.
     */
    public void setDataResource(URI dataResource) {
        this.dataResource = dataResource;
    }

    /**
     * <p>Getter for the field <code>specification</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.Specification} object.
     */
    @RDF("prov:hadPlan")
    public Specification getSpecification() {
        return specification;
    }

    /**
     * <p>Setter for the field <code>specification</code>.</p>
     *
     * @param specification a {@link org.datakurator.ffdq.model.Specification} object.
     */
    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    /**
     * <p>Getter for the field <code>mechanism</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.Mechanism} object.
     */
    @RDF("prov:wasAttributedTo")
    public Mechanism getMechanism() {
        return mechanism;
    }

    /**
     * <p>Setter for the field <code>mechanism</code>.</p>
     *
     * @param mechanism a {@link org.datakurator.ffdq.model.Mechanism} object.
     */
    public void setMechanism(Mechanism mechanism) {
        this.mechanism = mechanism;
    }

    /**
     * <p>Getter for the field <code>result</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.report.Result} object.
     */
    @RDF("prov:generated")
    public Result getResult() {
        return result;
    }

    /**
     * <p>Setter for the field <code>result</code>.</p>
     *
     * @param result a {@link org.datakurator.ffdq.model.report.Result} object.
     */
    public void setResult(Result result) {
        this.result = result;
    }
}
