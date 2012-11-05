package nl.limesco.cserv.payment.rest;

import javax.ws.rs.Path;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.payment.api.PaymentService;

@Path("payments")
public class PaymentResourceExtension implements AccountResourceExtension {

	private volatile PaymentService paymentService;
	
	@Override
	public PaymentResource getAccountResourceExtention(Account account, boolean admin) {
		return new PaymentResource(paymentService, account, admin);
	}

}
