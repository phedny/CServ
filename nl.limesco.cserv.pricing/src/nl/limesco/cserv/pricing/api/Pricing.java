package nl.limesco.cserv.pricing.api;

import nl.limesco.cserv.cdr.api.Cdr;

public interface Pricing {

	long getPerCall();
	
	long getPerMinute();
	
	long getForCdr(Cdr cdr);
	
}
