/**
 * ActedUpon.java
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
    "bdqffdq = https://rs.tdwg.org/bdqffdq/terms/",
     "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("bdqffdq:ActedUpon")
public class ActedUpon extends InformationElement {

    private String id = "urn:uuid:" + UUID.randomUUID();
	
    @RDFSubject()
    public String getId() {
        return id;
    }
    @RDF("bdqffdq:composedOf")
    public List<URI> getComposedOf() {
        return composedOf;
    }

}
