package nl.limesco.cserv.invoice.api;

import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Optional;

public interface InvoiceService {

	Optional<? extends Invoice> getInvoiceById(String id);

	Optional<? extends Invoice> getInvoiceBySequentialId(String sequentialId);

	Collection<? extends Invoice> getInvoicesByAccountId(String accountId);
	
	Invoice storeInvoice(Invoice invoice);
	
	InvoiceBuilder buildInvoice();
	
	Invoice createInvoiceFromJson(String json) throws IOException;
	
}
