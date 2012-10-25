package nl.limesco.cserv.invoice.api;

import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Optional;

public interface InvoiceService {

	Optional<? extends Invoice> getInvoiceById(String id);

	Collection<? extends Invoice> getInvoicesByAccountId(String accountId);
	
	Invoice storeInvoice(Invoice invoice) throws IdAllocationException;
	
	InvoiceBuilder buildInvoice();
	
	Invoice createInvoiceFromJson(String json) throws IOException;
	
}
