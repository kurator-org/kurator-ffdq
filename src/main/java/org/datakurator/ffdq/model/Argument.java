/**
 * Argument.java
 */
package org.datakurator.ffdq.model;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({
    "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
    "bdq = https://rs.tdwg.org/bdq/terms/",
    "skos = http://www.w3.org/2004/02/skos/core#",
    "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:Argument")
public class Argument {

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
    @RDFSubject
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
    @RDF("rdfs:label")
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
    @RDF("bdqffdq:hasParameter")
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
    @RDF("bdqffdq:hasArgumentValue")
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
    
}
