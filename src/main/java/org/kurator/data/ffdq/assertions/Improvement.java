package org.kurator.data.ffdq.assertions;

import org.kurator.data.provenance.NamedContext;

import java.util.Map;

public class Improvement extends Assertion{
  private String enhancement;

  public Improvement() {} // default constructor for Jackson

  public Improvement (String enhancement, String specification, String mechanism, Context context, Result result){
    this.enhancement = enhancement;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    super.setContext(context);
    super.setResult(result);
  }

  public String getEnhancement(){
    return this.enhancement;
  }

  public void setEnhancement(String enhancement) {
    this.enhancement = enhancement;
  }
}
