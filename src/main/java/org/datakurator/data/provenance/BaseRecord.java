/**  BaseRecord.java
 *
 * Copyright 2016 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datakurator.data.provenance;

import org.datakurator.data.ffdq.assertions.DQAssertion;

import java.util.*;

/**
 * Generic record that maintains a list of stage changes to the record data. Used for capturing provenance and to query
 * the curation history of a record.
 *
 * @author lowery
 */
public class BaseRecord {
    private GlobalContext globalContext;

    private String recordId;

    private Map<String, String> initialValues = new HashMap<>();
    private Map<String, String> currentValues = new HashMap<>();

    private CurationStatus currentStatus;

    private Map<String, CurationStage> curationStages = new HashMap<>();
    private CurationStage currentStage;

    public BaseRecord() { }

    /**
     * Initialize the record using the values provided and set the global context.
     *
     * @param initialValues
     * @param globalContext
     */
    public BaseRecord(Map<String, String> initialValues, GlobalContext globalContext) {
        setInitialValues(initialValues);
        setGlobalContext(globalContext);
    }

    /**
     * Initialize the record using the values provided.
     *
     * @param initialValues
     */
    public BaseRecord(Map<String, String> initialValues) {
        this(initialValues, null);
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public BaseAssertion assertUpdate() {
        return new BaseAssertion(this);
    }

    /**
     * Contextual update of record state that appends a comment without changes to field values and status.
     *
     * @param context
     * @param comment
     */
    public void update(NamedContext context, String... comment) {
        CurationStep update = new CurationStep(currentValues, null, context, currentStatus, Arrays.asList(comment));
        addCurationStep(update, context);
    }

    /**
     * Contextual update of record state that involves change of curation status and one or more comments.
     *
     * @param context
     * @param status
     * @param comment
     */
    public void update(NamedContext context, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(currentValues, null, context, status, Arrays.asList(comment));
        addCurationStep(update, context);

        currentStatus = status;
    }

    /**
     * Contextual update of record state that involves updates to a field, change of curation status and
     * one or more comments.
     *
     * @param context
     * @param updates
     * @param status
     * @param comment
     */
    public void update(NamedContext context, Map<String, String> updates, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(currentValues, updates, context, status, Arrays.asList(comment));
        addCurationStep(update, context);

        currentValues.putAll(updates);
        currentStatus = status;
    }

    /**
     * Update of record state that appends a comment without changes to field values and status.
     *
     * @param comment
     */
    public void update(String comment) {
        CurationStep update = new CurationStep(currentValues, null, null, currentStatus, Collections.singletonList(comment));
        addCurationStep(update);
    }

    /**
     * Update of record state that involves updates to curation status and one or more comments.
     *
     * @param status
     * @param comment
     */
    public void update(CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(currentValues, null, null, status, Arrays.asList(comment));
        addCurationStep(update);

        currentStatus = status;
    }

    /**
     * Update of record state that involves updates to a field, change of curation status and
     * one or more comments.
     *
     * @param updates
     * @param status
     * @param comment
     */
    public void update(Map<String, String> updates, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(currentValues, updates, null, status, Arrays.asList(comment));
        addCurationStep(update);

        currentValues.putAll(updates);
        currentStatus = status;
    }

    private void addCurationStep(CurationStep update) {
        if (currentStage == null) {
            currentStage = new CurationStage(initialValues);
            curationStages.put(CurationStage.DEFAULT, currentStage);
        }

        currentStage.addCurationStep(update);
    }

    private void addCurationStep(CurationStep update, NamedContext context) {
        if (currentStage == null) {
            currentStage = new CurationStage(initialValues);
            curationStages.put(CurationStage.DEFAULT, currentStage);
        }

        currentStage.addCurationStep(update, context);
    }

    /**
     * Obtain a list of just the updates associated with a particular field context.
     *
     * @return context to curation steps map
     */
    public List<CurationStep> getCurationHistory(String field) {
        List<CurationStep> history = new ArrayList<>();

        for (CurationStage stage : curationStages.values()) {
            history.addAll(stage.getCurationHistory(field));
        }

        return history;
    }

    public Set<String> getFieldNames() {
        return currentValues.keySet();
    }

    public CurationStage getCurationStage(String stageName) {
        return currentStage;
    }

    public Map<String, CurationStage> getCurationStages() {
        return curationStages;
    }


    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public Map<String, String> getFinalValues() {
        return currentValues;
    }

    public CurationStatus getCurationStatus() {
        return currentStatus;
    }

    public CurationStatus getCurationStatus(String field) {
        List<CurationStep> curationSteps = getCurationHistory(field);
        CurationStep last = curationSteps.get(curationSteps.size()-1);

        return last.getFieldStatus().get(field);
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    public CurationStage getCurrentStage() {
        return currentStage;
    }

    public void startStage(String stage) {
        currentStage = new CurationStage(currentValues, stage);
        curationStages.put(stage, currentStage);
    }

    public String get(String field) {
        return currentValues.get(field);
    }

    public void setInitialValues(Map<String, String> initialValues) {
        this.initialValues.putAll(initialValues);
        this.currentValues.putAll(initialValues);
    }

    public void setGlobalContext(GlobalContext context) {
        this.globalContext = context;
    }

}
