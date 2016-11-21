package org.datakurator.postprocess;

import org.datakurator.data.ffdq.assertions.Result;

/**
 * Created by lowery on 11/21/16.
 */
public class CuratedRecord {
    private Result result;

    public CuratedRecord(Result result) {
        this.result = result;
    }

    public String getInitialValue(String field) {
        return result.getInitialValues().get(field);
    }
}
