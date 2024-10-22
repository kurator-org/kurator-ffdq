
/**
 * ValidationMethod.java
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
package org.datakurator.ffdq.model.solutions;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.Validation;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "skos = http://www.w3.org/2004/02/skos/core#",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:ValidationMethod")
public class ValidationMethod extends DataQualityMethod {
    private Validation cc;

    /**
     * <p>Constructor for ValidationMethod.</p>
     */
    public ValidationMethod() { }

    /**
     * <p>Constructor for ValidationMethod.</p>
     *
     * @param specification a {@link org.datakurator.ffdq.model.Specification} object.
     * @param contextualizedCriterion a {@link org.datakurator.ffdq.model.context.Validation} object.
     */
    public ValidationMethod(Specification specification, Validation contextualizedCriterion) {
        this.specification = specification;
        this.cc = contextualizedCriterion;
    }

    /**
     * <p>getValidation.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.context.Validation} object.
     */
    @RDF("bdqffdq:forValidation")
    public Validation getValidation() {
        return cc;
    }

    /**
     * <p>setContextualizedCriterion.</p>
     *
     * @param cc a {@link org.datakurator.ffdq.model.context.Validation} object.
     */
    public void setContextualizedCriterion(Validation cc) {
        this.cc = cc;
    }
    
    /**
     * <p>getLabel.</p>
     *
     * @return a generated label
     */
    @RDF("rdfs:label")
	public String getLabel() {
		StringBuilder labelBuilder = new StringBuilder();
		labelBuilder.append(this.getClass().getSimpleName()).append(": ");
		labelBuilder.append(cc.getLabel()).append(" with ");
		labelBuilder.append(specification.getLabel());
		return labelBuilder.toString();
	}
    
    /**
     * Get the preferred label, currently, same as the rdfs;label.
     *
     * @return a skos:prefLabel
     */
    @RDF("skos:prefLabel") 
    public String getPrefLabel() { 
    	return getLabel();
    }
    
}
