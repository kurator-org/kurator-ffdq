package org.datakurator.ffdq.api;

import org.datakurator.ffdq.model.report.ResultState;
import org.datakurator.ffdq.model.report.ResultValue;

public class DQResponse<T extends ResultValue> {
    private ResultState resultState;
    private T value;

    private StringBuffer resultComment;

    public DQResponse() {
        resultState = ResultState.NOT_RUN;
        resultComment = new StringBuffer();
    }

    public void addComment(String comment) {
        if (resultComment.length()>0) {
            resultComment.append("|");
        }
        resultComment.append(comment);
    }

    public String getComment() {
        return resultComment.toString();
    }

    /**
     * @return the resultState
     */
    public ResultState getResultState() {
        return resultState;
    }

    /**
     * @param resultState the resultState to set
     */
    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
