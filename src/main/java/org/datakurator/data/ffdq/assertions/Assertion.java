/**  Assertion.java
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

import java.util.List;

/**
 * Represents a generic assertion in FFDQ.
 *
 * @author allankv
 * @author lowery
 */
public abstract class Assertion {
  private String recordId;

  private Context context;
  private String specification;
  private String mechanism;
  private Result result;
  private List<String> sources;

  public String getSpecification() {
    return this.specification;
  }

  public String getMechanism() {
    return this.mechanism;
  }

  public Context getContext() {
    return context;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public void setSpecification(String specification) {
    this.specification = specification;
  }

  public void setMechanism(String mechanism) {
    this.mechanism = mechanism;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public List<String> getSources() {
    return sources;
  }

  public void setSources(List<String> sources) {
    this.sources = sources;
  }

  public String getRecordId() {
    return recordId;
  }

  public void setRecordId(String recordId) {
    this.recordId = recordId;
  }
}
