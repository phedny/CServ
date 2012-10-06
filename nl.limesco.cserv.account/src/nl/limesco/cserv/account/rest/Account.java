package nl.limesco.cserv.account.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class Account {
	
	private final int id;
	
	public Account(int id) {
		this.id = id;
	}

	@GET
	@Path("invoices")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInvoices() {
		return Integer.toString(id);
	}
	
}
