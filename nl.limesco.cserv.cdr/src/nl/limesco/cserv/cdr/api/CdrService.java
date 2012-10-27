package nl.limesco.cserv.cdr.api;

import java.util.Collection;

import com.google.common.base.Optional;

public interface CdrService {
	
	Optional<? extends Cdr> getCdrById(String id);
	
	Iterable<? extends Cdr> getCdrByCallId(String source, String callId);

	Collection<? extends Cdr> getUnpricedCdrs();
	
	Collection<? extends Cdr> getUninvoicedCdrs();
	
	void storeCdr(Cdr cdr);
	
	void storePricingForCdr(Cdr cdr, String pricingRuleId, long price, long cost);

}
