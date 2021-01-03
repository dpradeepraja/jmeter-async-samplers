package org.dpradeepraja.jmeter.plugin;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NotificationAbstract extends AbstractJavaSamplerClient {

	protected static final Logger LOG = LoggerFactory.getLogger(NotificationAbstract.class);

	public static final String FUNCTIONAL_ID = "FUNCTIONAL_ID";
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
				 
		throw new UnsupportedOperationException("Abstract method invoked");
	}

	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument(FUNCTIONAL_ID, "");
		return defaultParameters;
	}


}
