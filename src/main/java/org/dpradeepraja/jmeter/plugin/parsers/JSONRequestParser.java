package org.dpradeepraja.jmeter.plugin.parsers;


import org.dpradeepraja.jmeter.plugin.NotificationReceiverCreation;
import org.dpradeepraja.jmeter.plugin.models.SubscribedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JSONRequestParser implements RequestParser{
	
	private static final Logger LOG = LoggerFactory.getLogger(NotificationReceiverCreation.class);
	@Override
	public boolean CheckPatternMatch(SubscribedRequest subRequest, String body) {
		// TODO Auto-generated method stub
		
		LOG.info("JSON parser invoked for:" + subRequest.getCallbackURL() + " with Method:" + subRequest.getCallbackMethod());
		
		try {
		if(subRequest.getDataPath() == null || subRequest.getDataPath().isEmpty() == true)
			return true;
		
		if(subRequest.getExpectedValue() == null || subRequest.getExpectedValue().isEmpty() == true)
			throw new IllegalArgumentException("EXPECTED_VALUE is mandatory when DATA_PATH is configured");
		
		String receivedValue = JsonPath.read(body, subRequest.getDataPath()).toString();
		
		if(receivedValue != null && receivedValue.equalsIgnoreCase(subRequest.getExpectedValue()))
			return true;
		}
		catch (PathNotFoundException e)
		{
			return false;
		}
		return false;
	}

}
