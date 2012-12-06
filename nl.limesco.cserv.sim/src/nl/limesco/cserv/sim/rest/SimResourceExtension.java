package nl.limesco.cserv.sim.rest;

import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimService;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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
		public String getSims() {
			try {
				Collection<? extends Sim> sims = simService.getSimsByOwnerAccountId(account.getId());
				return new ObjectMapper().writeValueAsString(sims);
			} catch(JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch(JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch(IOException e) {
				throw new WebApplicationException(e);
			}
		}
	}
}
