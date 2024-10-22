/**
 * DataQualityNeed.java
 */
package org.datakurator.ffdq.model.context;

import java.net.URI;
import java.time.LocalDate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;

/**
 *  Superclass for "tests"
 *
 * @author mole
 * @version $Id: $Id
 */
@RDFBean("bdqffdq:DataQualityNeed")
public class DataQualityNeed {

	private static final Log logger = LogFactory.getLog(DataQualityNeed.class);
	
	protected String historyNote;
	protected String references;
	protected String note;
	protected String issued;
    protected String isVersionOf;
    
    /**
	 * @return the isVersionOf
	 */
	public String getIsVersionOf() {
		return isVersionOf;
	}
    
    /**
	 * @return the isVersionOf as a URI
	 */
    @RDF("dcterms:isVersionOf")
	public URI getIsVersionOfURI() {
    	try { 
    		URI uri = new URI(isVersionOf);
    		return uri;
    	} catch (Exception e) { 
    		return null;
    	}
	}

	/**
	 * @param isVersionOf the isVersionOf to set
	 */
	public void setIsVersionOf(String isVersionOf) {
		this.isVersionOf = isVersionOf;
	}
    

	/**
	 * @param historyNote the historyNote to set
	 */
	public void setHistoryNote(String historyNote) {
		this.historyNote = historyNote;
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(String references) {
		this.references = references;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
	/**
	 * @return the issued
	 */
	@RDF("dcterms:issued")
	public String getIssued() {
		return issued;
	}
	
	/**
	 * @return the issued as a date
	 */
	public LocalDate getIssuedDate() {
		// TODO: Add support for xmls:date from LocalDate to RDFBeans
		// Serialization with @RDF("dcterms:issued") throws exception: 
		// Caused by: org.cyberborean.rdfbeans.exceptions.RDFBeanException: Unsupported class [java.time.LocalDate] of value 2024-09-18
		// at org.cyberborean.rdfbeans.RDFBeanManager.toRdf(RDFBeanManager.java:725)
		LocalDate retval = null;
		try { 
			retval = LocalDate.parse(issued);
		} catch (Exception e) { 
			logger.debug(e.getMessage());
		}
		return retval;
	}

	/**
	 * @param issued the issued to set
	 */
	public void setIssued(String issued) {
		this.issued = issued;
	}
	

}
