package nl.limesco.cserv.invoice.api;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;

import com.google.common.base.Optional;

public interface InvoiceService {

	Optional<? extends Invoice> getInvoiceById(String id);

	Collection<? extends Invoice> getInvoicesByAccountId(String accountId);
	
	Collection<? extends Invoice> getInvoicesByPeriod(Calendar start, Calendar end);
	
	Invoice storeInvoice(Invoice invoice) throws IdAllocationException;
	
	InvoiceBuilder buildInvoice();
	
	Invoice createInvoiceFromJson(String json) throws IOException;
	
	/**
	 * Lock the InvoiceService. This should be done when one starts a process
	 * that requires invoicing information to be consistent and stay like that
	 * for a while, such as invoice generation.
	 * 
	 * Locking is not required when simply requesting invoice information,
	 * unless that information must be guaranteed to be the same later in the
	 * code.
	 * 
	 * Note that for invoice generation the CDR database also needs to remain
	 * consistent, so it is also necessary to lock the CdrService for that.
	 * Locking multiple resources happens in alphabetical order, so lock the
	 * CdrService first to prevent some cases of deadlock.
	 */
	void lock();
	
	/**
	 * Unlock the InvoiceService. Unlocking multiple resources happens in
	 * reverse alphabetical order, so unlock the CdrService first if you have
	 * locked it.
	 */
	void unlock();
	
}
