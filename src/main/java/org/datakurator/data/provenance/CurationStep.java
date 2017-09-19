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

package org.datakurator.data.provenance;

import java.util.*;

/**
 * Represents a single update applied to the state of a record. Keeps track of comments, status.sparql and any changes to
 * field values performed as part of the update.
 *
 * @author lowery
 * @author mole
 */
public class CurationStep {
    /**
     * Key value pairs representing data elements provided to the method as input.
     */
    private Map<String,String> initialElementValues = new HashMap<>();

    /**
     * Key value pairs representing data elements after any modifications by
     * application of the method.
     */
    private Map<String,String> finalElementValues = new HashMap<>();

    private Map<String, CurationStatus> fieldStatus = new HashMap<>();

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
     * The curation status.sparql that applies at a record level
     */
    private CurationStatus recordStatus;

    /**
     * A reference to the context of this changes
     */
    private NamedContext curationContext;

    /**
     * Constructs a curation step from arguments
     *
     * @param initialValues
     * @param updatedValues
     * @param context
     * @param status
     * @param comments
     */
    public CurationStep(Map<String, String> initialValues, Map<String, String> updatedValues,
                        NamedContext context, CurationStatus status, List<String> comments) {

        initialElementValues.putAll(initialValues);

        // Include unchanged values from initial as well, not just the fields pertaining to updates
        finalElementValues.putAll(initialValues);

        if (updatedValues != null) {
            // Merge any updates with the final values constructed from initial
            finalElementValues.putAll(updatedValues);

            // Set field status.sparql for updated fields

            for (String field : updatedValues.keySet()) {
                if (status != null)
                    fieldStatus.put(field, status);
            }
        }
        
        this.curationContext = context;
        this.curationComments = comments;
        this.recordStatus = status;
    }

    /**
     * Get the list of fields acted upon during this step.
     *
     * @return list of fields
     */
    public List<String> getFieldsActedUpon() {
        ArrayList<String> fieldsActedUpon = new ArrayList<String>();

        if (finalElementValues != null) {
            fieldsActedUpon.addAll(finalElementValues.keySet());
        }

        return fieldsActedUpon;
    }

    /**
     * Get the list of any additional fields consulted during this step.
     *
     * @return list of fields
     */
    public List<String> getFieldsConsulted() {
        ArrayList<String> fieldsConsulted = new ArrayList<>();

        if (curationContext != null) {
            fieldsConsulted.addAll(curationContext.getFieldsConsulted());
        }

        return fieldsConsulted;
    }

    /**
     * Get the current status.sparql of the record at the time of the update.
     *
     * @return the record curation status.sparql
     */
    public CurationStatus getCurationStatus() {
        return recordStatus;
    }

    public Map<String, CurationStatus> getFieldStatus() {
        return fieldStatus;
    }

    /**
     * Get a list of any sources consulted during this step.
     *
     * @return a copy of the sourcesConsulted list
     */
    public List<String> getSourcesConsulted() {
        return new ArrayList<String>(sourcesConsulted);
    }

    /**
     * Get the named context that corresponds to this step.
     *
     * @return named context
     */
    public NamedContext getContext() {
        return curationContext;
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

