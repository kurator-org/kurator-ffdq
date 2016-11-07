package org.kurator.data.ffdq.assertions;

import org.kurator.data.provenance.NamedContext;

import java.util.List;
import java.util.Map;

public abstract class Assertion {
  private Context context;
  private String specification;
  private String mechanism;
  private Result result;
  private List<String> sources;

  public String getSpecification() {
    return this.specification;
  }

  public String getMechanism() {
    return this.mechanism;
  }

  public Context getContext() {
    return context;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public void setSpecification(String specification) {
    this.specification = specification;
  }

  public void setMechanism(String mechanism) {
    this.mechanism = mechanism;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }
}
