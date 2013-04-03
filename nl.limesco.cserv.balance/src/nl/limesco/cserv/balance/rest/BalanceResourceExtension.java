package nl.limesco.cserv.balance.rest;

import javax.ws.rs.Path;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.payment.api.PaymentService;

@Path("balance")
public class BalanceResourceExtension implements AccountResourceExtension {

	private volatile InvoiceService invoiceService;
	
	private volatile PaymentService paymentService;
	
	@Override
	public Object getAccountResourceExtention(Account account, boolean admin) {
		return new BalanceResource(invoiceService, paymentService, account, admin);
	}

}
