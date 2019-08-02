/** AmendmentMethod.java
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
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.ContextualizedEnhancement;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/ffdq/"
})
@RDFBean("ffdq:AmendmentMethod")
public class AmendmentMethod extends AssertionMethod {
    private ContextualizedEnhancement ce;

    public AmendmentMethod() { }

    public AmendmentMethod(Specification specification, ContextualizedEnhancement contextualizedEnhancement) {
        this.specification = specification;
        this.ce = contextualizedEnhancement;
    }

    @RDF("ffdq:enhancementInContext")
    public ContextualizedEnhancement getContextualizedEnhancement() {
        return ce;
    }

    public void setContextualizedEnhancement(ContextualizedEnhancement ce) {
        this.ce = ce;
    }
}
