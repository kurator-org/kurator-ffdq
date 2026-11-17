/**
 * Parameter.java
 */
package org.datakurator.ffdq.model;

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.rdf.Namespace;

public class Parameter implements RdfMapped {

	private static final Log logger = LogFactory.getLog(Parameter.class);

	private String id;
	
	/** For {@link #readRdf(RdfReader, Resource)}. */
	private Parameter() { }

	public Parameter (String id) { 
		this.id = Namespace.expandCurie(id);
	}
	
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

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Parameter";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
    }

    /** {@inheritDoc} */
    @Override
    public Resource writeRdf(RdfWriter w) {
        IRI s = RdfWriter.subject(getId(), null);
        if (w.begin(this)) {
            w.type(s, RDF_TYPE);
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
    }

    /**
     * Read a Parameter from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the Parameter, the same instance each time for the same subject
     */
    public static Parameter readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Parameter) { return (Parameter) cached; }
        Parameter o = r.remember(s, new Parameter());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
