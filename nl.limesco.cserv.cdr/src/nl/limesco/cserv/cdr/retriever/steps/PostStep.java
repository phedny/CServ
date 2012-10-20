package nl.limesco.cserv.cdr.retriever.steps;

import java.io.IOException;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.osgi.service.cm.ConfigurationException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class PostStep extends Step {
	
	private final String contentType;
	
	private final String content;
	
	private final List<NameValuePair> formValues;
	
	private final Map<Integer, String> formValueParameters;
	
	PostStep(Dictionary properties, int index) throws ConfigurationException {
		super(properties, index);
		contentType = (String) properties.get(index + "_contenttype");
		content = (String) properties.get(index + "_content");
		final String formFields = (String) properties.get(index + "_content_formfields");
		if (formFields != null) {
			formValues = Lists.newArrayList();
			formValueParameters = Maps.newHashMap();
			for (String formField : formFields.split(",")) {
				for (int i = 0; ; i++) {
					final String valueSpec = (String) properties.get(index + "_form_" + formField + "_" + i);
					if (valueSpec == null) {
						break; // Continue with next form field
					} else if (valueSpec.charAt(0) == '=') {
						formValues.add(new BasicNameValuePair(formField, valueSpec.substring(1)));
					} else if (valueSpec.charAt(0) == '<') {
						formValueParameters.put(Integer.valueOf(formValues.size()), valueSpec.substring(1));
						formValues.add(new BasicNameValuePair(formField, ""));
					}
				}
			}
		} else {
			formValues = null;
			formValueParameters = null;
		}
	}

	@Override
	public boolean execute(HttpClient client, Map<String, Object> variables) throws IOException {
		final HttpPost request = new HttpPost(uri);
		
		if (contentType != null && content != null) {
			request.setEntity(new StringEntity(content, ContentType.parse(contentType)));
		} else if (formValues != null) {
			for (Entry<Integer, String> valueParameter : formValueParameters.entrySet()) {
				final String value = (String) variables.get(valueParameter.getValue());
				final int index = valueParameter.getKey().intValue();
				final String name = formValues.get(index).getName();
				formValues.set(index, new BasicNameValuePair(name, value));
			}
			request.setEntity(new UrlEncodedFormEntity(formValues));
		}
		
		final HttpResponse response = client.execute(request);
		return execute(response, variables);
	}
	
}