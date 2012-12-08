package nl.limesco.cserv.pricing.api;

import java.util.Calendar;
import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.SmsCdr;

public interface SmsApplicationConstraints extends ApplicationConstraints {

	Collection<SmsCdr.Type> getCdrTypes();

	boolean isApplicable(Calendar date, String source, SmsCdr.Type cdrType);
	
	boolean isApplicable(Cdr cdr);
		
}
