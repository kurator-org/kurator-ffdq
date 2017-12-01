/**  DQAmendment.java
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
package org.datakurator.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.DataResource;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("ffdq:Amendment")
public class Amendment extends Assertion {
    private UUID uuid = UUID.randomUUID();

    private ContextualizedEnhancement enhancement;

    @RDFSubject
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    @RDF("ffdq:enhancementInContext")
    public ContextualizedEnhancement getEnhancement() {
        return enhancement;
    }

    public void setEnhancement(ContextualizedEnhancement enhancement) {
        this.enhancement = enhancement;
    }
}