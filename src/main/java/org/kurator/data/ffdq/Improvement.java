package org.kurator.data.ffdq;

import java.util.Map;

public class Improvement extends Assertion{
  private String enhancement;
  private Map<String,String> result;

  public Improvement() {} // default constructor for Jackson

  public Improvement (Map<String,String> dataResource, String enhancement, String specification, String mechanism, Map<String,String> result) {
    super.setDataResource(dataResource);
    this.enhancement = enhancement;
    super.setSpecification(specification);
    super.setMechanism(mechanism);
    this.result = result;
  }
  public String getEnhancement(){
    return this.enhancement;
  }


  public Map<String, String> getResult() {
    return result;
  }
}
