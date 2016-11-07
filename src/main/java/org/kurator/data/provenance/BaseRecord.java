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

    private Map<String, String> initialValues;
    private Map<String, String> curatedValues;

    private Stack<CurationStep> updateHistory = new Stack<>();
    private Map<NamedContext, List<CurationStep>> curationHistory = new HashMap<>();

    private CurationStatus currentStatus;

    public BaseRecord(Map<String, String> initialValues) {
        this.initialValues = initialValues;
        this.curatedValues = initialValues;
    }

    public BaseRecord(Map<String, String> initialValues, GlobalContext globalContext) {
        this.initialValues = initialValues;
        this.curatedValues = initialValues;
        this.globalContext = globalContext;
    }

    public void update(NamedContext context, String comment) {
        CurationStep update = new CurationStep(curatedValues, null, context, currentStatus, Collections.singletonList(comment));
        addCurationStep(update, context);
    }

    private void addCurationStep(CurationStep update, NamedContext context) {
        updateHistory.push(update);
        List<CurationStep> bucket = curationHistory.get(context);

        if (bucket == null) {
            bucket = new ArrayList<>();
            curationHistory.put(context, bucket);
        }

        bucket.add(update);
    }

    private void addCurationStep(CurationStep update) {
        updateHistory.push(update);
    }

    public void update(NamedContext context, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(curatedValues, null, context, status, Arrays.asList(comment));
        addCurationStep(update, context);

        currentStatus = status;
    }

    public void update(NamedContext context, String field, String value, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(curatedValues, Collections.singletonMap(field, value), context, status, Arrays.asList(comment));
        addCurationStep(update, context);

        curatedValues.put(field, value);
        currentStatus = status;
    }

    public void update(String comment) {
        CurationStep update = new CurationStep(curatedValues, null, null, currentStatus, Collections.singletonList(comment));
        addCurationStep(update);
    }

    public void update(CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(curatedValues, null, null, status, Arrays.asList(comment));
        addCurationStep(update);

        currentStatus = status;
    }

    public void update(String field, String value, CurationStatus status, String... comment) {
        CurationStep update = new CurationStep(curatedValues, Collections.singletonMap(field, value), null, status, Arrays.asList(comment));
        addCurationStep(update);

        curatedValues.put(field, value);
        currentStatus = status;
    }

    public List<CurationStep> getCurationHistory(NamedContext context) {
        return curationHistory.get(context);
    }

    public Map<NamedContext, List<CurationStep>> getCurationHistory() {
        return curationHistory;
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public Map<String, String> getFinalValues() {
        return curatedValues;
    }

    public CurationStatus getCurationStatus() {
        return currentStatus;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }
}
