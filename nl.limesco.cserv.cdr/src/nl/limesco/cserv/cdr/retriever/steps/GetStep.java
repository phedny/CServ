package nl.limesco.cserv.cdr.retriever.steps;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.osgi.service.cm.ConfigurationException;

class GetStep extends Step {
	
	GetStep(Dictionary properties, int index) throws ConfigurationException {
		super(properties, index);
	}

	@Override
	public boolean execute(HttpClient client, Map<String, Object> variables) throws IOException {
		final HttpGet request = new HttpGet(uri);
		final HttpResponse response = client.execute(request);
		return execute(response, variables);
	}
	
}