package nl.limesco.cserv.invoicing;

import nl.limesco.cserv.cdr.api.CdrService;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceService;

public class InvoiceConstructor {
	
	private volatile InvoiceService invoiceService;
	
	private volatile CdrService cdrService;

	Invoice constructInvoiceForAccount(String accountId) {
		return null;
	}
	
}
