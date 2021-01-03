package org.dpradeepraja.jmeter.plugin;

import fi.iki.elonen.NanoHTTPD;

import org.dpradeepraja.jmeter.plugin.models.CallbackResult;
import org.dpradeepraja.jmeter.plugin.models.SubscribedRequest;
import org.dpradeepraja.jmeter.plugin.parsers.RequestParser;
import org.jdom2.IllegalDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HttpServer extends NanoHTTPD {

	private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);
	private String content;

	private static final ConcurrentHashMap<SubscribedRequest, CompletableFuture<CallbackResult>> results = new ConcurrentHashMap<SubscribedRequest, CompletableFuture<CallbackResult>>();

	RequestParser parser;

	/**
	 * Start a HTTP Server on port 8080
	 */
	public HttpServer(RequestParser inpParser) {
		super(8080);
		parser = inpParser;
		try {
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			content = readReponseFile();
			LOG.info("NanoHTTPD started");
		} catch (URISyntaxException | IOException e) {
			LOG.info("Can not start NanoHTTPD", e);
		}

	}

	/**
	 * Read a sample response file
	 * 
	 * @return Content of the response file
	 * @throws URISyntaxException if uri is not understood
	 * @throws IOException        if file can not be read/created
	 */
	private String readReponseFile() throws URISyntaxException, IOException {
		URI uri = HttpServer.class.getResource("/response.xml").toURI();
		LOG.info("uri = " + uri + " Scheme:" + uri.getScheme());
		
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		
		if(!uri.getScheme().equals("file"))
			FileSystems.getFileSystem(uri);
		Path path = Paths.get(uri);
		LOG.info("path = " + path);
		return new String(Files.readAllBytes(path));
	}

	/**
	 * Method called when HTTP server receive a request
	 *
	 * In my sample, the received request is XML
	 * 
	 * @param session
	 * @return a HTTP response
	 */
	@Override
	public Response serve(IHTTPSession session) {

		try {
			// In my sample, the received request always contains a content-length.
			// See on internet for other way to close a HTTP response
			String length = session.getHeaders().get("content-length");
			if (length != null) {

				// Read response
				int contentLength = Integer.parseInt(length);
				byte[] buffer = new byte[contentLength];
				session.getInputStream().read(buffer, 0, contentLength);
				String body = new String(buffer);
				LOG.debug("request =" + body);

				SubscribedRequest subRequest = LookUpRegisteredRequests(session.getUri(),
						session.getMethod().toString());

				CompletableFuture<CallbackResult> result = LookUpRegisteredRequestValue(subRequest.getFunctionalID());

				if (parser.CheckPatternMatch(subRequest, body) == true)
					UpdateRequestSuccessful(result, body);
				else
					UpdateRequestFailed(result, body);
			}
		} catch (IOException e) {
			LOG.info("Cannot serve response = " + session, e);
		} catch (IllegalArgumentException e) {
			LOG.error("Cannot parse = " + session, e);
		}

		// constant response
		return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/xml", content);

	}

	public static void RegisterNewRequest(SubscribedRequest newRequest) {

		// Todo handle duplicate registrations

		CompletableFuture<CallbackResult> future = new CompletableFuture<CallbackResult>();
		results.put(newRequest, future);

	}

	public static SubscribedRequest LookUpRegisteredRequests(String functionalId) {

		Iterator<SubscribedRequest> it = results.keySet().iterator();

		while (it.hasNext()) {
			SubscribedRequest key = it.next();
			if (key.getFunctionalID().equalsIgnoreCase(functionalId))
				return key;
		}

		throw new IllegalDataException("Functional ID not registered:" + functionalId);
	}

	public static void ClearServedRequests(SubscribedRequest key) {
		results.remove(key);
	}

	public static CompletableFuture<CallbackResult> LookUpRegisteredRequestValue(String functionalId) {

		Iterator<SubscribedRequest> it = results.keySet().iterator();

		while (it.hasNext()) {
			SubscribedRequest key = it.next();
			if (key.getFunctionalID().equalsIgnoreCase(functionalId))
				return results.get(key);
		}

		throw new IllegalDataException("Functional ID not registered:" + functionalId);
	}

	public static SubscribedRequest LookUpRegisteredRequests(String callBackURI, String callBackMethod) {

		Iterator<SubscribedRequest> it = results.keySet().iterator();

		while (it.hasNext()) {
			SubscribedRequest key = it.next();
			if (key.getCallbackURL().equalsIgnoreCase(callBackURI)
					&& key.getCallbackMethod().equalsIgnoreCase(callBackMethod))
				return key;
		}

		throw new IllegalDataException("Method:" + callBackMethod + " not registered for URL:" + callBackURI);
	}

	public static void UpdateRequestSuccessful(CompletableFuture<CallbackResult> resultObj, String body) {
		UpdateRequestStatus(resultObj, body, "PASS");
	}

	public static void UpdateRequestFailed(CompletableFuture<CallbackResult> resultObj, String body) {
		UpdateRequestStatus(resultObj, body, "FAILED");
	}

	public static void UpdateRequestStatus(CompletableFuture<CallbackResult> resultObj, String body, String status) {

		// Notify the JMeter sample that response is received for this identifier

		if (resultObj == null)
			throw new IllegalDataException("Result objection empty when updating body:" + body);

		resultObj.complete(new CallbackResult(status, body));
	}

}
