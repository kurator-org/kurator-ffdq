/**  Result.java
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

package org.datakurator.data.ffdq.assertions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Describes the data curation result tied to an FFDQ assertion.
 *
 * @author lowery
 */
public class Result {
    private Map<String, String> initialValues;
    private Map<String, String> curatedValues;

    private List<String> comments;
    private String status;

    public Result() {} // default constructor for Jackson

    public Result(Map<String, String> initialValues, Map<String, String> curatedValues, List<String> comments, String status) {
        this.initialValues = initialValues;
        this.curatedValues = curatedValues;
        this.comments = comments;
        this.status = status;
    }

    public Result(Map<String, String> initialValues, List<String> comments, String status) {
        this.initialValues = initialValues;
        this.curatedValues = Collections.EMPTY_MAP;
        this.comments = comments;
        this.status = status;
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public void setInitialValues(Map<String, String> initialValues) {
        this.initialValues = initialValues;
    }

    public Map<String, String> getCuratedValues() {
        return curatedValues;
    }

    public void setCuratedValues(Map<String, String> curatedValues) {
        this.curatedValues = curatedValues;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
