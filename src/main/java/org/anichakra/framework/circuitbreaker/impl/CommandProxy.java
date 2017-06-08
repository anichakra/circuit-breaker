package org.anichakra.framework.circuitbreaker.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.anichakra.framework.circuitbreaker.api.CircuitBreaker;
import org.anichakra.framework.circuitbreaker.api.Command;
import org.anichakra.framework.circuitbreaker.api.CommandExecutionFailureException;
import org.anichakra.framework.circuitbreaker.api.ExecutorServiceFactory;

/**
 * The proxy class of the {@link Command} that encapsulates the command instance
 * and the {@link CircuitBreaker}. It proxies the
 * {@link Command#execute(Object)} method by executing it and then checking for
 * failure to trip the CircuitBreaker associated with the circuit to which this
 * Command instance belongs to.
 * 
 * @author 576219
 * @param <K>
 * @param <V>
 */
public class CommandProxy<K, V> implements Command<K, V> {

	private Command<K, V> command;
	private CircuitBreaker<K, V> circuitBreaker;
	private ExecutorService executor;

	/**
	 * 
	 * @param command
	 * @param circuitBreaker
	 * @param executorServiceFactory
	 */
	public CommandProxy(Command<K, V> command, CircuitBreaker<K, V> circuitBreaker,
			ExecutorServiceFactory executorServiceFactory) {
		this.command = command;
		this.circuitBreaker = circuitBreaker;
		executor = executorServiceFactory.getExecutorService();
	}

	/**
	 * All executes calls to the actual command instance is made from here, but
	 * each call is wrapped in a new thread with a timeout as defined in
	 * {@link CircuitBreaker#invocationTimeout()}. It checks whether the circuit
	 * is tripped or not. If not then it keeps on calling the
	 * {@link Command#execute()} method of the command. If the circuit is
	 * already tripped then the {@link CircuitBreaker#executeFallback(Object)}
	 * method is called.
	 * <p>
	 * This method also checks for any exception in execute() method and if
	 * there is one then it checks whether its time to trip the circuit breaker.
	 */

	public V execute(final K k) throws CommandExecutionFailureException {
		V v = null;
		if (circuitBreaker.hasTripped()) {
			v = circuitBreaker.executeFallback(k);
		} else {
			Callable<V> c = () -> {
				V returnVal = null;
				try {
					returnVal = command.execute(k);
				} catch (Exception e) {
					if (circuitBreaker.checkFault(e)) {
						circuitBreaker.trip();
						returnVal = circuitBreaker.executeFallback(k);
					} else {
						throw e;
					}
				}
				return returnVal;
			};
			
			Future<V> future = executor.submit(c);
			try {
				v = future.get(circuitBreaker.invocationTimeout(), TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				future.cancel(true);
				throw new CommandExecutionFailureException("Exception calling execute method with input:" + k, e);
			} catch (ExecutionException e) {
				future.cancel(true);
				throw new CommandExecutionFailureException("Exception calling execute method with input:" + k, e);
			} catch (TimeoutException e) {
				future.cancel(true);
				CommandExecutionFailureException ce = new CommandExecutionFailureException(
						"Time taken by the command is more than " + circuitBreaker.invocationTimeout() + "sec for input "
								+ k,
						e);
				if (circuitBreaker.checkFault(ce)) {
					circuitBreaker.trip();
					v = circuitBreaker.executeFallback(k);
				} else {
					throw ce;
				}
			}
		}
		return v;
	}

}
