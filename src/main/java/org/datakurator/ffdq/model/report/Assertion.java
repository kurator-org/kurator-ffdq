/**  Assertion.java
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
import org.datakurator.ffdq.model.DataResource;
import org.datakurator.ffdq.model.Mechanism;
import org.datakurator.ffdq.model.Specification;

public abstract class Assertion {
    private Specification specification;
    private Mechanism mechanism;

    private Result result;
    private DataResource dataResource;

    @RDF("prov:used")
    public DataResource getDataResource() {
        return dataResource;
    }

    public void setDataResource(DataResource dataResource) {
        this.dataResource = dataResource;
    }

    @RDF("prov:hadPlan")
    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    @RDF("prov:wasAttributedTo")
    public Mechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(Mechanism mechanism) {
        this.mechanism = mechanism;
    }

    @RDF("prov:generated")
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
