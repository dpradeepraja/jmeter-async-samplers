package org.dpradeepraja.jmeter.plugin.models;

public class SubscribedRequest {

	public SubscribedRequest() {
	}

	public SubscribedRequest(String funclId, String callbackURL, String callbackMethod, String dataPath,
			String expectedVal) {
		this.functionalID = funclId;
		this.callbackURL = callbackURL;
		this.callbackMethod = callbackMethod;
		this.dataPath = dataPath;
		this.expectedValue = expectedVal;
		// TODO Auto-generated constructor stub
	}

	public String getCallbackURL() {
		return callbackURL;
	}

	public String getCallbackMethod() {
		return callbackMethod;
	}

	public String getDataPath() {
		return dataPath;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	public String getFunctionalID() {
		return functionalID;
	}

	private String callbackURL;
	private String callbackMethod;
	private String dataPath;
	private String expectedValue;
	private String functionalID;

	public SubscribedRequest setFunctionalId(String value) {
		this.functionalID = value;
		return this;
	}

	public SubscribedRequest setCallBackURL(String value) {
		this.callbackURL = value;
		return this;
	}

	public SubscribedRequest setCallBackHTTPVerb(String value) {
		this.callbackMethod = value;
		return this;
	}

	public SubscribedRequest setDataPath(String value) {
		this.dataPath = value;
		return this;
	}

	public SubscribedRequest setExpectedValue(String value) {
		this.expectedValue = value;
		return this;
	}

}
