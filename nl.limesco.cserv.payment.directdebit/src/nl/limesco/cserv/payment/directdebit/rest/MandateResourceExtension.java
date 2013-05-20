package nl.limesco.cserv.payment.directdebit.rest;

import javax.ws.rs.Path;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.payment.directdebit.api.MandateService;

@Path("dd-mandates")
public class MandateResourceExtension implements AccountResourceExtension {
	
	private volatile MandateService mandateService;

	@Override
	public Object getAccountResourceExtention(Account account, boolean admin) {
		return new MandateResource(mandateService, account, admin);
	}

}
