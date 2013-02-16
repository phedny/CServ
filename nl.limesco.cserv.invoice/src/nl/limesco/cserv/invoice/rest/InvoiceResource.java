package nl.limesco.cserv.invoice.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import nl.limesco.cserv.invoice.api.IdAllocationException;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceBuilder;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.InvoiceTransformationService;
import nl.limesco.cserv.invoice.api.ItemLine;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class InvoiceResource {
	
	private final InvoiceService invoiceService;
	
	private final InvoiceTransformationService invoiceTransformationService;
	
	private final Account account;
	
	private final boolean admin;
	
	public InvoiceResource(InvoiceService invoiceService, InvoiceTransformationService invoiceTransformationService, Account account, boolean admin) {
		this.invoiceService = invoiceService;
		this.invoiceTransformationService = invoiceTransformationService;
		this.account = account;
		this.admin = admin;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getInvoices() {
		final List<SummarizedInvoice> summarizedInvoices = Lists.newArrayList();
		for (Invoice invoice : invoiceService.getInvoicesByAccountId(account.getId())) {
			if (!invoice.isSound()) {
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
			}
			summarizedInvoices.add(new SummarizedInvoice(invoice));
		}
		
		try {
			return new ObjectMapper().writeValueAsString(summarizedInvoices);
		} catch (JsonGenerationException e) {
			throw new WebApplicationException(e);
		} catch (JsonMappingException e) {
			throw new WebApplicationException(e);
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}
	
	@GET
	@Path("{invoiceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInvoiceByIdAsJson(@PathParam("invoiceId") String id) {
		return invoiceTransformationService.transformToJson(getInvoiceByIdForRest(id));
	}

	@GET
	@Path("{invoiceId}")
	@Produces("application/x-tex")
	public String getInvoiceByIdAsTex(@PathParam("invoiceId") String id) {
		return invoiceTransformationService.transformToTex(getInvoiceByIdForRest(id), account);
	}

	@GET
	@Path("{invoiceId}")
	@Produces("application/pdf")
	public byte[] getInvoiceByIdAsPdf(@PathParam("invoiceId") String id) {
		return invoiceTransformationService.transformToPdf(getInvoiceByIdForRest(id), account);
	}
	
	private Invoice getInvoiceByIdForRest(String id) {
		final Optional<? extends Invoice> optionalInvoice = invoiceService.getInvoiceById(id);
		if (!optionalInvoice.isPresent()) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		final Invoice invoice = optionalInvoice.get();
		if (!invoice.getAccountId().equals(account.getId())) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (!invoice.isSound()) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
		return invoice;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewInvoice(String json) {
		if (!admin) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		try {
			final Invoice inputInvoice = invoiceService.createInvoiceFromJson(json);
			if (inputInvoice.getId() != null) {
				// Invoice must not have an ID
				throw new WebApplicationException(Status.BAD_REQUEST);
			} else if (inputInvoice.getAccountId() != null && !inputInvoice.getAccountId().equals(account.getId())) {
				// Invoice must have either no accountId set or the correct one
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
			
			final InvoiceCurrency currency;
			if (inputInvoice.getCurrency() != null) {
				currency = inputInvoice.getCurrency();
			} else {
				currency = InvoiceCurrency.EUR; // Default currency
			}
			
			// Build the new invoice
			final InvoiceBuilder builder = invoiceService.buildInvoice()
					.accountId(account.getId())
					.currency(currency);
			
			if (inputInvoice.getItemLines() != null) {
				for (ItemLine itemLine : inputInvoice.getItemLines()) {
					builder.itemLine(itemLine);
				}
			}
			
			final Invoice invoice = builder.buildInvoice();
			invoiceService.storeInvoice(invoice);
			return Response.created(new URI(account.getId() + "/invoices/" + invoice.getId())).build();
			
		} catch (IOException e) {
			throw new WebApplicationException(e);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e);
		} catch (IdAllocationException e) {
			throw new WebApplicationException(e, Status.CONFLICT);
		}
	}
	
}
