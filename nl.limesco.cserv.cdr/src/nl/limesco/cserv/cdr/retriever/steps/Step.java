package nl.limesco.cserv.cdr.retriever.steps;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.osgi.service.cm.ConfigurationException;

public abstract class Step {
	
	protected final String source;
	
	protected final String uri;
	
	protected final int[] expectedStatus;
	
	protected final String outputVar;

	protected Step(Dictionary properties, int index) throws ConfigurationException {
		source = (String) properties.get("source");
		uri = (String) properties.get(index + "_uri");
		outputVar = (String) properties.get(index + "_outputvar");
		
		final String expectedStatusCombined = (String) properties.get(index + "_expectedstatus");
		if (expectedStatusCombined != null) {
			final String[] expectedStatusSplit = expectedStatusCombined.split(",");
			expectedStatus = new int[expectedStatusSplit.length];
			for (int i = 0; i < expectedStatusSplit.length; i++) {
				try {
					expectedStatus[i] = Integer.parseInt(expectedStatusSplit[i]);
				} catch (NumberFormatException e) {
					throw new ConfigurationException(index + "_expectedStatus", "Must be a list of valid HTTP response codes");
				}
			}
		} else {
			expectedStatus = null;
		}
	}
	
	public static Step newInstance(Dictionary properties, int index) throws ConfigurationException {
		final String method = (String) properties.get(index + "_method");
		if (method == null) {
			return null;
		} else if ("GET".equals(method)) {
			return new GetStep(properties, index);
		} else if ("POST".equals(method)) {
			return new PostStep(properties, index);
		} else if ("parsecsv".equals(method)) {
			return new ParseCsvStep(properties, index);
		} else if ("formatdate".equals(method)) {
			return new FormatDateStep(properties, index);
		}
		return null;
	}
	
	public abstract boolean execute(HttpClient client, Map<String, Object> variables) throws IOException;
	
	protected boolean execute(HttpResponse response, Map<String, Object> variables) throws IOException {
		if (!checkStatusCode(response)) {
			return false;
		}
		
		if (outputVar != null) {
			variables.put(outputVar, response.getEntity().getContent());
		} else {
			EntityUtils.consume(response.getEntity());
		}
		
		return true;
	}

	protected boolean checkStatusCode(final HttpResponse response) {
		if (expectedStatus != null) {
			final int statusCode = response.getStatusLine().getStatusCode();
			for (int expectedCode : expectedStatus) {
				if (statusCode == expectedCode) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}
	
}
