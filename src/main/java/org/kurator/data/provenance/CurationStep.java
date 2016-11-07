/**  CurationStep.java
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

public class CurationStep {
    /**
     * Key value pairs representing data elements provided to the method as input.
     */
    private Map<String,String> initialElementValues;

    /**
     * Key value pairs representing data elements after any modifications by
     * application of the method.
     */
    private Map<String,String> finalElementValues;

    /**
     * An ordered list of comments made by the curation step in evaluating the initial element
     * values.  Provides provenance for the curation states and any changes proposed in the
     * final element values.
     */
    private List<String> curationComments;

    /**
     * An ordered list of sources that were consulted by the curation step in evaluating the
     * initial element values.  Provides provenance for the curation states and changes proposed
     * in the final element values.
     */
    private List<String> sourcesConsulted;

    /**
     * The curation status that applies at a record level
     */
    private CurationStatus recordStatus;

    /**
     * A reference to the context of this changes
     */
    private NamedContext curationContext;

    public CurationStep(Map<String, String> initialValues, Map<String, String> updatedValues,
                        NamedContext context, CurationStatus status, List<String> comments) {
        // Create copies
        Map<String, String> initialElementValues = new HashMap<>(initialValues);
        Map<String, String> finalElementValues = new HashMap<>();

        if (updatedValues != null) {
            finalElementValues.putAll(initialValues);
            finalElementValues.putAll(updatedValues);
        }

        this.initialElementValues = initialElementValues;
        this.finalElementValues = finalElementValues;
        this.curationContext = context;
        this.curationComments = comments;
        this.recordStatus = status;
    }

    public void addCurationComment(String curationComment) {
        curationComments.add(curationComment);
    }

    public List<String> getCurationComments() {
        return new ArrayList<String>(curationComments);
    }

    public Map<String,String> getInitialElementValues() {
        return initialElementValues;
    }

    public Map<String,String> getFinalElementValues() {
        return finalElementValues;
    }

    public List<String> getFieldsActedUpon() {
        ArrayList<String> fieldsActedUpon = new ArrayList<String>();

        if (finalElementValues != null) {
            fieldsActedUpon.addAll(finalElementValues.keySet());
        }

        return fieldsActedUpon;
    }

    public List<String> getFieldsConsulted() {
        ArrayList<String> fieldsConsulted = new ArrayList<>();

        if (curationContext != null) {
            fieldsConsulted.addAll(curationContext.getFieldsConsulted());
        }

        return fieldsConsulted;
    }

    /**
     * @return the record curation status
     */
    public CurationStatus getCurationStatus() {
        return recordStatus;
    }

    /**
     * @return a copy of the sourcesConsulted list
     */
    public List<String> getSourcesConsulted() {
        return new ArrayList<String>(sourcesConsulted);
    }

    public NamedContext getContext() {
        return curationContext;
    }

    @Override
    public String toString() {
        return "CurationStep{" +
                "initialElementValues=" + initialElementValues +
                ", finalElementValues=" + finalElementValues +
                ", curationComments=" + curationComments +
                ", sourcesConsulted=" + sourcesConsulted +
                ", recordStatus=" + recordStatus +
                ", curationContext=" + curationContext +
                '}';
    }
}

