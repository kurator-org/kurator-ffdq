/**  CurationStage.java
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
 * Encapsulates a series of updates performed as part of a temporal stage in the curation of a record.
 */
public class CurationStage {
    public static final String DEFAULT = "DEFAULT";

    public static final String PRE_ENHANCEMENT = "PRE_ENHANCEMENT";
    public static final String ENHANCEMENT = "ENHANCEMENT";
    public static final String POST_ENHANCEMENT = "POST_ENHANCEMENT";

    private String stageClassifier;
    private Map<String, String> initialValues = new HashMap<>();
    private Map<String, String> curatedValues = new HashMap<>();

    private Stack<CurationStep> updateHistory = new Stack<>();
    private Map<NamedContext, List<CurationStep>> curationHistory = new HashMap<>();

    public CurationStage(Map<String, String> initialValues, String stage) {
        this.stageClassifier = stage;
        this.initialValues.putAll(initialValues);
        this.curatedValues.putAll(initialValues);
    }

    public CurationStage(Map<String, String> initialValues) {
        this.stageClassifier = DEFAULT;
        this.initialValues.putAll(initialValues);
        this.curatedValues.putAll(initialValues);
    }

    void addCurationStep(CurationStep update, NamedContext context) {
        updateHistory.push(update);
        List<CurationStep> bucket = curationHistory.get(context);

        if (bucket == null) {
            bucket = new ArrayList<>();
            curationHistory.put(context, bucket);
        }

        bucket.add(update);
    }

    void addCurationStep(CurationStep update) {
        updateHistory.push(update);
    }


    /**
     * List curation history by context. Maps each context to a list that contains a subset of updates associated with
     * that context.
     *
     * @return context to curation steps map
     */
    public Map<NamedContext, List<CurationStep>> getCurationHistory() {
        return curationHistory;
    }

    /**
     * Obtain a list of just the updates associated with a particular context.
     *
     * @return context to curation steps map
     */
    public List<CurationStep> getCurationHistory(NamedContext context) {
        return curationHistory.get(context);
    }

    /**
     * The name of this stage (pre enhancement, enhancement, post enhancemen)
     *
     * @return
     */
    public String getStageClassifier() {
        return stageClassifier;
    }
}
