package org.kurator.data.ffdq.assertions;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class DQReport {
  private String stage;

  private List<Measure> measures;
  private List<Validation> validations;
  private List<Improvement> improvements;

  public DQReport(String stage) {
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

  public void write(Writer writer) throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    mapper.writerWithDefaultPrettyPrinter().writeValue(writer, this);
  }
}
