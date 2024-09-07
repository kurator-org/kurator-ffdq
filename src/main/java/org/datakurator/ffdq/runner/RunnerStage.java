
/**
 * RunnerStage.java
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
package org.datakurator.ffdq.runner;

import java.util.ArrayList;
import java.util.List;
public class RunnerStage {
    private String name;
    private List<AssertionTest> validations = new ArrayList<>();
    private List<AssertionTest> measures = new ArrayList<>();
    private List<AssertionTest> amendments = new ArrayList<>();

    /**
     * <p>Constructor for RunnerStage.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public RunnerStage(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>validations</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<AssertionTest> getValidations() {
        return validations;
    }

    /**
     * <p>Setter for the field <code>validations</code>.</p>
     *
     * @param validations a {@link java.util.List} object.
     */
    public void setValidations(List<AssertionTest> validations) {
        this.validations = validations;
    }

    /**
     * <p>Getter for the field <code>measures</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<AssertionTest> getMeasures() {
        return measures;
    }

    /**
     * <p>Setter for the field <code>measures</code>.</p>
     *
     * @param measures a {@link java.util.List} object.
     */
    public void setMeasures(List<AssertionTest> measures) {
        this.measures = measures;
    }

    /**
     * <p>Getter for the field <code>amendments</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<AssertionTest> getAmendments() {
        return amendments;
    }

    /**
     * <p>Setter for the field <code>amendments</code>.</p>
     *
     * @param amendments a {@link java.util.List} object.
     */
    public void setAmendments(List<AssertionTest> amendments) {
        this.amendments = amendments;
    }
}
