/**  DQValidation.java
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

/**
 * Represents a validation assertion in FFDQ.
 *
 * @author allankv
 * @author lowery
 */
public class DQValidation extends DQAssertion {
  private String criterion;

  public DQValidation() {} // default constructor for Jackson

  public DQValidation(String label, String criterion, String specification, String mechanism, Context context, Result result){
    super.setLabel(label);
    this.criterion = criterion;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    super.setContext(context);
    super.setResult(result);
  }

  public DQValidation(DQValidation v) {
    setLabel(v.getLabel());
    setCriterion(v.getCriterion());
    setSpecification(v.getSpecification());
    setMechanism(v.getMechanism());
    setContext(v.getContext());
    setResult(v.getResult());
  }

  public String getCriterion(){
    return this.criterion;
  }

  public void setCriterion(String criterion) {
    this.criterion = criterion;
  }
}
