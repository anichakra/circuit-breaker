package org.anichakra.framework.circuitbreaker.api;

/**
 * The implementation will act as a proxy to the external system interface. This
 * is the abstraction for any command that calls any external system interface
 * which has a potential of failure at runtime. The Command acts as an
 * indirection layer that requires a {@link CircuitBreaker} to watch for
 * failures and then disconnect all calls to this Command instance.
 * 
 * @author 576219
 * @see CommandRegistry
 * @see CircuitBreaker
 * @see Circuit
 * @param <K>
 * @param <V>
 */
public interface Command<K, V> {

	/**
	 * The implementation should contain the invocation to an remote or external
	 * system which can fail or give timeout exception during invocation.
	 * 
	 * @param k
	 *            Any input parameter
	 * @return The valid return value from the external interface call
	 * @throws CommandExecutionFailureException
	 *             This will wrap the underlying exception be it timeout or any
	 *             connection related system exception.
	 */
	V execute(K k) throws CommandExecutionFailureException;

}
