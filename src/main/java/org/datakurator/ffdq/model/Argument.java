/**
 * Argument.java
 */
package org.datakurator.ffdq.model;

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Argument implements RdfMapped {

	private static final Log logger = LogFactory.getLog(Argument.class);

    private String id = "urn:uuid:" + UUID.randomUUID();
    private String label;
    private String value;
    
    private Parameter parameter;
	
    /**
     * Constructor taking a label.
     * 
     * @param label the label for the Argument
     */
    /** For {@link #readRdf(RdfReader, Resource)}. */
    private Argument() { }

    public Argument(String label) {
		this.label = label;
		this .value = "Default";
	}
    
	/**
	 * Constructor taking a parameter and a label
	 * 
	 * @param parameter the formal parameter for which this argument is an actual parameter
     * @param label the label for the Argument
	 */
	public Argument(Parameter parameter, String label) {
		this.parameter = parameter;
		this.label = label;
		this .value = "Default";
	}
	
	/**
	 * Constructor taking a parameter, label, and value
	 * 
	 * @param parameter the formal parameter for which this argument is an actual parameter
     * @param label the label for the Argument
	 * @param value the actual value of the parameter provided by the argument
	 */
	public Argument(Parameter parameter, String label, String value) {
		this.parameter = parameter;
		this.label = label;
		this .value = "Default";
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

    /**
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLabel() {
        return label;
    }

    /**
     * <p>Setter for the field <code>label</code>.</p>
     *
     * @param label a {@link java.lang.String} object.
     */
    public void setLabel(String label) {
        this.label = label;
    }
   
    /**
     * <p>Getter for the field <code>parameter</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * <p>Setter for the field <code>parameter</code>.</p>
     *
     * @param parameter a {@link java.lang.String} object.
     */
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
    

    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:Argument";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
        w.value(s, "rdfs:label", getLabel());
        w.value(s, "bdqffdq:hasParameter", getParameter());
        w.value(s, "bdqffdq:hasArgumentValue", getValue());
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
        setLabel(r.string(s, "rdfs:label"));
        setParameter(r.read(s, "bdqffdq:hasParameter", Parameter::readRdf));
        setValue(r.string(s, "bdqffdq:hasArgumentValue"));
    }

    /**
     * Read a Argument from RDF.
     *
     * @param r the reader
     * @param s the subject
     * @return the Argument, the same instance each time for the same subject
     */
    public static Argument readRdf(RdfReader r, Resource s) {
        Object cached = r.cached(s);
        if (cached instanceof Argument) { return (Argument) cached; }
        Argument o = r.remember(s, new Argument());
        o.setId(s.stringValue());
        o.readRdfProperties(r, s);
        return o;
    }

}
