package org.anichakra.framework.circuitbreaker.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.anichakra.framework.circuitbreaker.api.Circuit;
import org.anichakra.framework.circuitbreaker.api.CircuitBreaker;
import org.anichakra.framework.circuitbreaker.api.Command;
import org.anichakra.framework.circuitbreaker.api.CommandRegistry;
import org.anichakra.framework.circuitbreaker.api.ExecutorServiceFactory;
import org.anichakra.framework.circuitbreaker.impl.DefaultCommandRegistry;
import org.anichakra.framework.circuitbreaker.impl.DefaultExecutorServiceFactory;
import org.anichakra.framework.circuitbreaker.impl.DefaultFaultThreshold;
import org.anichakra.framework.circuitbreaker.impl.DefaultHealthThreshold;
import org.anichakra.framework.circuitbreaker.test.mock.MockCommand;
import org.anichakra.framework.circuitbreaker.test.mock.MockRequest;
import org.anichakra.framework.circuitbreaker.test.mock.MockResponse;
import org.junit.Before;
import org.junit.Test;

public class MainTest {

	private static final int ITERATION = 1000;
	private static final String COMMAND_NAME = "mockCommand";
	private CommandRegistry commandRegistry = null;

	@Before
	public void initialize() {
		commandRegistry = getCommandRegistry();
		commandRegistry.register(COMMAND_NAME, new Circuit<MockRequest, MockResponse>() {
			MockCommand mockCommand = new MockCommand(new DefaultFaultThreshold() {
				public int getTimeSpanForCheck() {
					return 1; // minute
				}
			}, new DefaultHealthThreshold() {
				public int getTimeSpanForCheck() {
					return 1; // minute
				}

				public int getDelayBetweenHealthChecks() {
					return 5; // seconds
				}
			});

			public Command<MockRequest, MockResponse> getCommand() {
				return mockCommand;
			}

			public CircuitBreaker<MockRequest, MockResponse> getCircuitBreaker() {
				return mockCommand;
			}

			public ExecutorServiceFactory getExecutorServiceFactory() {
				return new DefaultExecutorServiceFactory(2);
			}
		});
	}

	@Test
	public void test() {
		final Command<MockRequest, MockResponse> mockCommand = commandRegistry.discover(COMMAND_NAME);
		ExecutorService executor = getExecutorService();
		List<Future<MockResponse>> futureList = new ArrayList<Future<MockResponse>>();
		IntStream.range(1, ITERATION+1).forEach((count) -> {
			futureList.add(executor.submit(() -> CommandExecutor.execute(mockCommand, count)));
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		List<MockResponse> responses = new Vector<MockResponse>();
		futureList.parallelStream().forEach((future) -> {
			try {
				MockResponse response = future.get();
				responses.add(response);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		});
		executor.shutdown();
		assertEquals(responses.size(), ITERATION);
	}

	private static CommandRegistry getCommandRegistry() {
		DefaultCommandRegistry commandRegistry = new DefaultCommandRegistry();
		return commandRegistry;
	}

	protected ExecutorService getExecutorService() {
		return Executors.newCachedThreadPool();
	}
}
