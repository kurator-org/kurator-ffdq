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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Writer;
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

  private List<Measure> measures;
  private List<Validation> validations;
  private List<Improvement> improvements;

  public DQReportStage(String stage) {
    this.stage = stage;

    this.measures = new ArrayList<Measure>();
    this.validations = new ArrayList<Validation>();
    this.improvements = new ArrayList<Improvement>();
  }

  public void pushMeasure(Measure measure){
    this.measures.add(measure);
  }
  public void pushValidation(Validation validation){
    this.validations.add(validation);
  }
  public void pushImprovement(Improvement improvement){
    this.improvements.add(improvement);
  }
  public List<Measure> getMeasures(){
    return this.measures;
  }
  public List<Validation> getValidations(){
    return this.validations;
  }
  public List<Improvement> getImprovements(){
    return this.improvements;
  }

  public String getStage() {
    return stage;
  }
}
