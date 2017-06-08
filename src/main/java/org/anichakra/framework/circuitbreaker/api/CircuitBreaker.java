package org.anichakra.framework.circuitbreaker.api;

import org.anichakra.framework.circuitbreaker.impl.AbstractCircuitBreaker;
import org.anichakra.framework.circuitbreaker.impl.CommandProxy;

/**
 * A circuit breaker is associated with a {@link Circuit} and a {@link Command}.
 * When the Command is failing to call the external system then the
 * CircuitBreaker interrupts and stops the Command as part of the Circuit to get
 * executed further and instead calls a fallback Command. This fallback is
 * called till the Circuit is up and running that means the external system is
 * up and the Command that is calling the external system is working fine.
 * 
 * @author 576219
 * @see AbstractCircuitBreaker
 * @see CommandProxy
 * @param <K>
 * @param <V>
 */
public interface CircuitBreaker<K, V> {

	/**
	 * The implementation should contain a fallback implementation of
	 * {@link Command} associated witht the {@link Circuit} which contains this
	 * {@link CircuitBreaker}. The fallback should take the same input parameter
	 * and should return the same type of response instance. This is done to
	 * avoid any downtime of this system which is running this circuit.
	 * 
	 * @param k
	 * @return
	 */
	V executeFallback(K k);

	/**
	 * When the circuit is failing beyond a threshold then circuit breaker will
	 * trip the circuit, so that the invocation line to the external system is
	 * avoided and the fallback method is called. This makes the circuit open
	 * from closed condition. The threshold can be number of failures in a
	 * period of time. The circuit breaker will monitor each call to made to the
	 * {@link Command} associated with the {@link Circuit} for failure.
	 * <p>
	 * As soon as the circuit is tripped then the circuit breaker must start
	 * checking the health of the circuit by invoking the checkHealth() method
	 * of the circuit breaker repeatedly to make sure the external system
	 * service is again up.
	 */
	void trip();

	/**
	 * 
	 * @return TRUE if the circuit is already tripped. If the circuit is again
	 *         restored then the circuit goes to closed state and this method
	 *         returns FALSE.
	 */
	boolean hasTripped();

	/**
	 * 
	 * @return The timeout in seconds for the external service call. The
	 *         implementation class should return the timeout value so that
	 *         based on that the {@link Command} would be interrupted if not
	 *         completed within this time. If a Command takes more than the time
	 *         specified in the timeout then a
	 *         {@link CommandExecutionFailureException} should be thrown and the
	 *         corresponding external system invocation will be considered as
	 *         failure. The framework will increment the failure count and watch
	 *         if threshold is reached within a period of time.
	 */
	int invocationTimeout();

	/**
	 * This will check if the circuit is still workable or circuit breaker need
	 * to trip and open the connection to external system. This method need to
	 * be invoked each time when an exception is received when calling the
	 * {@link Command}, so that the implementation of this interface should
	 * increment the count of failures. If the exception type is related to
	 * timeout of the service call or due to any other fault then an increment
	 * in the fault count should be done.
	 * 
	 * @param exception
	 *            The exception that has occurred while calling the
	 *            {@link Command}
	 * @return TRUE if fault threshold is reached after adding this exception in
	 *         the stack.
	 */

	boolean checkFault(Exception exception);

	/**
	 * After the fault has happened and circuit is tripped the circuit breaker
	 * automatically fires this method intermittently to check the health of the
	 * circuit. The method should invoke the same external system interface with
	 * some pre-defined data. While calling if found that the failure rate has
	 * reduced to a certain well defined value then it will be assumed that the
	 * external system is up and the {@link Command} of the circuit is workable.
	 * 
	 * @return TRUE if the health threshold value is reached. That is the number
	 *         of failures within a given amount of time has reduced to a
	 *         certain number.
	 */
	boolean checkHealth();

}
