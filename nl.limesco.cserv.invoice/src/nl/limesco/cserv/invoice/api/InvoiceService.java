package nl.limesco.cserv.invoice.api;

import java.util.Collection;

import com.google.common.base.Optional;

public interface InvoiceService {

	Optional<? extends Invoice> getInvoiceById(String id);
	
	Collection<? extends Invoice> getInvoicesByAccountId(String accountId);
	
}
