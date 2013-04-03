package nl.limesco.cserv.payment.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.payment.api.Payment;
import nl.limesco.cserv.payment.api.PaymentBuilder;
import nl.limesco.cserv.payment.api.PaymentService;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;

public class PaymentResource {
	
	private final PaymentService paymentService;
	
	private final Account account;
	
	private final boolean admin;

	public PaymentResource(PaymentService paymentService, Account account, boolean admin) {
		this.paymentService = paymentService;
		this.account = account;
		this.admin = admin;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<? extends Payment> getPayments() {
		return paymentService.getPaymentsByAccountId(account.getId());
	}
	
	@GET
	@Path("{paymentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Payment getPaymentById(@PathParam("paymentId") String id) {
		final Optional<? extends Payment> optionalPayment = paymentService.getPaymentById(id);
		if (!optionalPayment.isPresent()) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		final Payment payment = optionalPayment.get();
		if (!payment.getAccountId().equals(account.getId())) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		return payment;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewPayment(String json) {
		if (!admin) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		try {
			final Payment inputPayment = paymentService.createPaymentFromJson(json);
			if (inputPayment.getId() != null) {
				// Object must not have an ID
				throw new WebApplicationException(Status.BAD_REQUEST);
			} else if (inputPayment.getAccountId() != null && !inputPayment.getAccountId().equals(account.getId())) {
				// Invoice must have either no accountId set or the correct one
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
			
			final InvoiceCurrency currency;
			if (inputPayment.getCurrency() != null) {
				currency = inputPayment.getCurrency();
			} else {
				currency = InvoiceCurrency.EUR; // Default currency
			}
			
			// Build the new invoice
			final PaymentBuilder builder = paymentService.buildPayment(inputPayment)
					.accountId(account.getId())
					.currency(currency);
			
			final Payment payment = builder.build();
			paymentService.updatePayment(payment);
			return Response.created(new URI(account.getId() + "/payments/" + payment.getId())).build();
			
		} catch (IOException e) {
			throw new WebApplicationException(e);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e);
		}
	}
	
}
