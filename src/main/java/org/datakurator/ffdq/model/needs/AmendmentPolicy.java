/**  AmendmentPolicy.java
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
import org.datakurator.ffdq.model.context.Amendment;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:AmendmentPolicy")
public class AmendmentPolicy {
	private String id = "urn:uuid:" + UUID.randomUUID();

    private UseCase useCase;
    private Amendment ce;

    @RDFSubject
    public String getId() {
        return id;
    }
    
    /**
     * Override the autogenerated UUID with a provided id for this policy.
     * 
     * @param id the provided id to use, ignored if null or blank
     */
    public void setId(String id) {
    	if (id!=null && id.length()>0) { 
    		this.id = id;
    	}
    }

    @RDF("bdqffdq:hasUseCase")
    public UseCase getUseCase() {
        return useCase;
    }

    public void setUseCase(UseCase useCase) {
        this.useCase = useCase;
    }

    @RDF("bdqffdq:enhancementInContext")
    public Amendment getEnhancementInContext() {
        return ce;
    }

    public void setEnhancementInContext(Amendment ce) {
        this.ce = ce;
    }

	/**
	 * @return a generated label
	 */
    @RDF("rdfs:label")
	public String getLabel() {
		StringBuilder labelBuilder = new StringBuilder();
		labelBuilder.append("AmendmentPolicy: ");
		labelBuilder.append(ce.getLabel()).append(" in UseCase ");
		labelBuilder.append(useCase.getLabel());
		return labelBuilder.toString();
	}
}
