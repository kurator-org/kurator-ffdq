/**  DQMeasure.java
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
 * Represents a measure assertion in FFDQ.
 *
 * @author allankv
 * @author lowery
 */
public class DQMeasure extends DQAssertion {
  private String dimension;

  public DQMeasure() {} // default constructor for Jackson

  public DQMeasure(String dimension, String specification, String mechanism, Context context, Result result){
    this.dimension = dimension;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    super.setContext(context);
    super.setResult(result);
  }

  public DQMeasure(DQMeasure m) {
    setLabel(m.getLabel());
    setDimension(m.getDimension());
    setSpecification(m.getSpecification());
    setMechanism(m.getMechanism());
    setContext(m.getContext());
    setResult(m.getResult());
  }

  public String getDimension(){
    return this.dimension;
  }

  public void setDimension(String dimension) {
    this.dimension = dimension;
  }
}
