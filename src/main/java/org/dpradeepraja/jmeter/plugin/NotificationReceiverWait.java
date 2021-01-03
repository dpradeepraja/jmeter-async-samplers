package org.dpradeepraja.jmeter.plugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.dpradeepraja.jmeter.plugin.models.CallbackResult;
import org.dpradeepraja.jmeter.plugin.models.SubscribedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationReceiverWait extends NotificationAbstract {

	public static final String TIME_OUT_IN_SECS = "TIME_OUT_IN_SECS";
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String functionalIdentifier = context.getParameter(FUNCTIONAL_ID);
		long timeOut = Long.parseLong(context.getParameter(TIME_OUT_IN_SECS));
		
		SampleResult result = new SampleResult();
		result.sampleStart();
		SubscribedRequest subRequest = null;
		try {

			LOG.debug("functionalIdentifier=" + functionalIdentifier);

			subRequest = HttpServer.LookUpRegisteredRequests(functionalIdentifier);

			result.setSamplerData(
					"Waiting " + timeOut + " seconds for:" + subRequest.getCallbackMethod() + " on URL:" + subRequest.getCallbackURL());

			CompletableFuture<CallbackResult> future = HttpServer.LookUpRegisteredRequestValue(functionalIdentifier);
			CallbackResult resultObj = future.get(timeOut, TimeUnit.SECONDS);

			if (resultObj.status.equals("PASS")) {
				result.setSuccessful(true);
			} else {
				result.setSuccessful(false);
				result.setResponseMessage("invalid status " + resultObj.status);
			}
			result.setResponseData(resultObj.callbackBody, null);
		}

		catch (Exception e) {
			LOG.error("Exception on " + functionalIdentifier, e);
			result.setSuccessful(false);
			result.setResponseData(e.toString(), null);

		} finally {
			result.sampleEnd(); // stop stopwatch
			if (subRequest != null)
				HttpServer.ClearServedRequests(subRequest);
		}
		return result;
	}
	
	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument(FUNCTIONAL_ID, "");
		defaultParameters.addArgument(TIME_OUT_IN_SECS, "60");
		return defaultParameters;
	}
}
