/**
 * 
 */
package org.datakurator.ffdq.runner;

import java.lang.reflect.InvocationTargetException;

/**
 * @author mole
 *
 */
public class RunnerException extends Exception {

	/**
	 * @param message
	 */
	public RunnerException(String message) {
		super(message);
	}

    public RunnerException(String message, InvocationTargetException e) {
    }

}
