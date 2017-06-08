package org.anichakra.framework.circuitbreaker.test.mock;

public class MockRequest {

	private String value;
	private int count;

	public MockRequest(String value, int count) {
		this.value = value;
		this.count = count;
	}
	
	public int count() {
		return  count;
	}
	
	@Override
	public String toString() {
		return value + " count " + count;
	}

}
