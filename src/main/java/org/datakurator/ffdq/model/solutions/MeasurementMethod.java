/** MeasurementMethod.java
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
import org.datakurator.ffdq.model.context.ContextualizedDimension;

@RDFNamespaces({
        "ffdq = http://rs.tdwg.org/bdq/ffdq/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("ffdq:MeasurementMethod")
public class MeasurementMethod extends AssertionMethod {
    private ContextualizedDimension cd;

    public MeasurementMethod() { }

    public MeasurementMethod(Specification specification, ContextualizedDimension contextualizedDimension) {
        this.specification = specification;
        this.cd = contextualizedDimension;
    }

    @RDF("ffdq:dimensionInContext")
    public ContextualizedDimension getContextualizedDimension() {
        return cd;
    }

    public void setContextualizedDimension(ContextualizedDimension cd) {
        this.cd = cd;
    }
    
	/**
	 * @return a generated label
	 */
    @RDF("rdfs:label")
	public String getLabel() {
		StringBuilder labelBuilder = new StringBuilder();
		labelBuilder.append(this.getClass().getSimpleName()).append(": ");
		labelBuilder.append(cd.getLabel()).append(" with Specification ");
		labelBuilder.append(specification.getLabel());
		return labelBuilder.toString();
	}
}
