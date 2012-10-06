package nl.limesco.cserv.account.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;

import com.google.common.base.Optional;

public class UnavailableAccountService implements AccountService {

	@Override
	public Optional<? extends Account> getAccountById(String id) {
		throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
	}

	@Override
	public Account createAccount() {
		throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
	}

	@Override
	public void updateAccount(Account account) {
		throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
	}

}
