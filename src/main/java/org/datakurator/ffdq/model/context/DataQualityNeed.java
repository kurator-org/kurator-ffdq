/**
 * DataQualityNeed.java
 */
package org.datakurator.ffdq.model.context;

import org.datakurator.ffdq.rdf.RdfMapped;
import org.datakurator.ffdq.rdf.RdfReader;
import org.datakurator.ffdq.rdf.RdfWriter;
import org.eclipse.rdf4j.model.Resource;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.model.BibliographicResource;

/**
 *  Superclass for "tests"
 *
 * @author mole
 * @version $Id: $Id
 */
public class DataQualityNeed implements RdfMapped {

	private static final Log logger = LogFactory.getLog(DataQualityNeed.class);
	
	protected String historyNote;
	protected String references;
	protected List<BibliographicResource> citationResources = new ArrayList<BibliographicResource>();
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
	 * @return the citation resources
	 */
	public List<BibliographicResource> getCitationResources() {
		return citationResources;
	}

	/**
	 * @param citationResources the list of BibliographicResource instances to set
	 */
	public void setCitationResources(List<BibliographicResource> citationResources) {
		if (citationResources != null) {
			this.citationResources = citationResources;
		}
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
	public String getIssued() {
		return issued;
	}
	
	/**
	 * @return the issued as a date
	 */
	public LocalDate getIssuedDate() {
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
	


    // ---- RDF mapping (explicit RDF4J; replaces the RDFBeans annotations) ----

    /** The RDF type of this class. */
    public static final String RDF_TYPE = "bdqffdq:DataQualityNeed";

    /**
     * Write this object's own properties (and those of its superclasses) as RDF.
     *
     * @param w the writer
     * @param s the subject
     */
    protected void writeRdfProperties(RdfWriter w, Resource s) {
    }

    /**
     * Read this object's own properties (and those of its superclasses) from RDF.
     *
     * @param r the reader
     * @param s the subject
     */
    protected void readRdfProperties(RdfReader r, Resource s) {
    }


    /** {@inheritDoc}  Written by the subclasses (Validation, Issue, Measure, Amendment). */
    @Override
    public Resource writeRdf(RdfWriter w) {
        throw new UnsupportedOperationException("A DataQualityNeed is written as one of its subclasses: " + getClass().getName());
    }

    /**
     * Read a Validation, Issue, Measure, or Amendment from RDF, by its rdf:type.
     *
     * @param r the reader
     * @param s the subject
     * @return the Test, or null if the subject has none of those types
     */
    public static DataQualityNeed readRdf(RdfReader r, Resource s) {
        if (r.hasType(s, Validation.RDF_TYPE)) { return Validation.readRdf(r, s); }
        if (r.hasType(s, Issue.RDF_TYPE)) { return Issue.readRdf(r, s); }
        if (r.hasType(s, Measure.RDF_TYPE)) { return Measure.readRdf(r, s); }
        if (r.hasType(s, Amendment.RDF_TYPE)) { return Amendment.readRdf(r, s); }
        return null;
    }

}
