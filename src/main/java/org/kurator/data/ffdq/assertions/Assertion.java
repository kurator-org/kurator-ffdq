package org.kurator.data.ffdq.assertions;

import java.util.List;
import java.util.Map;

public abstract class Assertion {
  private String namedContext;
  private Map<String, String> dataResource;
  private String specification;
  private String mechanism;
  private List<String> sources;

  public Map<String, String> getDataResource() {
    return this.dataResource;
  }

  public String getSpecification() {
    return this.specification;
  }

  public String getMechanism() {
    return this.mechanism;
  }

  public String getNamedContext() {
    return namedContext;
  }

  public void setDataResource(Map<String, String> dataResource) {
    this.dataResource = dataResource;
  }

  public void setSpecification(String specification) {
    this.specification = specification;
  }

  public void setMechanism(String mechanism) {
    this.mechanism = mechanism;
  }

  public void setNamedContext(String namedContext) {
    this.namedContext = namedContext;
  }
}
