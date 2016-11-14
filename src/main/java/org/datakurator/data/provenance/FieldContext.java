package org.datakurator.data.provenance;

import java.util.*;

/**
 * Created by lowery on 11/8/16.
 */
public class FieldContext {
    private List<String> fieldsActedUpon = new ArrayList<>();
    private List<String> fieldsConsulted = new ArrayList<>();

    public FieldContext(String... fieldsActedUpon) {
        setActedUpon(fieldsActedUpon);
    }

    public void setActedUpon(String... fieldsActedUpon) {
        this.fieldsActedUpon = Arrays.asList(fieldsActedUpon);
    }

    public void setConsulted(String... fieldsConsulted) {
        this.fieldsConsulted = Arrays.asList(fieldsConsulted);
    }

    public List<String> getConsulted() {
        return fieldsConsulted;
    }

    public List<String> getActedUpon() {
        return fieldsActedUpon;
    }

    public Map<String, String> getProperties() {
        Map<String, String> props = new HashMap<>();

        props.put("context.fieldsActedUpon", getActedUpon().toString());
        props.put("context.fieldsConsulted", getConsulted().toString());

        return props;
    }
}
