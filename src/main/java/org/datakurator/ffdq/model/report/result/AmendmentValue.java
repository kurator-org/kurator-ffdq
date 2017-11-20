package org.datakurator.ffdq.model.report.result;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.datakurator.ffdq.model.DataResource;
import org.datakurator.ffdq.model.report.ResultValue;
import org.datakurator.ffdq.rdf.Namespace;

import java.util.Map;
import java.util.UUID;

@RDFNamespaces({
        "ffdq = http://example.com/ffdq/",
        "prov = http://www.w3.org/ns/prov#"
})
@RDFBean("prov:Entity")
public class AmendmentValue implements ResultValue {
    private UUID uuid = UUID.randomUUID();

    private int score = 1; // TODO: For ranking of alternatives
    private Map<String, String> value;

    public AmendmentValue() {

    }

    public AmendmentValue(Map<String, String> value) {
        this.value = value;
    }

    @RDFSubject
    public String getId() {
        return "urn:uuid" + uuid.toString();
    }

    @RDF("prov:value")
    public DataResource getDataResource() {
        return new DataResource(value);
    }

    /**
     * @return the result
     */
    public Map<String,String> getValue() {
        return value;
    }

    /**
     * Add a Darwin Core term and its value to the result.
     *
     * @param key the darwin core term for which a value is being provided
     * @param value the value provided for the key.
     */
    public void addResult(String key, String value) {
        // Check that the term is valid and contains the prefix
        Namespace.resolvePrefixedTerm(key);

        this.value.put(key, value);
    }
}
