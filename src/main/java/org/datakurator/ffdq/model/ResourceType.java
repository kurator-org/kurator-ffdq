
/**
 *  ResourceType.java
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
package org.datakurator.ffdq.model;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
	"rdfs = http://www.w3.org/2000/01/rdf-schema#",
	"bdqffdq = https://rs.tdwg.org/bdqffdq/terms/"
})
@RDFBean("bdqffdq:ResourceType")
public class ResourceType {
    /** Constant <code>SINGLE_RECORD</code> */
    public static final ResourceType SINGLE_RECORD = new ResourceType("SingleRecord");
    /** Constant <code>MULTI_RECORD</code> */
    public static final ResourceType MULTI_RECORD = new ResourceType("MultiRecord");

    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDFSubject(prefix = "bdqffdq:")
    public String getId() {
        return label;
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
     * <p>Constructor for ResourceType.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public ResourceType(String label) {
        this.label = label;
    }

    /**
     * <p>Constructor for ResourceType.</p>
     */
    public ResourceType() { }

    /**
     * <p>fromString.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.datakurator.ffdq.model.ResourceType} object.
     */
    public static ResourceType fromString(String value) {
        if (value.equalsIgnoreCase(SINGLE_RECORD.label)) return SINGLE_RECORD;
        else if (value.equalsIgnoreCase(MULTI_RECORD.label)) return MULTI_RECORD;
        else throw new UnsupportedOperationException("Unable to find an ffdq:ResourceType for value: " + value);
    }

}
