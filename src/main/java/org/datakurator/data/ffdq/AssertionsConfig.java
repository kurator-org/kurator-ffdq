/**  AssertionsConfig.java
 *
 * Copyright 2016 President and Fellows of Harvard College
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

package org.datakurator.data.ffdq;

import org.datakurator.data.ffdq.assertions.DQAssertion;
import org.datakurator.data.ffdq.assertions.DQImprovement;
import org.datakurator.data.ffdq.assertions.DQMeasure;
import org.datakurator.data.ffdq.assertions.DQValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration encapsulations the definitions for a set of FFDQ assertions.
 *
 * @author lowery
 */
public class AssertionsConfig {
    private Map<String, DQAssertion> assertionMap = new HashMap<>();

    private List<DQMeasure> measures = new ArrayList<>();
    private List<DQValidation> validations = new ArrayList<>();
    private List<DQImprovement> improvements = new ArrayList<>();

    public AssertionsConfig() { }

    public List<DQMeasure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<DQMeasure> measures) {
        this.measures = measures;
        mapAssertions(measures);
    }

    private void mapAssertions(List<? extends DQAssertion> assertions) {
        for (DQAssertion assertion : assertions) {
            assertionMap.put(assertion.getContext().getName(), assertion);
        }
    }

    public List<DQValidation> getValidations() {
        return validations;
    }

    public void setValidations(List<DQValidation> validations) {
        this.validations = validations;
        mapAssertions(validations);
    }

    public List<DQImprovement> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<DQImprovement> improvements) {
        this.improvements = improvements;
        mapAssertions(improvements);
    }

    public DQAssertion forContext(String name) {
        // TODO: This functionality needs to be refactored to be less fragile. Use the factory pattern instead.
        DQAssertion assertion = assertionMap.get(name);

        if (assertion instanceof DQMeasure) {
            return new DQMeasure((DQMeasure) assertion);
        } else if (assertion instanceof DQValidation) {
            return new DQValidation((DQValidation) assertion);
        } else if (assertion instanceof DQImprovement) {
            return new DQImprovement((DQImprovement) assertion);
        }

        return null; // Unsupported assertion type
    }
}
