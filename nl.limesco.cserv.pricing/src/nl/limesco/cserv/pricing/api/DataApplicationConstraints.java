package nl.limesco.cserv.pricing.api;

import java.util.Calendar;

import nl.limesco.cserv.cdr.api.Cdr;

public interface DataApplicationConstraints extends ApplicationConstraints {

	boolean isApplicable(Calendar date, String source);
	
	boolean isApplicable(Cdr cdr);
		
}
