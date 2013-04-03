package nl.limesco.cserv.sim.rest;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimService;

@Path("sims")
public class SimResourceExtension implements AccountResourceExtension {
	private volatile SimService simService;
	
	@Override
	public SimSubResourceExtension getAccountResourceExtention(Account account, boolean admin) {
		return new SimSubResourceExtension(simService, account, admin);
	}
	
	public class SimSubResourceExtension {
		private Account account;
		private boolean admin;
		private SimService simService;
		
		public SimSubResourceExtension(SimService simService, Account account, boolean admin) {
			this.account = account;
			this.admin = admin;
			this.simService = simService;
		}
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Collection<? extends Sim> getSims() {
			return simService.getSimsByOwnerAccountId(account.getId());
		}
	}
}
