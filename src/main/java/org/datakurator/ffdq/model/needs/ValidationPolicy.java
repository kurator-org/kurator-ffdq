/**  ValidationPolicy.java
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
package org.datakurator.ffdq.model.needs;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.context.ContextualizedCriterion;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/ffdq/"
})
@RDFBean("ffdq:ValidationPolicy")
public class ValidationPolicy {
    private UUID uuid = UUID.randomUUID();

    private UseCase useCase;
    private ContextualizedCriterion cc;

    @RDFSubject
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    @RDF("ffdq:coversUseCase")
    public UseCase getUseCase() {
        return useCase;
    }

    public void setUseCase(UseCase useCase) {
        this.useCase = useCase;
    }

    @RDF("ffdq:criterionInContext")
    public ContextualizedCriterion getCriterionInContext() {
        return cc;
    }

    public void setCriterionInContext(ContextualizedCriterion cc) {
        this.cc = cc;
    }
}
