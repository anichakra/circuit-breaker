package org.anichakra.framework.circuitbreaker.api;

import org.anichakra.framework.circuitbreaker.impl.DefaultCommandRegistry;

/**
 * The registry that stores all the {@link Circuit}s in the system with a
 * predefined unique name. 
 * @see DefaultCommandRegistry
 * @author 576219
 *
 */
public interface CommandRegistry {

	/**
	 * 
	 * @param string
	 *            The name of the circuit
	 * @return The Command instance associated with the circuit that is
	 *         registered
	 */
	<K, V> Command<K, V> discover(String string);

	/**
	 * 
	 * @param name
	 *            The unique name of the circuit
	 * @param circuit
	 *            The Circuit instance
	 */
	<K, V> void register(String name, Circuit<K, V> circuit);
}
