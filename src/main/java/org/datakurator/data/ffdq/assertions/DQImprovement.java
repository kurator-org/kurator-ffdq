/**  DQImprovement.java
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
 * Represents an improvement assertion in FFDQ.
 *
 * @author allankv
 * @author lowery
 */
public class DQImprovement extends DQAssertion {
  private String enhancement;

  public DQImprovement() {} // default constructor for Jackson

  public DQImprovement(String enhancement, String specification, String mechanism, Context context, Result result){
    this.enhancement = enhancement;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    super.setContext(context);
    super.setResult(result);
  }

  /**
   * Copy constructor
   */
  public DQImprovement(DQImprovement i) {
    setEnhancement(i.getEnhancement());
    setSpecification(i.getSpecification());
    setMechanism(i.getMechanism());
    setContext(i.getContext());
    setResult(i.getResult());
  }

  public String getEnhancement(){
    return this.enhancement;
  }

  public void setEnhancement(String enhancement) {
    this.enhancement = enhancement;
  }
}
