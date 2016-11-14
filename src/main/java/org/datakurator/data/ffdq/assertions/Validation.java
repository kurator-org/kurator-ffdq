package org.datakurator.data.ffdq.assertions;

public class Validation extends Assertion{
  private String criterion;

  public Validation() {} // default constructor for Jackson

  public Validation (String criterion, String specification, String mechanism, Context context, Result result){
    this.criterion = criterion;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    super.setContext(context);
    super.setResult(result);
  }

  public String getCriterion(){
    return this.criterion;
  }

  public void setCriterion(String criterion) {
    this.criterion = criterion;
  }
}
