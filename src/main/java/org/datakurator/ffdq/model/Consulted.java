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
 * 
 * @author mole
 *
 */
@RDFNamespaces({
    "ffdq = http://rs.tdwg.org/bdq/ffdq/"
})
@RDFBean("ffdq:Consulted")
public class Consulted extends InformationElement {

    private String id = "urn:uuid:" + UUID.randomUUID();
	
    @RDFSubject()
    public String getId() {
        return id;
    }

    @RDF("ffdq:composedOf")
    public List<URI> getComposedOf() {
        return composedOf;
    }
    
}
