package org.anichakra.framework.circuitbreaker.test.mock;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Mock service simulating an external system interface which will process be
 * healthy till first 15 seconds of the test run and then will gradually become
 * unhealthy and then after 30 seconds will be completely unresponsive. This
 * will be till 90 seconds, after will the service will be again healthy.
 * 
 * @author 576219
 *
 */
public class MockRemoteService {

	private long startTime = System.currentTimeMillis() / 1000;

	public String invoke(MockRequest myRequest) {
		long spentTime = (System.currentTimeMillis() / 1000 - startTime);
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (spentTime >= 15 && spentTime < 30) {
			Random random = new Random();
			int a = random.nextInt(100);
			if (a % 3 == 0) {
				try {
					TimeUnit.MINUTES.sleep(1);
					return "success executing " + myRequest;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			} else if (a % 2 == 0) {
				throw new MockServiceSystemException("Fault in executing " + myRequest);
			} else {
				return "success executing " + myRequest;
			}
		} else if (spentTime >= 30 && spentTime < 90) {
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} else {
			return "success executing " + myRequest;
		}
		return null;
	}
}
