package org.anichakra.framework.circuitbreaker.test.mock;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.anichakra.framework.circuitbreaker.api.Command;
import org.anichakra.framework.circuitbreaker.api.CommandExecutionFailureException;
import org.anichakra.framework.circuitbreaker.impl.AbstractCircuitBreaker;
import org.anichakra.framework.circuitbreaker.impl.DefaultFaultThreshold;
import org.anichakra.framework.circuitbreaker.impl.DefaultHealthThreshold;

public class MockCommand extends AbstractCircuitBreaker<MockRequest, MockResponse>
		implements Command<MockRequest, MockResponse> {
	MockRemoteService externalService = new MockRemoteService();

	public MockCommand(DefaultFaultThreshold faultThreshold, DefaultHealthThreshold defaultHealthThreshold) {
		super(faultThreshold, defaultHealthThreshold);
	}

	public MockResponse execute(MockRequest k) throws CommandExecutionFailureException {
		// call external service
		String value = externalService.invoke(k);
		return new MockResponse(value);
	}

	public MockResponse executeFallback(MockRequest k) {
		// calling fallback
		MockResponse response = new MockResponse("success executing fallback of " + k);
		response.setError("error: 0001");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		return response;
	}

	protected void executeHealthCheck(int counter) {
		String response = "";
		try {
			response = externalService.invoke(new MockRequest("health check", counter+1));
		} finally {
			if (response == null) {
				System.out.printf("%s: health check done for count %d, remote-service is unresponsive\n", getTime(), counter+1);
			} else {
				System.out.printf("%s: health check done returns %s\n", getTime(), response);
			}
		}
	}

	private String getTime() {
		return new SimpleDateFormat("HH:mm:ss SSS").format(new Date());
	}

	public Class<MockServiceSystemException> getFaultType() {
		return MockServiceSystemException.class;
	}

	@Override
	public int invocationTimeout() {
		return 2;
	}

}
