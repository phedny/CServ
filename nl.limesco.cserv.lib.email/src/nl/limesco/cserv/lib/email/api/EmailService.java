package nl.limesco.cserv.lib.email.api;

import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

public interface EmailService {

	SimpleEmail newSimpleEmail();
	
	MultiPartEmail newMultiPartEmail();
	
	HtmlEmail newHtmlEmail();
	
}
