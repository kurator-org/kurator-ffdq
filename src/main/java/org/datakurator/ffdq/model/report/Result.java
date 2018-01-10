/**  Result.java
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
package org.datakurator.ffdq.model.report;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "prov = http://www.w3.org/ns/prov#",
        "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("ffdq:Result")
public class Result<T extends ResultValue> {
    private String id = "urn:uuid:" + UUID.randomUUID();

    private ResultState resultState;
    private T resultValue;

    private Entity value;

    private String comment;
    private boolean isAmbiguous;

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("ffdq:hasAmbiguity")
    public Boolean isAmbiguous() {
        return isAmbiguous;
    }

    public void setAmbiguous(boolean ambiguous) {
        isAmbiguous = ambiguous;
    }

    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }

    @RDF("ffdq:hasState")
    public ResultState getResultState() {
        return resultState;
    }

    @RDF("ffdq:hasValue")
    public Entity getValue() {
        return this.value;
    }

    public void setValue(Entity value) {
        this.value = value;
    }

    public void setResultValue(T resultValue) {
        this.resultValue = resultValue;
        this.value = new Entity(resultValue);
    }

    public T getResultValue() {
        return resultValue;
    }

    @RDF("rdfs:comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
