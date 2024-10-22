
/**
 * IssueMethod.java
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
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.Specification;
import org.datakurator.ffdq.model.context.Issue;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "skos = http://www.w3.org/2004/02/skos/core#",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:IssueMethod")
public class IssueMethod extends DataQualityMethod  {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private Specification specification;
    private Issue ci;

    /**
     * <p>Constructor for IssueMethod.</p>
     */
    public IssueMethod() { }
    
    /**
     * <p>Constructor for IssueMethod.</p>
     *
     * @param specification a {@link org.datakurator.ffdq.model.Specification} object.
     * @param ci a {@link org.datakurator.ffdq.model.context.Issue} object.
     */
    public IssueMethod(Specification specification, Issue ci) { 
    	this.specification = specification;
    	this.ci = ci;
    }

    /**
     * <p>Constructor for IssueMethod.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public IssueMethod(String id) {
        this.id = id;
    }

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
     * <p>Getter for the field <code>specification</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.Specification} object.
     */
    @RDF("bdqffdq:hasSpecification")
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
     * <p>getContextualizedIssue.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.context.Issue} object.
     */
    @RDF("bdqffdq:forIssue")
    public Issue getContextualizedIssue() {
        return ci;
    }

    /**
     * <p>setContextualizedIssue.</p>
     *
     * @param ci a {@link org.datakurator.ffdq.model.context.Issue} object.
     */
    public void setContextualizedIssue(Issue ci) {
        this.ci = ci;
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
		labelBuilder.append(ci.getLabel()).append(" with Specification ");
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
