package nl.limesco.cserv.payment.directdebit.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.payment.directdebit.api.Mandate;
import nl.limesco.cserv.payment.directdebit.api.MandateService;

import com.google.common.base.Optional;

public class MandateResource {

	private final MandateService mandateService;
	
	private final Account account;
	
	private final boolean admin;
	
	public MandateResource(MandateService mandateService, Account account, boolean admin) {
		this.mandateService = mandateService;
		this.account = account;
		this.admin = admin;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<? extends Mandate> getMandates() {
		return mandateService.getMandatesForAccount(account.getId());
	}
	
	@POST
	@Path("new")
	public Response createMandate() throws URISyntaxException {
		if (!admin) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		final Mandate mandate = mandateService.createMandateForAccount(account.getId());
		return Response.created(new URI(account.getId() + "/dd-mandates/" + mandate.getId())).build();
	}
	
	@GET
	@Path("{mandateId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Mandate getMandate(@PathParam("mandateId") String mandateId) {
		final Optional<? extends Mandate> mandate = mandateService.getMandateById(mandateId);
		if (mandate.isPresent() && mandate.get().getAccountId().equals(account.getId())) {
			return mandate.get();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	@PUT
	@Path("{mandateId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateMandate(@PathParam("mandateId") String mandateId, String json) throws IOException {
		if (!admin) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		final Optional<? extends Mandate> currentMandate = mandateService.getMandateById(mandateId);
		if (!currentMandate.isPresent()) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		final Mandate mandate = mandateService.createMandateFromJson(json);
		if (!account.getId().equals(mandate.getAccountId())
				|| !mandateId.equals(mandate.getId())
				|| !currentMandate.get().getCreditorId().equals(mandate.getCreditorId())) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		
		mandateService.updateMandate(mandate);
	}
	
}
