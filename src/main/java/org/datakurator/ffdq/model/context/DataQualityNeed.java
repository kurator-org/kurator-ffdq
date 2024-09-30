/**
 * DataQualityNeed.java
 */
package org.datakurator.ffdq.model.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	

}
