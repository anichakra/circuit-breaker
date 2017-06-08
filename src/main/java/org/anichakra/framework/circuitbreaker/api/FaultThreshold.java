package org.anichakra.framework.circuitbreaker.api;

import org.anichakra.framework.circuitbreaker.impl.AbstractCircuitBreaker;

/**
 * The configuration for fault threshold that will be used during {@link CircuitBreaker#checkFault(Exception)}
 * @see AbstractCircuitBreaker
 * @author 576219
 *
 */
public interface FaultThreshold {
	/**
	 * Maximum number of faults that is permissible within a time period
	 */
	int getMaxAllowableFaults();

	/**
	 * The time period in minute in which the number of faults or health will be
	 * checked
	 */
	int getTimeSpanForCheck();
}
