package nl.limesco.cserv.sim.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.auth.api.Role;
import nl.limesco.cserv.auth.api.WebAuthorizationService;
import nl.limesco.cserv.sim.api.CallConnectivityType;
import nl.limesco.cserv.sim.api.PortingState;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimApnType;
import nl.limesco.cserv.sim.api.SimChecker;
import nl.limesco.cserv.sim.api.SimService;
import nl.limesco.cserv.sim.api.SimState;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;

@Path("sims")
public class SimResource {
	
	private volatile WebAuthorizationService authorizationService;
	
	private volatile SimService simService;
	
	private volatile AccountService accountService;

	@Path("{simId}")
	public SimSubResource getSim(@PathParam("simId") String id, @Context HttpServletRequest request) {
		String accountId = authorizationService.requiredAccountId(request);
		final Optional<? extends Sim> optSim = simService.getSimByIccid(id);
		if(!optSim.isPresent()) {
			// Give 403 if the requester is not an admin, 404 otherwise
			// (as to not too easily expose whether SIMs exist to non-admins)
			authorizationService.requireUserRole(request, Role.ADMIN);
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		final Sim sim = optSim.get();
		if(!sim.getOwnerAccountId().isPresent() || !sim.getOwnerAccountId().get().equals(accountId)) {
			authorizationService.requireUserRole(request, Role.ADMIN);
		}
		return new SimSubResource(sim);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewSim(String json, @Context HttpServletRequest request) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		
		try {
			final Sim newSim = simService.createSimFromJson(json);
			if(simService.getSimByIccid(newSim.getIccid()).isPresent()) {
				throw new WebApplicationException(Status.CONFLICT);
			}
			simService.storeSim(newSim);
			return Response.created(new URI(newSim.getIccid())).build();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e);
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getMySims(String json, @Context HttpServletRequest request) {
		String accountId = authorizationService.requiredAccountId(request);
		Collection<? extends Sim> sims = simService.getSimsByOwnerAccountId(accountId);
		try {
			return new ObjectMapper().writeValueAsString(sims);
		} catch(JsonGenerationException e) {
			throw new WebApplicationException(e);
		} catch(JsonMappingException e) {
			throw new WebApplicationException(e);
		} catch(IOException e) {
			throw new WebApplicationException(e);
		}
	}
	
	@GET
	@Path("/unallocated")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUnallocatedSims(@Context HttpServletRequest request) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		Collection<? extends Sim> sims = simService.getUnallocatedSims();
		try {
			return new ObjectMapper().writeValueAsString(sims);
		} catch(JsonGenerationException e) {
			throw new WebApplicationException(e);
		} catch(JsonMappingException e) {
			throw new WebApplicationException(e);
		} catch(IOException e) {
			throw new WebApplicationException(e);
		}
	}
	
	public class SimSubResource {
		private final Sim sim;
		
		public SimSubResource(Sim sim) {
			this.sim = sim;
		}
	
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public String getSim() {
			try {
				return new ObjectMapper().writeValueAsString(this.sim);
			} catch(JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch(JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch(IOException e) {
				throw new WebApplicationException(e);
			}
		}
		
		@PUT
		@Consumes(MediaType.APPLICATION_JSON)
		public void updateAccount(String json, @Context HttpServletRequest request) {
			authorizationService.requireUserRole(request, Role.ADMIN);
			try {
				Sim data = simService.createSimFromJson(json);
				if(data.getIccid() == null || !data.getIccid().equals(sim.getIccid())) {
					throw new WebApplicationException(Status.BAD_REQUEST);
				}
				simService.storeSim(data);
			} catch(JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch(JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch(IOException e) {
				throw new WebApplicationException(e);
			}
		}
		
		@POST
		@Path("/allocate")
		@Consumes(MediaType.APPLICATION_JSON)
		public void allocateSim(String json, @Context HttpServletRequest request) {
			authorizationService.requireUserRole(request, Role.ADMIN);
			if(sim.getState() != SimState.STOCK){
				throw new WebApplicationException(Status.CONFLICT);
			}
			try {
				ObjectMapper om = new ObjectMapper();
				Map<String,String> req = om.readValue(json, Map.class);
				String accountId = req.get("ownerAccountId");
				String apn = req.get("apn");
				String callConnectivityType = req.get("callConnectivityType");
				String numberPorting = req.get("numberPorting");
				if(accountId == null || apn == null || callConnectivityType == null || numberPorting == null) {
					throw new WebApplicationException(Status.BAD_REQUEST);
				}
				SimApnType apntype = SimApnType.valueOf(apn);
				if(apntype == null) {
					throw new WebApplicationException(Status.BAD_REQUEST);
				}
				CallConnectivityType cct = CallConnectivityType.valueOf(callConnectivityType);
				if(cct == null) {
					throw new WebApplicationException(Status.BAD_REQUEST);
				}
				PortingState ps;
				if(numberPorting.equals("true")) {
					ps = PortingState.WILL_PORT;
				} else {
					ps = PortingState.NO_PORT;
				}
				Optional<? extends Account> optAccount = accountService.getAccountById(accountId);
				if(!optAccount.isPresent()) {
					throw new WebApplicationException(Status.NOT_FOUND);
				}
				sim.setState(SimState.ALLOCATED);
				sim.setOwnerAccountId(accountId);
				sim.setApnType(apntype);
				sim.setCallConnectivityType(cct);
				sim.setPortingState(ps);
				simService.storeSim(sim);
			} catch(JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch(JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch(IOException e) {
				throw new WebApplicationException(e);
			}
		}
		
		@GET
		@Path("validate")
		@Produces(MediaType.APPLICATION_JSON)
		public String validateSim(@Context HttpServletRequest request) {		
			authorizationService.requireUserRole(request, Role.ADMIN);
			try {
				SimChecker c = new SimChecker(this.sim);
				c.run();
				if(c.isSound()) {
					throw new WebApplicationException(Status.NO_CONTENT);
				}
				return new ObjectMapper().writeValueAsString(c.getProposedChanges());
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
