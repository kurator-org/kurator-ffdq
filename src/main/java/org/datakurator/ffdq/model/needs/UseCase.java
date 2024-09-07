
/**
 *  UseCase.java
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
package org.datakurator.ffdq.model.needs;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:UseCase")
public class UseCase {
    private UUID uuid;
    private String subject;
    private String label;
    private String description;

    /**
     * <p>Constructor for UseCase.</p>
     */
    public UseCase() {
        this.uuid = UUID.randomUUID();
    }

    /**
     * <p>Constructor for UseCase.</p>
     *
     * @param subject a {@link java.lang.String} object.
     * @param label a {@link java.lang.String} object.
     */
    public UseCase(String subject, String label) {
        this.uuid = UUID.randomUUID();
        this.subject = subject;
        this.label = label;
    }
    
    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return "urn:uuid:" + uuid.toString();
    }

    /**
     * <p>Setter for the field <code>uuid</code>.</p>
     *
     * @param uuid a {@link java.util.UUID} object.
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDF("rdfs:label")
    public String getLabel() {
        return label;
    }

    /**
     * <p>Setter for the field <code>label</code>.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDF("rdfs:comment")
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>subject</code>.</p>
     *
     * @return the subject
     */
    @RDFSubject()
	public String getSubject() {
		return subject;
	}

	/**
	 * <p>Setter for the field <code>subject</code>.</p>
	 *
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
