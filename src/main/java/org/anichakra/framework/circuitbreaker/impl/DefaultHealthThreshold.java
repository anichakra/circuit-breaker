package org.anichakra.framework.circuitbreaker.impl;

import org.anichakra.framework.circuitbreaker.api.HealthThreshold;

/**
 * Default HealthThreshold with the following properties:
 * <p>
 * <ul>
 * <li>minAllowableFaults=5</li>
 * <li>timeSpanForCheck=20</li>
 * <li>delayBetweenHealthChecks=60</li>
 * <li>timeSpanForCheck=12</li>
 * </ul>
 * 
 * @see AbstractCircuitBreaker
 * @author 576219
 *
 */
public class DefaultHealthThreshold implements HealthThreshold {

	/**
	 * 
	 */
	
	public int getMinAllowableFaults() {
		return 5;
	}

	/**
	 * 
	 */
	
	public int getTimeSpanForCheck() {
		return 20;
	}

	/**
	 * 
	 */
	
	public int getDelayBetweenHealthChecks() {
		return 60;
	}

	/**
	 * 
	 */
	
	public int getMaxDurationForHealthCheck() {
		return 12;
	}

}
