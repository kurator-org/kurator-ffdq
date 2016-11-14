package org.datakurator.data.ffdq.assertions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 3/30/16.
 */
public class Context {
    private String name;
    private List<String> fieldsActedUpon = new ArrayList<>();
    private List<String> fieldsConsulted = new ArrayList<>();

    public Context() {} // default constructor for Jackson

    public Context(String name, List<String> fieldsActedUpon, List<String> fieldsConsulted) {
        this.name = name;
        this.fieldsActedUpon = fieldsActedUpon;
        this.fieldsConsulted = fieldsConsulted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFieldsActedUpon() {
        return fieldsActedUpon;
    }

    public void setFieldsActedUpon(List<String> fieldsActedUpon) {
        this.fieldsActedUpon = fieldsActedUpon;
    }

    public List<String> getFieldsConsulted() {
        return fieldsConsulted;
    }

    public void setFieldsConsulted(List<String> fieldsConsulted) {
        this.fieldsConsulted = fieldsConsulted;
    }
}
