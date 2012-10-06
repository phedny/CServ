package nl.limesco.cserv.account.rest;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("accounts")
public class AccountsResource {

	@Path("{id}")
	public Account getAccount(@PathParam("id") int id) {
		return new Account(id);
	}
	
	@Path("~")
	public Account getMyAccount() {
		return new Account(1337);
	}
	
}
