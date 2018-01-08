package org.datakurator.ffdq.model.report.result;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.DataResource;
import org.datakurator.ffdq.model.DwcOccurrence;
import org.datakurator.ffdq.model.report.ResultValue;
import org.datakurator.ffdq.rdf.Namespace;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("prov:Entity")
public class AmendmentValue implements ResultValue {
    private String id = "urn:uuid" + UUID.randomUUID();

    private int score = 1; // TODO: For ranking of alternatives
    private Object value;
    private DwcOccurrence record;

    public AmendmentValue() {
        this.record = new DwcOccurrence();
    }

    public AmendmentValue(Map<String, String> value) {
        this.record = new DwcOccurrence(value);
    }

    @RDFSubject
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @RDF("prov:value")
    public Object getValue() {
        if (this.value == null) {
            return record.getURI();
        }

        return value;
    }

    public void setValue(Object value) {
        if (value instanceof URI) {
            this.record.setURI((URI)value);
        } else {
            this.value = value;
        }
    }

    public DataResource getDataResource() {
        return record;
    }

    public String get(String key) {
        return record.asMap().get(key);
    }

    public int size() {
        return record.asMap().size();
    }

    /**
     * Add a Darwin Core term and its value to the result.
     *
     * @param key the darwin core term for which a value is being provided
     * @param value the value provided for the key.
     */
    public void addResult(String key, String value) {
        // Check that the term is valid and contains the prefix
        URI uri = Namespace.resolvePrefixedTerm(key);

        this.record.put(uri, value);
    }
}
