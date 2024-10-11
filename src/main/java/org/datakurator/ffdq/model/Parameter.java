/**
 * Parameter.java
 */
package org.datakurator.ffdq.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({
    "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
    "bdq = https://rs.tdwg.org/bdq/terms/",
    "skos = http://www.w3.org/2004/02/skos/core#",
    "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:Parameter")
public class Parameter {

	private static final Log logger = LogFactory.getLog(Parameter.class);

	private String id;
	
	public Parameter (String id) { 
		this.id = id;
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
}
