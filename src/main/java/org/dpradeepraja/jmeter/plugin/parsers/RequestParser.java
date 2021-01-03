package org.dpradeepraja.jmeter.plugin.parsers;

import org.dpradeepraja.jmeter.plugin.models.SubscribedRequest;

public interface RequestParser {
	public boolean CheckPatternMatch(SubscribedRequest subRequest, String body);
}
