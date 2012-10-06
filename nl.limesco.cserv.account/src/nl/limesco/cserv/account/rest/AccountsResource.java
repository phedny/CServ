package nl.limesco.cserv.account.rest;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;

import com.google.common.base.Optional;

@Path("accounts")
public class AccountsResource {
	
	private volatile AccountService accountService;

	@Path("{id}")
	public AccountSubResource getAccount(@PathParam("id") String id) {
		final Optional<? extends Account> account = accountService.getAccountById(id);
		if (account.isPresent()) {
			return new AccountSubResource(account.get());
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	@Path("~")
	public AccountSubResource getMyAccount() {
		return new AccountSubResource(accountService.createAccount());
	}
	
}
