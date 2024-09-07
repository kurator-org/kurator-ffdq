
/**
 *  AmendmentAssertion.java
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
import org.datakurator.ffdq.model.context.Amendment;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("bdqffdq:AmendmentAssertion")
public class AmendmentAssertion extends Assertion {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Amendment enhancement;

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
     * <p>Getter for the field <code>enhancement</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.context.Amendment} object.
     */
    @RDF("bdqffdq:enhancementInContext")
    public Amendment getEnhancement() {
        return enhancement;
    }

    /**
     * <p>Setter for the field <code>enhancement</code>.</p>
     *
     * @param enhancement a {@link org.datakurator.ffdq.model.context.Amendment} object.
     */
    public void setEnhancement(Amendment enhancement) {
        this.enhancement = enhancement;
    }
}
