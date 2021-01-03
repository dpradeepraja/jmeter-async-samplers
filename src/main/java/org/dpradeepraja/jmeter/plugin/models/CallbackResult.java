package org.dpradeepraja.jmeter.plugin.models;

public class CallbackResult {
	public CallbackResult(String status, String body) {
		this.status = status;
		this.callbackBody = body;
	}
	public String status;
	public String callbackBody;
}
