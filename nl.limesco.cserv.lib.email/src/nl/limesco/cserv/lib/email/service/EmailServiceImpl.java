package nl.limesco.cserv.lib.email.service;

import java.util.Dictionary;

import nl.limesco.cserv.lib.email.api.EmailService;

import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class EmailServiceImpl implements EmailService, ManagedService {
	
	private volatile String hostname;

	@Override
	public SimpleEmail newSimpleEmail() {
		final SimpleEmail email = new SimpleEmail();
		email.setHostName(hostname);
		return email;
	}

	@Override
	public MultiPartEmail newMultiPartEmail() {
		final MultiPartEmail email = new MultiPartEmail();
		email.setHostName(hostname);
		return email;
	}

	@Override
	public HtmlEmail newHtmlEmail() {
		final HtmlEmail email = new HtmlEmail();
		email.setHostName(hostname);
		return email;
	}

	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties != null) {
			hostname = (String) properties.get("hostname");
			if (hostname == null) {
				throw new ConfigurationException("hostname", "Hostname must be set");
			}
		}
	}

}
