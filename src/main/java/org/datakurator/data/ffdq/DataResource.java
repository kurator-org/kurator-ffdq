package org.datakurator.data.ffdq;

import org.datakurator.data.provenance.CurationStage;
import org.datakurator.data.provenance.CurationStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lowery on 11/21/16.
 */
public class DataResource {
    public Map<String, String> initialValues = new HashMap<>();
    public Map<String, String> curatedValues = new HashMap<>();
    public Map<String, CurationStatus> curatedStates = new HashMap<>();

    public DataResource() {

    }

    public void add(String field, String initialValue, String curatedValue, CurationStatus status) {
        initialValues.put(field, initialValue);
        curatedValues.put(field, curatedValue);
        curatedStates.put(field, status);
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public void setInitialValues(Map<String, String> initialValues) {
        this.initialValues = initialValues;
    }

    public Map<String, String> getCuratedValues() {
        return curatedValues;
    }

    public void setCuratedValues(Map<String, String> curatedValues) {
        this.curatedValues = curatedValues;
    }

    public Map<String, CurationStatus> getCuratedStates() {
        return curatedStates;
    }

    public void setCuratedStates(Map<String, CurationStatus> curatedStates) {
        this.curatedStates = curatedStates;
    }

    public Set<String> getFields() {
        return curatedValues.keySet();
    }
}
