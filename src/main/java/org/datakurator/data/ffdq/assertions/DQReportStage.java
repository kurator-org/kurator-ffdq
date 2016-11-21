/**  DQReportStage.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * A Data quality stage that contains FFDQ assertions.
 *
 * @author allankv
 * @author lowery
 */
public class DQReportStage {
  private String stage;

  private List<DQMeasure> measures;
  private List<DQValidation> validations;
  private List<DQImprovement> improvements;

  public DQReportStage(String stage) {
    this.stage = stage;

    this.measures = new ArrayList<DQMeasure>();
    this.validations = new ArrayList<DQValidation>();
    this.improvements = new ArrayList<DQImprovement>();
  }

  public void pushMeasure(DQMeasure measure){
    this.measures.add(measure);
  }
  public void pushValidation(DQValidation validation){
    this.validations.add(validation);
  }
  public void pushImprovement(DQImprovement improvement){
    this.improvements.add(improvement);
  }
  public List<DQMeasure> getMeasures(){
    return this.measures;
  }
  public List<DQValidation> getValidations(){
    return this.validations;
  }
  public List<DQImprovement> getImprovements(){
    return this.improvements;
  }

  public String getStage() {
    return stage;
  }
}
