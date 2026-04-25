/**
 * ActedUpon.java
 */
package org.datakurator.ffdq.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

/**
 * <p>ActedUpon class.</p>
 *
 * @author mole
 * @version $Id: $Id
 */
@RDFNamespaces({
    "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
    "skos = http://www.w3.org/2004/02/skos/core#",
    "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:ActedUpon")
public class ActedUpon extends InformationElement {

    private String id = "urn:uuid:" + UUID.randomUUID();

    /**
     * For MultiRecord Measures that aggregate Responses from upstream Tests,
     * holds the unversioned IRIs of the upstream test terms.
     * Serialized as one or more {@code bdqffdq:aggregatesResponsesFrom} triples.
     */
    private List<URI> aggregatesResponsesFrom = new ArrayList<>();
	
    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @RDFSubject()
    public String getId() {
        return id;
    }
    
    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * <p>getComposedOf.</p>
     *
     * @return a {@link java.util.List} object.
     */
    @RDF("bdqffdq:composedOf")
    public List<URI> getComposedOf() {
        return composedOf;
    }

    /**
     * Returns the upstream test term IRIs that this ActedUpon aggregates responses from.
     * Used for MultiRecord Measures that aggregate Responses from one or more upstream Tests.
     * Each IRI is the unversioned term IRI of the upstream test (e.g.,
     * {@code https://rs.tdwg.org/bdqtest/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134}).
     *
     * @return a list of upstream test term URIs, or an empty list if not applicable
     */
    @RDF("bdqffdq:aggregatesResponsesFrom")
    public List<URI> getAggregatesResponsesFrom() {
        return aggregatesResponsesFrom;
    }

    /**
     * Sets the upstream test term IRIs that this ActedUpon aggregates responses from.
     *
     * @param aggregatesResponsesFrom the list of upstream test term URIs to set
     */
    public void setAggregatesResponsesFrom(List<URI> aggregatesResponsesFrom) {
        this.aggregatesResponsesFrom = aggregatesResponsesFrom;
    }

    /**
     * Adds a single upstream test term IRI to the {@code aggregatesResponsesFrom} list.
     *
     * @param uri the upstream test term URI to add; ignored if null
     */
    public void addAggregatesResponsesFrom(URI uri) {
        if (uri != null) {
            this.aggregatesResponsesFrom.add(uri);
        }
    }

    /**
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return the label
     */
    @RDF("rdfs:label")
	public String getLabel() {
		return super.label;
	}

	/**
	 * <p>Setter for the field <code>label</code>.</p>
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		super.label = label;
	}
    
}
