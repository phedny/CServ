package nl.limesco.cserv.sim.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.auth.api.Role;
import nl.limesco.cserv.auth.api.WebAuthorizationService;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimService;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;

@Path("sims")
public class SimResource {
	
	private volatile WebAuthorizationService authorizationService;
	
	private volatile SimService simService;

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
	}

}
