/** RunnerStage.java
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
package org.datakurator.ffdq.runner;

import java.util.ArrayList;
import java.util.List;

public class RunnerStage {
    private String name;
    private List<AssertionTest> validations = new ArrayList<>();
    private List<AssertionTest> measures = new ArrayList<>();
    private List<AssertionTest> amendments = new ArrayList<>();

    public RunnerStage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssertionTest> getValidations() {
        return validations;
    }

    public void setValidations(List<AssertionTest> validations) {
        this.validations = validations;
    }

    public List<AssertionTest> getMeasures() {
        return measures;
    }

    public void setMeasures(List<AssertionTest> measures) {
        this.measures = measures;
    }

    public List<AssertionTest> getAmendments() {
        return amendments;
    }

    public void setAmendments(List<AssertionTest> amendments) {
        this.amendments = amendments;
    }
}
