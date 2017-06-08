package org.anichakra.framework.circuitbreaker.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.anichakra.framework.circuitbreaker.api.CircuitBreaker;
import org.anichakra.framework.circuitbreaker.api.CommandExecutionFailureException;
import org.anichakra.framework.circuitbreaker.api.FaultThreshold;
import org.anichakra.framework.circuitbreaker.api.HealthThreshold;

/**
 * The abstract {@link CircuitBreaker} that provides a default implementation of
 * the trip functionality along with the check functionalities. It abstracts and
 * keeps open the implementation of the fallback method and the checkHealth
 * method to the implementer.
 * 
 * 
 * @author 576219
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractCircuitBreaker<K, V> implements CircuitBreaker<K, V> {

	private AtomicBoolean closed = new AtomicBoolean(true);

	/**
	 * The default timeout in seconds for external system calls
	 */
	private final static int INVOCATION_TIMEOUT = 60;

	private Queue<Long> faultQueue = new ConcurrentLinkedQueue<Long>();

	private AtomicInteger healthCheckCounter = new AtomicInteger(0);

	private final FaultThreshold faultThreshold;

	private final HealthThreshold healthThreshold;

	private ExecutorService executor;

	/**
	 * Creates an instance of {@link CircuitBreaker} with
	 * {@link DefaultFaultThreshold} and {@link DefaultHealthThreshold}.
	 */
	protected AbstractCircuitBreaker() {
		this.faultThreshold = new DefaultFaultThreshold();
		this.healthThreshold = new DefaultHealthThreshold();
		executor = Executors.newCachedThreadPool();
	}

	/**
	 * Creates an instance of {@link CircuitBreaker} with the passed
	 * {@link DefaultFaultThreshold} and {@link DefaultHealthThreshold}.
	 * 
	 * @param faultThreshold
	 * @param healthThreshold
	 */
	protected AbstractCircuitBreaker(final FaultThreshold faultThreshold, final HealthThreshold healthThreshold) {
		this.faultThreshold = faultThreshold;
		this.healthThreshold = healthThreshold;
		executor = Executors.newFixedThreadPool(2);
	}

	/**
	 * Open the circuit and starts a new thread that continuously checks the
	 * status of the health of the external system by calling the checkHealth()
	 * method. If health is found restored then it again close the circuit. The
	 * health is checked until
	 * {@link HealthThreshold#getMaxDurationForHealthCheck()}. If health is
	 * still not restored then no more health check is done, and fallback method
	 * is called forever.
	 */

	public void trip() {
		if (hasTripped())
			return;
		closed.set(false);
		Future<?> future = executor.submit(() -> {
			boolean healthy = false;
			do {
				try {
					TimeUnit.SECONDS.sleep(healthThreshold.getDelayBetweenHealthChecks());
					healthy = checkHealth();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			} while (!healthy);
			faultQueue.clear();
			closed.set(true);
		});
		try {
			future.get(healthThreshold.getMaxDurationForHealthCheck(), TimeUnit.HOURS);
		} catch (InterruptedException e) {
			future.cancel(true);
			Thread.currentThread().interrupt();
		} catch (ExecutionException | TimeoutException e) {
			future.cancel(true);
		}
	}

	/**
	 * Each health check is done in a new {@link Runnable}. If fault happens
	 * that that is added to the fault counter. For each health check call
	 * timeout is also set according to
	 * {@link CircuitBreaker#invocationTimeout()}. If number of faults is less
	 * than {@link HealthThreshold#getMinAllowableFaults()) within
	 * {@link HealthThreshold#getTimeSpanForCheck()) then it is considered that
	 * health is restored.
	 * <p>
	 * This method abstracts the executeHealthCheck() which the CircuitBreaker
	 * implementation class need to implement. The method should act as a proxy
	 * to the external system interface call.
	 */

	public boolean checkHealth() {
		Future<?> future = executor.submit(() -> {
			try {
				executeHealthCheck(healthCheckCounter.getAndIncrement());
				refreshFaultQueue();
			} catch (Exception e) {
				checkFault(e);
			}
		});

		try {
			future.get(invocationTimeout(), TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			future.cancel(true);
			Thread.currentThread().interrupt();
			handleException(e);
		} catch (ExecutionException | TimeoutException e) {
			future.cancel(true);
			handleException(e);
		}
		return faultQueue.size() <= healthThreshold.getMinAllowableFaults();
	}

	private void handleException(Exception e) {
		CommandExecutionFailureException exception = new CommandExecutionFailureException(
				"Check health execution exception: ", e);
		checkFault(exception);
	}

	/**
	 * Each exception raised need to be added to the fault counter and when the
	 * {@link FaultThreshold#getMaxAllowableFaults()} is reached within
	 * {@link FaultThreshold#getTimeSpanForCheck()} then fault threshold is said
	 * to be reached.
	 * <p>
	 * This method abstracts getFaultType();
	 */

	public boolean checkFault(Exception e) {
		if (e instanceof CommandExecutionFailureException || e.getClass().isAssignableFrom(getFaultType())) {
			faultQueue.offer(System.currentTimeMillis());
			refreshFaultQueue();
		}
		return faultQueue.size() > faultThreshold.getMaxAllowableFaults();
	}

	/**
	 * Refreshes the fault queue. Removes faults from the queue if older than
	 * {@linkplain FaultThreshold#getTimeSpanForCheck()}.
	 */
	protected void refreshFaultQueue() {
		while ((System.currentTimeMillis() - faultQueue.peek().longValue()) / 60000 > faultThreshold
				.getTimeSpanForCheck()) {
			faultQueue.poll();
		}
	}

	/**
	 * Whether the circuit breaker is in tripped state
	 */
	public boolean hasTripped() {
		return !closed.get();
	}

	/**
	 * 
	 */
	public int invocationTimeout() {
		return INVOCATION_TIMEOUT;
	}

	/**
	 * The external system interface might raise many different types of
	 * exceptions. But this abstraction is kept so that the implemeter can
	 * choose for which particular exception the Command will consider it as a
	 * system exception and consider for one of the failures of the circuit.
	 * 
	 * @return The class of the system exception raised during calling the
	 *         external system interface.
	 */
	protected abstract Class<? extends Exception> getFaultType();

	/**
	 * The external system call should be done here with some data embedded in
	 * the method itself.
	 * 
	 * @param The
	 *            number of times the health check is called
	 */
	protected abstract void executeHealthCheck(int counter);

}
