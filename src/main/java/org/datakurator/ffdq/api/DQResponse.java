/** DQResponse.java
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
package org.datakurator.ffdq.api;

import org.datakurator.ffdq.model.report.ResultState;

public class DQResponse<T extends ResultValue> {
    private ResultState resultState;
    private T value;

    private StringBuffer resultComment;

    public DQResponse() {
        resultState = ResultState.NOT_RUN;
        resultComment = new StringBuffer();
    }

    public void addComment(String comment) {
        if (resultComment.length()>0) {
            resultComment.append("|");
        }
        resultComment.append(comment);
    }

    public String getComment() {
        return resultComment.toString();
    }

    /**
     * @return the resultState
     */
    public ResultState getResultState() {
        return resultState;
    }

    /**
     * @param resultState the resultState to set
     */
    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Object getObject() {
        return value.getObject();
    }
}
