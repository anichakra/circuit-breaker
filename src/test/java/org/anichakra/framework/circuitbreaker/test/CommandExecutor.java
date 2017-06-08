package org.anichakra.framework.circuitbreaker.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import org.anichakra.framework.circuitbreaker.api.Command;
import org.anichakra.framework.circuitbreaker.test.mock.MockRequest;
import org.anichakra.framework.circuitbreaker.test.mock.MockResponse;

public class CommandExecutor {
	public static MockResponse execute(Command<MockRequest, MockResponse> command, int count) {
		MockRequest request = new MockRequest("remote-service", count);
		MockResponse response = null;
		try {
			response = command.execute(request);
			System.out.printf("%-12s: %s\n", getTime(), response.getValue());
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			Throwable t = getRootCause(sb, e);
			String message = "";
			if(t instanceof TimeoutException) {
				message = "Remote System is not responding!";
			} else {
				message = "Remote System is throwing exception!";
			}
			// e.printStackTrace();
			System.out.printf("%-12s: %s(%s)\n", getTime(), message, t.getMessage()==null?e.getMessage():t.getMessage());
		}
		return response;
	}
	
	public static String getTime() {
		return new SimpleDateFormat("HH:mm:ss SSS").format(new Date());
	}

	
	public static Throwable getRootCause(StringBuilder sb, Throwable throwable) {
		Throwable returnVal = throwable;
		if (returnVal.getMessage() != null) {
			if (sb.length() > 0)
				sb.append(";");
			sb.append(returnVal.getMessage());
		}
		if (returnVal.getCause() != null) {
			returnVal = getRootCause(sb, returnVal.getCause());
		}
		return returnVal;
	}
}
