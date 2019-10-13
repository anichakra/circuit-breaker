package org.anichakra.framework.circuitbreaker.api;

/**
 * A circuit is a composition of a command that calls the external system as a
 * service and a circuit breaker. Each circuit will have one command and one
 * circuit breaker. When the circuit gets repetitive faults in external service
 * invocation then it trips the circuit breaker to open the circuit and stop any
 * call to the external system. This is done to avoid any further failure in the
 * system and make the system unusable. The Command of the circuit is ignored
 * for the rest of the time till the circuit is up, that means the external
 * system is up and ready with the service.
 * 
 * @see CommandRegistry
 * @author 576219
 *
 * @param <K>
 * @param <V>
 */

public interface Circuit<K, V> {

	/**
	 * 
	 * @return The Command instance for that circuit.
	 */
	Command<K, V> getCommand();

	/**
	 * 
	 * @return The CircuitBreaker instance associated with this circuit.
	 */
	CircuitBreaker<K, V> getCircuitBreaker();

	/**
	 * 
	 * @return The associated ExecutorServiceFactory.
	 */
	ExecutorServiceFactory getExecutorServiceFactory();
}
