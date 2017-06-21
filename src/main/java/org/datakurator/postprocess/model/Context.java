package org.datakurator.postprocess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 6/21/17.
 */
public class Context {
    private List<String> fieldsActedUpon = new ArrayList<>();
    private List<String> fieldsConsulted = new ArrayList<>();

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
