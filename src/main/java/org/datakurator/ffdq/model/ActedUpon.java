/**
 * ActedUpon.java
 */
package org.datakurator.ffdq.model;

import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * <p>ActedUpon class.</p>
 *
 * @author mole
 * @version $Id: $Id
 */
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
     * A human-readable note for this ActedUpon node.
     * For MultiRecord aggregation nodes this is set to describe which upstream test's
     * response outcomes are being aggregated, e.g.
     * {@code "Aggregated Response outcomes produced by VALIDATION_COUNTRY_FOUND across a MultiRecord."}.
     */
    private String note;

    /**
     * Returns the {@code skos:note} for this ActedUpon node.
     *
     * @return the note string, or null if not set
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the {@code skos:note} for this ActedUpon node.
     *
     * @param note the note string to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return the label
     */
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
    

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:ActedUpon";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        super.writeRdfProperties(w, s);
        w.values(s, "bdqffdq:composedOf", getComposedOf());
        w.values(s, "bdqffdq:aggregatesResponsesFrom", getAggregatesResponsesFrom());
        w.value(s, "skos:note", getNote());
        w.value(s, "rdfs:label", getLabel());
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), null);
        if (w.begin(this)) {
            w.type(s, RDF_TYPE, "bdqffdq:InformationElement");
            writeRdfProperties(w, s);
        }
        return s;
    }

    /**
     * Read this object's own properties (and those of its superclasses) from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
        super.readRdfProperties(r, s);
        setComposedOf(new java.util.ArrayList<>(r.uris(s, "bdqffdq:composedOf")));
        setAggregatesResponsesFrom(new java.util.ArrayList<>(r.uris(s, "bdqffdq:aggregatesResponsesFrom")));
        setNote(r.string(s, "skos:note"));
        setLabel(r.string(s, "rdfs:label"));
    }

    /**
     * Read a ActedUpon from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the ActedUpon, the same instance each time for the same subject
     */
    public static ActedUpon readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof ActedUpon) { return (ActedUpon) cached; }
        ActedUpon o = r.remember(s, new ActedUpon());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
