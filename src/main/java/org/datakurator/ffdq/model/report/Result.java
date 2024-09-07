
/**
 *  Result.java
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
package org.datakurator.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.Entity;
import org.datakurator.ffdq.model.ResultState;

import java.util.UUID;

@RDFNamespaces({
        "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
        "prov = http://www.w3.org/ns/prov#",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:Result")
public class Result {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private ResultState state;
    private Entity value;

    private String comment;
    private boolean isAmbiguous;

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
     * <p>isAmbiguous.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    @RDF("bdqffdq:hasAmbiguity")
    public Boolean isAmbiguous() {
        return isAmbiguous;
    }

    /**
     * <p>setAmbiguous.</p>
     *
     * @param ambiguous a boolean.
     */
    public void setAmbiguous(boolean ambiguous) {
        isAmbiguous = ambiguous;
    }

    /**
     * <p>Getter for the field <code>state</code>.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.ResultState} object.
     */
    @RDF("bdqffdq:hasState")
    public ResultState getState() {
        return state;
    }

    /**
     * <p>Setter for the field <code>state</code>.</p>
     *
     * @param state a {@link org.datakurator.ffdq.model.ResultState} object.
     */
    public void setState(ResultState state) {
        this.state = state;
    }

    /**
     * <p>getEntity.</p>
     *
     * @return a {@link org.datakurator.ffdq.model.Entity} object.
     */
    @RDF("bdqffdq:hasValue")
    public Entity getEntity() {
        return this.value;
    }

    /**
     * <p>setEntity.</p>
     *
     * @param value a {@link org.datakurator.ffdq.model.Entity} object.
     */
    public void setEntity(Entity value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>comment</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDF("rdfs:comment")
    public String getComment() {
        return comment;
    }

    /**
     * <p>Setter for the field <code>comment</code>.</p>
     *
     * @param comment a {@link java.lang.String} object.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
