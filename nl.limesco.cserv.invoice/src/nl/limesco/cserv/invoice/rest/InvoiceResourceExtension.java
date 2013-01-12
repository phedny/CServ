package nl.limesco.cserv.invoice.rest;

import javax.ws.rs.Path;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.InvoiceTransformationService;

@Path("invoices")
public class InvoiceResourceExtension implements AccountResourceExtension {

	private volatile InvoiceService invoiceService;
	
	private volatile InvoiceTransformationService invoiceTransformationService;
	
	@Override
	public InvoiceResource getAccountResourceExtention(Account account, boolean admin) {
		return new InvoiceResource(invoiceService, invoiceTransformationService, account, admin);
	}

}
