/**
 * Consulted.java
 */
package org.datakurator.ffdq.model;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

/**
 * <p>Consulted class.</p>
 *
 * @author mole
 * @version $Id: $Id
 */
@RDFNamespaces({
    "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
    "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:Consulted")
public class Consulted extends InformationElement {

    private String id = "urn:uuid:" + UUID.randomUUID();
	
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
     * <p>getComposedOf.</p>
     *
     * @return a {@link java.util.List} object.
     */
    @RDF("bdqffdq:composedOf")
    public List<URI> getComposedOf() {
        return composedOf;
    }
    
}
