package org.kurator.data.ffdq.assertions;

import org.kurator.data.provenance.CurationStatus;
import org.kurator.data.provenance.NamedContext;

import java.util.Map;

public class Measure extends Assertion {
  private String dimension;

  public Measure() {} // default constructor for Jackson

  public Measure (String dimension, String specification, String mechanism, Context context, Result result){
    this.dimension = dimension;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    super.setContext(context);
    super.setResult(result);
  }
  public String getDimension(){
    return this.dimension;
  }

  public void setDimension(String dimension) {
    this.dimension = dimension;
  }
}
