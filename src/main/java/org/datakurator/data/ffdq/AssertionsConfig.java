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

import org.datakurator.data.ffdq.assertions.Assertion;
import org.datakurator.data.ffdq.assertions.Improvement;
import org.datakurator.data.ffdq.assertions.Measure;
import org.datakurator.data.ffdq.assertions.Validation;

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
    private Map<String, Assertion> assertionMap = new HashMap<>();

    private List<Measure> measures = new ArrayList<>();
    private List<Validation> validations = new ArrayList<>();
    private List<Improvement> improvements = new ArrayList<>();

    public AssertionsConfig() { }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
        mapAssertions(measures);
    }

    private void mapAssertions(List<? extends Assertion> assertions) {
        for (Assertion assertion : assertions) {
            assertionMap.put(assertion.getContext().getName(), assertion);
        }
    }

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
        mapAssertions(validations);
    }

    public List<Improvement> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<Improvement> improvements) {
        this.improvements = improvements;
        mapAssertions(improvements);
    }

    public Assertion forContext(String name) {
        // TODO: This functionality needs to be refactored to be less fragile. Use the factory pattern instead.
        Assertion assertion = assertionMap.get(name);

        if (assertion instanceof Measure) {
            return new Measure((Measure) assertion);
        } else if (assertion instanceof Validation) {
            return new Validation((Validation) assertion);
        } else if (assertion instanceof Improvement) {
            return new Improvement((Improvement) assertion);
        }

        return null; // Unsupported assertion type
    }
}
