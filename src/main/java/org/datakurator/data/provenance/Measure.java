package org.datakurator.data.provenance;

import java.util.Map;

/**
 * Created by lowery on 11/17/16.
 */
public class Measure extends FFDQAssertion {
    public Measure(FFDQRecord record) {
        super(record);
    }

    public void complete(String... comment) {
        update(getContext(), CurationStatus.COMPLETE, comment);
    }

    public void complete(Map<String, String> updates, String... comment) {
        update(getContext(), updates, CurationStatus.COMPLETE, comment);
    }

    public void incomplete(String... comment) {
        update(getContext(), CurationStatus.NOT_COMPLETE, comment);
    }

    public void incomplete(Map<String, String> updates, String... comment) {
        update(getContext(), updates, CurationStatus.NOT_COMPLETE, comment);
    }
    
    public void ambiguous(String... comment) {
        update(getContext(), CurationStatus.AMBIGUOUS, comment);
    }        
}
