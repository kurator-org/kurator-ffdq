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

package org.kurator.data.provenance;

import java.util.*;

public class BaseRecord {
    private GlobalContext globalContext;

    private Map<String, String> initialValues = new HashMap<>();
    private Map<String, String> currentValues = new HashMap<>();

    private CurationStatus currentStatus;

    private Map<String, CurationStage> curationStages = new HashMap<>();
    private CurationStage currentStage;

    public BaseRecord(Map<String, String> initialValues, GlobalContext globalContext) {
        this.initialValues.putAll(initialValues);
        this.currentValues.putAll(initialValues);

        this.globalContext = globalContext;
    }

    public BaseRecord(Map<String, String> initialValues) {
        this(initialValues, null);
    }

    public void update(NamedContext context, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(currentValues, null, context, status, Arrays.asList(comment));
        addCurationStep(update, context);

        currentStatus = status;
    }

    public void update(NamedContext context, String field, String value, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(currentValues, Collections.singletonMap(field, value), context, status, Arrays.asList(comment));
        addCurationStep(update, context);

        currentValues.put(field, value);
        currentStatus = status;
    }

    public void update(String comment) {
        CurationStep update = new CurationStep(currentValues, null, null, currentStatus, Collections.singletonList(comment));
        addCurationStep(update);
    }

    public void update(CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(currentValues, null, null, status, Arrays.asList(comment));
        addCurationStep(update);

        currentStatus = status;
    }

    public void update(String field, String value, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(currentValues, Collections.singletonMap(field, value), null, status, Arrays.asList(comment));
        addCurationStep(update);

        currentValues.put(field, value);
        currentStatus = status;
    }

    public void update(NamedContext context, String comment) {
        CurationStep update = new CurationStep(currentValues, null, context, currentStatus, Collections.singletonList(comment));
        addCurationStep(update, context);
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
}
