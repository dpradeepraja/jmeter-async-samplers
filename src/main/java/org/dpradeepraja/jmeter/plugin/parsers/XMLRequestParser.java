package org.dpradeepraja.jmeter.plugin.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dpradeepraja.jmeter.plugin.HttpServer;
import org.dpradeepraja.jmeter.plugin.NotificationReceiverCreation;
import org.dpradeepraja.jmeter.plugin.models.SubscribedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//To use XMLRequestParser, use below line in NotificationReceiverCreation
//private static final HttpServer httpServer = new HttpServer(new XMLRequestParser());
public class XMLRequestParser implements RequestParser {
	private static final Pattern PATTERN_NOTIF = Pattern.compile(".*FunctionalIdentifier>([+\\d]+)</.*StatusMessage>([\\w_]+)</.*");
	
	private static final Logger LOG = LoggerFactory.getLogger(NotificationReceiverCreation.class);
	public boolean CheckPatternMatch(SubscribedRequest subRequest, String body)
	{
		// Look for a functional identifier and a status
		Matcher m = PATTERN_NOTIF.matcher(body);
		
		if (m.matches()  == false || m.groupCount() != 2) 
			return false;

			String functionalIdentifier = m.group(1);
			String status = m.group(2);

			LOG.error("functionalIdentifier =" + functionalIdentifier);
			LOG.error("status =" + status);
			return true;
	}
}
