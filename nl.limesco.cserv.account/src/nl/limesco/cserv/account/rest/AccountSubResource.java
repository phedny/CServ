package nl.limesco.cserv.account.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.limesco.cserv.account.api.Account;

public class AccountSubResource {
	
	private final Account account;
	
	public AccountSubResource(Account account) {
		this.account = account;
	}

	@GET
	@Path("invoices")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInvoices() {
		return account.getId();
	}
	
}
