package org.datakurator.data.provenance;

import java.util.Map;

/**
 * Created by lowery on 11/17/16.
 */
public class Improvement extends FFDQAssertion {
    public Improvement(FFDQRecord record) {
        super(record);
    }

    public void fillIn(Map<String, String> updates, String... comment) {
        update(getContext(), updates, CurationStatus.FILLED_IN, comment);
    }
}
