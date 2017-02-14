package org.datakurator.postprocess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowery on 2/13/2017.
 */
public class AssertionSummary {
    private String recordId;
    private List<AssertionRow> assertionRows = new ArrayList<>();

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public List<AssertionRow> getAssertionRows() {
        return assertionRows;
    }

    public void setAssertionRows(List<AssertionRow> assertionRows) {
        this.assertionRows = assertionRows;
    }

    public void addAssertionRow(AssertionRow assertionRow) {
        this.assertionRows.add(assertionRow);
    }
}
