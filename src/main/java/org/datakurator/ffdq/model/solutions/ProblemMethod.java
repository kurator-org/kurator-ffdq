/** ProblemMethod.java
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
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.ContextualizedIssue;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:ProblemMethod")
public class ProblemMethod {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Specification specification;
    private ContextualizedIssue ci;

    public ProblemMethod() { }
    
    public ProblemMethod(Specification specification, ContextualizedIssue ci) { 
    	this.specification = specification;
    	this.ci = ci;
    }

    public ProblemMethod(String id) {
        this.id = id;
    }

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("bdqffdq:hasSpecification")
    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    @RDF("bdqffdq:issueInContext")
    public ContextualizedIssue getContextualizedIssue() {
        return ci;
    }

    public void setContextualizedIssue(ContextualizedIssue ci) {
        this.ci = ci;
    }
    
	/**
	 * @return a generated label
	 */
    @RDF("rdfs:label")
	public String getLabel() {
		StringBuilder labelBuilder = new StringBuilder();
		labelBuilder.append(this.getClass().getSimpleName()).append(": ");
		labelBuilder.append(ci.getLabel()).append(" with Specification ");
		labelBuilder.append(specification.getLabel());
		return labelBuilder.toString();
	}
}
