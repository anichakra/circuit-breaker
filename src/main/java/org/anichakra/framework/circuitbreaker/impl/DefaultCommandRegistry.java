package org.anichakra.framework.circuitbreaker.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.anichakra.framework.circuitbreaker.api.Circuit;
import org.anichakra.framework.circuitbreaker.api.CircuitBreaker;
import org.anichakra.framework.circuitbreaker.api.Command;
import org.anichakra.framework.circuitbreaker.api.CommandRegistry;

/**
 * The default implementation of {@link CommandRegistry} where all the
 * {@link Command} and {@link CircuitBreaker} instances as part of the
 * {@link Circuit}s are wrapped in a {@link CommandProxy} are registered and
 * cached in a {@link ConcurrentHashMap} instance.
 * 
 * @author 576219
 *
 */
public class DefaultCommandRegistry implements CommandRegistry {

	private static ConcurrentHashMap<String, CommandProxy<?, ?>> commandMap = new ConcurrentHashMap<String, CommandProxy<?, ?>>();

	/**
	 * Finds the corresponding {@link Command} instance for the circuit name.
	 */
	@SuppressWarnings("unchecked")

	public <K, V> Command<K, V> discover(String name) {
		return (Command<K, V>) commandMap.get(name);
	}

	/**
	 * Register a {@link Command} from a circuit by creating a
	 * {@link CommandProxy} instance and wrapping the Command and the
	 * corresponding {@link CircuitBreaker}F
	 */

	public <K, V> void register(String name, Circuit<K, V> circuit) {
		CommandProxy<K, V> proxy = new CommandProxy<K, V>(circuit.getCommand(), circuit.getCircuitBreaker(),
				circuit.getExecutorServiceFactory());
		commandMap.put(name, proxy);
	}
}
