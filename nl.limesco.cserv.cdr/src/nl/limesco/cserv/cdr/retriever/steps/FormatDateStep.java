package nl.limesco.cserv.cdr.retriever.steps;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.client.HttpClient;
import org.osgi.service.cm.ConfigurationException;

class FormatDateStep extends Step {
	
	private final String inputVar;
	
	private final SimpleDateFormat pattern;
	
	FormatDateStep(Dictionary properties, int index) throws ConfigurationException {
		super(properties, index);
		inputVar = (String) properties.get(index + "_inputvar");
		pattern = new SimpleDateFormat((String) properties.get(index + "_pattern"));
		pattern.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public boolean execute(HttpClient client, Map<String, Object> variables) throws IOException {
		final Calendar date = (Calendar) variables.get(inputVar);
		final String formattedDate = pattern.format(date.getTime());
		variables.put(outputVar, formattedDate);
		return true;
	}

}