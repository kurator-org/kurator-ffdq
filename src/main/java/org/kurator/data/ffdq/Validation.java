package org.kurator.data.ffdq;

import java.util.Map;

public class Validation extends Assertion{
  private String criterion;
  private Result<ValidationState> result;
  private Improvement improvement;

  public Validation() {} // default constructor for Jackson

  public Validation (Map<String,String> dataResource, String criterion, String specification, String mechanism, Result<ValidationState> result){
    super.setDataResource(dataResource);
    this.criterion = criterion;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    this.result = result;
  }
  public String getCriterion(){
    return this.criterion;
  }

  public Result<ValidationState>  getResult() {
    return result;
  }

  public Improvement getImprovement() {
    return improvement;
  }

  public void setImprovement(Improvement improvement) {
    this.improvement = improvement;
  }
}
