package org.anichakra.framework.circuitbreaker.api;

import java.util.concurrent.ExecutorService;

/**
 * This Factory class provides an ExecutorService based on the thread pool
 * configuration in implementaton class.
 * 
 * @author 576219
 *
 */
public interface ExecutorServiceFactory {
	
	/**
	 * 
	 * @return An ExecutorService instance
	 */
	ExecutorService getExecutorService();
}
