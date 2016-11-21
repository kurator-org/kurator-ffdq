package org.datakurator.data.provenance;

/**
 * Created by lowery on 11/17/16.
 */
public class FFDQRecord extends BaseRecord {
    public Validation assertValidation() {
        return new Validation(this);
    }

    public Measure assertMeasure() {
        return new Measure(this);
    }

    public Improvement assertImprovement() {
        return new Improvement(this);
    }

    public Validation assertValidation(NamedContext context) {
        Validation v = new Validation(this);
        v.setContext(context);

        return v;
    }

    public Measure assertMeasure(NamedContext context) {
        Measure m = new Measure(this);
        m.setContext(context);

        return m;
    }

    public Improvement assertImprovement(NamedContext context) {
        Improvement i = new Improvement(this);
        i.setContext(context);

        return i;
    }
}
