package nl.limesco.cserv.invoice.rest;

import javax.ws.rs.Path;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.invoice.api.BatchInvoicingService;
import nl.limesco.cserv.invoice.api.InvoiceConstructor;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.InvoiceTransformationService;

@Path("invoices")
public class InvoiceResourceExtension implements AccountResourceExtension {

	private volatile InvoiceService invoiceService;
	
	private volatile InvoiceTransformationService invoiceTransformationService;
	
	private volatile InvoiceConstructor invoiceConstructor;
	
	private volatile BatchInvoicingService batchInvoicingService;
	
	@Override
	public InvoiceResource getAccountResourceExtention(Account account, boolean admin) {
		return new InvoiceResource(invoiceService, invoiceTransformationService, invoiceConstructor, batchInvoicingService, account, admin);
	}

}
