package org.datakurator.data.provenance;

/**
 * Created by lowery on 11/17/16.
 */
public class Validation extends FFDQAssertion {
    public Validation(FFDQRecord record) {
        super(record);
    }

    public void compliant(String... comment) {
        update(getContext(), CurationStatus.COMPLIANT, comment);
    }

    public void nonCompliant(String... comment) {
        update(getContext(), CurationStatus.NOT_COMPLIANT, comment);
    }
    
    public void ambiguous(String... comment) {
        update(getContext(), CurationStatus.AMBIGUOUS, comment);
    }    
    
}
