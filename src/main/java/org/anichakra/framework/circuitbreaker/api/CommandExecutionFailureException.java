package org.anichakra.framework.circuitbreaker.api;

public class CommandExecutionFailureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @param message The message for the failure
	 * @param rootCause The root cause of the failure
	 */
	public CommandExecutionFailureException(String message, Exception rootCause) {
		super(message, rootCause);
	}

}
