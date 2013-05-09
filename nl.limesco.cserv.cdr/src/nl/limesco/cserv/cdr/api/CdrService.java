package nl.limesco.cserv.cdr.api;

import java.util.Calendar;
import java.util.Collection;

import com.google.common.base.Optional;

public interface CdrService {
	
	Optional<? extends Cdr> getCdrById(String id);
	
	Iterable<? extends Cdr> getCdrByCallId(String source, String callId);

	Collection<? extends Cdr> getUnpricedCdrs();
	
	Collection<? extends Cdr> getUninvoicedCdrs();

	Collection<? extends Cdr> getUninvoicedCdrsForAccount(String account, String builder, Calendar until);

	void storeCdr(Cdr cdr);
	
	void storePricingForCdr(Cdr cdr, String pricingRuleId, long price, long cost);

	void setInvoiceIdForBuilder(String builder, String invoiceId);

	/**
	 * Lock the CdrService. This should be done when one starts a process that
	 * requires CDR information to be consistent (such as invoice generation)
	 * or will change CDR information so that it might not be consistent anymore.
	 * 
	 * Locking is not required when simply requesting CDR's information,
	 * unless that information must be guaranteed to be the same later in the
	 * code.
	 * 
	 * @see InvoiceService.lock().
	 */
	void lock();
	
	/**
	 * Unlock the CdrService.
	 */
	void unlock();
}
