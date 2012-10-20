package nl.limesco.cserv.cdr.api;

import java.io.IOException;
import java.util.Calendar;

public interface CdrRetriever {
	
	static final String ACCOUNT_TYPE = "nl.limesco.cserv.cdr.accountType";
	
	static final String INTERNAL = "internal";
	
	static final String INTERNAL_FILTER = "(" + ACCOUNT_TYPE + "=" + INTERNAL + ")";
	
	static final String EXTERNAL = "external";
	
	static final String EXTERNAL_FILTER = "(" + ACCOUNT_TYPE + "=" +  EXTERNAL + ")";
	
	Iterable<Cdr> retrieveCdrsForDay(Calendar day) throws IOException;
	
}
