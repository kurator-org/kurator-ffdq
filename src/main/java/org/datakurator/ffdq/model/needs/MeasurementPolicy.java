/**  MeasurementPolicy.java
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
import org.datakurator.ffdq.model.context.Measure;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:MeasurementPolicy")
public class MeasurementPolicy {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private UseCase useCase;
    private Measure cd;

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
    	if (id!=null && id.length()> 0) { 
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

    @RDF("bdqffdq:dimensionInContext")
    public Measure getDimensionInContext() {
        return cd;
    }

    public void setDimensionInContext(Measure cd) {
        this.cd = cd;
    }
    
	/**
	 * @return a generated label
	 */
    @RDF("rdfs:label")
	public String getLabel() {
		StringBuilder labelBuilder = new StringBuilder();
		labelBuilder.append("MeasurementPolicy: ");
		labelBuilder.append(cd.getLabel()).append(" in UseCase ");
		labelBuilder.append(useCase.getLabel());
		return labelBuilder.toString();
	}
}
