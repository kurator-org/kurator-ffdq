/**
 * 
 */
package org.datakurator.ffdq.runner;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>RunnerException class.</p>
 *
 * @author mole
 * @version $Id: $Id
 */
public class RunnerException extends Exception {

	/**
	 * <p>Constructor for RunnerException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public RunnerException(String message) {
		super(message);
	}

    /**
     * <p>Constructor for RunnerException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param e a {@link java.lang.reflect.InvocationTargetException} object.
     */
    public RunnerException(String message, InvocationTargetException e) {
    }

}
