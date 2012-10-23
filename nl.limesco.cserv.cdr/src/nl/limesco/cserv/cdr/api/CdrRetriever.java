package nl.limesco.cserv.cdr.api;

import java.io.IOException;
import java.util.Calendar;

public interface CdrRetriever {
	
	Iterable<Cdr> retrieveCdrsForDay(Calendar day) throws IOException;
	
}
