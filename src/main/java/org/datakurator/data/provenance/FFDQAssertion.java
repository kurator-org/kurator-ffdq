package org.datakurator.data.provenance;

/**
 * Created by lowery on 11/17/16.
 */
public class FFDQAssertion extends BaseAssertion {
    private NamedContext context;

    public FFDQAssertion(FFDQRecord record) {
        super(record);
    }

    public void setContext(NamedContext context) {
        this.context = context;
    }

    public NamedContext getContext() {
        return context;
    }

    public void prereqUnmet(String... comment) {
        update(context, CurationStatus.DATA_PREREQUISITES_NOT_MET, comment);
    }
}
