package org.datakurator.data.provenance;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Created by lowery on 11/17/16.
 */
public class BaseAssertion {
    private BaseRecord record;

    public BaseAssertion(BaseRecord record) {
        this.record = record;
    }

    /**
     * Contextual update of record state that appends a comment without changes to field values and status.
     *
     * @param context
     * @param comment
     */
    public void update(NamedContext context, String... comment) {
        record.update(context, comment);
    }

    /**
     * Contextual update of record state that involves change of curation status and one or more comments.
     *
     * @param context
     * @param status
     * @param comment
     */
    public void update(NamedContext context, CurationStatus status, String... comment) {
        record.update(context, status, comment);
    }

    /**
     * Contextual update of record state that involves updates to a field, change of curation status and
     * one or more comments.
     *
     * @param context
     * @param updates
     * @param status
     * @param comment
     */
    public void update(NamedContext context, Map<String, String> updates, CurationStatus status, String... comment) {
        record.update(context, updates, status, comment);
    }

    /**
     * Update of record state that appends a comment without changes to field values and status.
     *
     * @param comment
     */
    public void update(String comment) {
        record.update(comment);
    }

    /**
     * Update of record state that involves updates to curation status and one or more comments.
     *
     * @param status
     * @param comment
     */
    public void update(CurationStatus status, String... comment) {
        record.update(status, comment);
    }

    /**
     * Update of record state that involves updates to a field, change of curation status and
     * one or more comments.
     *
     * @param updates
     * @param status
     * @param comment
     */
    public void update(Map<String, String> updates, CurationStatus status, String... comment) {
        record.update(updates, status, comment);
    }

}
