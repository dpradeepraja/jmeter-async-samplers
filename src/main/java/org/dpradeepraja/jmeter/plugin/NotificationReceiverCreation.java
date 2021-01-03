package org.dpradeepraja.jmeter.plugin;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.dpradeepraja.jmeter.plugin.models.SubscribedRequest;
import org.dpradeepraja.jmeter.plugin.parsers.JSONRequestParser;
import org.dpradeepraja.jmeter.plugin.parsers.XMLRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationReceiverCreation extends NotificationAbstract {

	private static final HttpServer httpServer = new HttpServer(new JSONRequestParser());
	public static final String CALLBACK_URI = "CALLBACK_URI";
	public static final String CALLBACK_METHOD = "CALLBACK_METHOD";
	public static final String DATA_PATH = "DATA_PATH";
	public static final String EXPECTED_VALUE = "EXPECTED_VALUE";
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String functionalIdentifier = context.getParameter(FUNCTIONAL_ID);
		String callbackURL = context.getParameter(CALLBACK_URI);
		String callbackVerb = context.getParameter(CALLBACK_METHOD);
		String dataPath = context.getParameter(DATA_PATH);
		String expectedValue = context.getParameter(EXPECTED_VALUE);
		
		if(functionalIdentifier == null || functionalIdentifier.isBlank()
			|| callbackURL == null || callbackURL.isBlank()
			|| callbackVerb == null || callbackVerb.isBlank())
			throw new IllegalArgumentException("FUNCTIONAL_ID,CALLBACK_URI,CALLBACK_METHOD are mandatory");
		
		LOG.debug("functionalIdentifier=" + functionalIdentifier);

		SampleResult result = new SampleResult();
		
		result.setSamplerData("Setting up server to listen HTTP:" + callbackVerb + " on URL:" + callbackURL);
		
		try {
			
			SubscribedRequest request = new SubscribedRequest()
					.setCallBackURL(callbackURL)
					.setCallBackHTTPVerb(callbackVerb)
					.setFunctionalId(functionalIdentifier)
					.setDataPath(dataPath)
					.setExpectedValue(expectedValue);
			
			HttpServer.RegisterNewRequest(request);
			
		}
		catch (Exception e) {
			LOG.error("Exception on " + functionalIdentifier, e);
			throw e;
		}
		
		result.setSuccessful(true);
		result.setResponseData("Setup complete for HTTP:" + callbackVerb + " on URL:" + callbackURL,null);
		
		return result;
	}
	
	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument(FUNCTIONAL_ID, "");
		defaultParameters.addArgument(CALLBACK_URI, "/");
		defaultParameters.addArgument(CALLBACK_METHOD, "POST");
		defaultParameters.addArgument(DATA_PATH, "$");
		defaultParameters.addArgument(EXPECTED_VALUE, "");
		return defaultParameters;
	}
	
	public SubscribedRequest LookUpRegisteredRequests(String callBackURI, String callBackMethod)
	{
		return HttpServer.LookUpRegisteredRequests(callBackURI, callBackMethod);
	}


}
