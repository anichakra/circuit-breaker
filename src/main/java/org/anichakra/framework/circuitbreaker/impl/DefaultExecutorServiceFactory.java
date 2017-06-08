package org.anichakra.framework.circuitbreaker.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.anichakra.framework.circuitbreaker.api.ExecutorServiceFactory;

/**
 * Default implementation of ExecutorServiceFactory
 * @author 576219
 *
 */
public class DefaultExecutorServiceFactory implements ExecutorServiceFactory{

	@SuppressWarnings("unused")
	private int concurrencyLevel;
	public DefaultExecutorServiceFactory(int concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel;
	}
	public ExecutorService getExecutorService() {
		return Executors.newCachedThreadPool();
	}
}
