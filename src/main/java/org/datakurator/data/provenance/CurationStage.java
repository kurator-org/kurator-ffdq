package org.datakurator.data.provenance;

import java.util.*;

/**
 * Created by lowery on 11/8/16.
 */
public class CurationStage {
    public static final String DEFAULT = "DEFAULT";

    public static final String PRE_ENHANCEMENT = "PRE_ENHANCEMENT";
    public static final String ENHANCEMENT = "ENHANCEMENT";
    public static final String POST_ENHANCEMENT = "POST_ENHANCEMENT";

    private String stageClassifier;
    private Map<String, String> initialValues;
    private Map<String, String> curatedValues;

    private Stack<CurationStep> updateHistory = new Stack<>();
    private Map<NamedContext, List<CurationStep>> curationHistory = new HashMap<>();

    public CurationStage(Map<String, String> initialValues, String stage) {
        this.stageClassifier = stage;
        this.initialValues = initialValues;
        this.curatedValues = initialValues;
    }

    public CurationStage(Map<String, String> initialValues) {
        this.stageClassifier = DEFAULT;
        this.initialValues = initialValues;
        this.curatedValues = initialValues;
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


    public Map<NamedContext, List<CurationStep>> getCurationHistory() {
        return curationHistory;
    }

    public List<CurationStep> getCurationHistory(NamedContext context) {
        return curationHistory.get(context);
    }

    public String getStageClassifier() {
        return stageClassifier;
    }
}
