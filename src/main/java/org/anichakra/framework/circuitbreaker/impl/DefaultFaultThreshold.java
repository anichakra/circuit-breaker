package org.anichakra.framework.circuitbreaker.impl;

import org.anichakra.framework.circuitbreaker.api.FaultThreshold;

/**
 * Default FaultThreshold with the following properties:
 * <p>
 * <ul>
 * <li>maxAllowableFaults=100</li>
 * <li>timeSpanForCheck=10</li>
 * </ul>
 * 
 * @see AbstractCircuitBreaker
 * @author 576219
 *
 */
public class DefaultFaultThreshold implements FaultThreshold {

	/**
	 * 
	 */
	
	public int getMaxAllowableFaults() {
		return 100;
	}

	/**
	 *
	 */
	
	public int getTimeSpanForCheck() {
		return 10;
	}

}
