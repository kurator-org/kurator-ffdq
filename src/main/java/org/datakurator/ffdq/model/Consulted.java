/**
 * Consulted.java
 */
package org.datakurator.ffdq.model;

import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import java.net.URI;
import java.util.List;
import java.util.UUID;


/**
 * <p>Consulted class.</p>
 *
 * @author mole
 * @version $Id: $Id
 */
public class Consulted extends InformationElement {

    private String id = "urn:uuid:" + UUID.randomUUID();
	
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
    

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Consulted";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        super.writeRdfProperties(w, s);
        w.values(s, "bdqffdq:composedOf", getComposedOf());
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
    }

    /**
     * Read a Consulted from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the Consulted, the same instance each time for the same subject
     */
    public static Consulted readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Consulted) { return (Consulted) cached; }
        Consulted o = r.remember(s, new Consulted());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
