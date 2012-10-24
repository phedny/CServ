package nl.limesco.cserv.cdr.api;

import com.google.common.base.Optional;

public interface CdrService {
	
	Optional<? extends Cdr> getCdrById(String id);
	
	Iterable<? extends Cdr> getCdrByCallId(String source, String callId);
	
	void storeCdr(Cdr cdr);

}
