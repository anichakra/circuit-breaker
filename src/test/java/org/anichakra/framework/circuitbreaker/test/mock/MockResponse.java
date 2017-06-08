package org.anichakra.framework.circuitbreaker.test.mock;

public class MockResponse {
	private String value;
	private String error;

	public MockResponse(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
