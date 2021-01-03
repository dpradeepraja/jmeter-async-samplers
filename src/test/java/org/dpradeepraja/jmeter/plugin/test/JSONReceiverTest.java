package org.dpradeepraja.jmeter.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.dpradeepraja.jmeter.plugin.NotificationAbstract;
import org.dpradeepraja.jmeter.plugin.NotificationReceiverCreation;
import org.dpradeepraja.jmeter.plugin.NotificationReceiverWait;
import org.dpradeepraja.jmeter.plugin.models.SubscribedRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.mockito.Mockito;

@RunWith(Parameterized.class)
public class JSONReceiverTest {

	private static final int TIMEOUT = 3;

	public final String baseURI = "http://localhost:8080";
	
	private SubscribedRequest testRequest;
	private String body;
	private boolean expectedValue;
	public JSONReceiverTest(SubscribedRequest paramRequest, String ParamBody, Boolean expVal) {
		this.testRequest = paramRequest;
		this.body = ParamBody;
		this.expectedValue = expVal;
	}

	private JavaSamplerContext GetMockContext(SubscribedRequest request) {
		JavaSamplerContext mockSampler = Mockito.mock(JavaSamplerContext.class);
		when(mockSampler.getParameter(NotificationAbstract.FUNCTIONAL_ID)).thenReturn(request.getFunctionalID());
		when(mockSampler.getParameter(NotificationReceiverCreation.CALLBACK_URI)).thenReturn(request.getCallbackURL());
		when(mockSampler.getParameter(NotificationReceiverCreation.CALLBACK_METHOD)).thenReturn(request.getCallbackMethod());
		when(mockSampler.getParameter(NotificationReceiverCreation.DATA_PATH)).thenReturn(request.getDataPath());
		when(mockSampler.getParameter(NotificationReceiverCreation.EXPECTED_VALUE)).thenReturn(request.getExpectedValue());
		when(mockSampler.getParameter(NotificationReceiverWait.TIME_OUT_IN_SECS)).thenReturn("5");

		return mockSampler;
	}

	private void InitiateAsyncCallBack(SubscribedRequest request, String body)
			throws ClientProtocolException, IOException {

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT * 1000)
				.setConnectTimeout(TIMEOUT * 1000).build();

		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
				// .setConnectionManager(poolingHttpClientConnectionManager)
				.build();

		HttpPost httpPost = new HttpPost(baseURI + request.getCallbackURL());

		StringEntity entity = new StringEntity(body);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		CloseableHttpResponse response = client.execute(httpPost);
		assertEquals(response.getStatusLine().getStatusCode(), 200);
		client.close();
	}

	@Parameterized.Parameters
	public static Collection testData() {
		return Arrays
				.asList(new Object[][] {  
					{new SubscribedRequest("DummyID1", "/waitForJSON1", "POST", "$.name", "John"),
						new String("{\"id\":1,\"name\":\"John\"}"), true },
					{ new SubscribedRequest("DummyID2", "/waitForJSON2", "POST", "$.id", "1"),
							new String("{\"id\":1,\"name\":\"John\"}"), true },
					{ new SubscribedRequest("DummyID2", "/waitForJSON2", "POST", "$.id", "2"),
								new String("{\"id\":1,\"name\":\"John\"}"), false }
						});
	}

	@Test
	public void testCreationAndWait() {

		NotificationReceiverCreation receiver = new NotificationReceiverCreation();
		receiver.runTest(GetMockContext(testRequest));

		SubscribedRequest registeredRequest = receiver.LookUpRegisteredRequests(testRequest.getCallbackURL(),
				testRequest.getCallbackMethod());
		assertEquals(testRequest.getCallbackMethod(), registeredRequest.getCallbackMethod());
		assertEquals(testRequest.getCallbackURL(), registeredRequest.getCallbackURL());
		assertEquals(testRequest.getFunctionalID(), registeredRequest.getFunctionalID());

		NotificationReceiverWait waiter = new NotificationReceiverWait();

		Thread t = new Thread(() -> {
			try {
				this.InitiateAsyncCallBack(testRequest, body);
			} catch (ClientProtocolException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		t.run();

		SampleResult result = waiter.runTest(GetMockContext(testRequest));

		assertEquals(expectedValue, result.isSuccessful());
	}

}
