package org.kurator.data.ffdq.assertions;

import java.util.Map;

public class Measure extends Assertion {
  private String dimension;
  private Result<MeasurementState> result;

  public Measure() {} // default constructor for Jackson

  public Measure (Map<String,String> dataResource, String dimension, String specification, String mechanism, Result<MeasurementState> result){
    super.setDataResource(dataResource);
    this.dimension = dimension;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    this.result = result;
  }
  public String getDimension(){
    return this.dimension;
  }

  public Result<MeasurementState> getResult() {
    return result;
  }
}
