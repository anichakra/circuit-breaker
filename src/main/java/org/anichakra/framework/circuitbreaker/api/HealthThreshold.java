package org.anichakra.framework.circuitbreaker.api;

import org.anichakra.framework.circuitbreaker.impl.AbstractCircuitBreaker;

/**
 * The configuration for health threshold that will be used during {@link CircuitBreaker#checkHealth()}
 * @see AbstractCircuitBreaker
 * @author 576219
 *
 */
public interface HealthThreshold {

	/**
	 * The minimum allowable faults in the system with a time span to ensure
	 * that the health of the external system interface is ok and the circuit
	 * can be closed.
	 * 
	 * @return The number of faults. Make sure it is less than {@link FaultThreshold#getMaxAllowableFaults()}.
	 */
	int getMinAllowableFaults();

	/**
	 * The time span through which the health i.e. number of faults while calling the {@link CircuitBreaker#checkHealth()} is called.
	 * @return Time in minutes
	 */
	int getTimeSpanForCheck();

	/**
	 * The delay in seconds between each call to external system to check for
	 * its health;
	 *  @return Time in seconds
	 */
	int getDelayBetweenHealthChecks();

	/**
	 * The Maximum Time in hours for checking the health of the external system.
	 * After that no health checks will be done.
	 * 
	 *  @return Time in hours
	 */
	int getMaxDurationForHealthCheck();

}
