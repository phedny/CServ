package nl.limesco.cserv.account.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceService;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@Path("accounts")
public class AccountsResource {
	
	private volatile AccountService accountService;
	
	private volatile InvoiceService invoiceService;

	@Path("{accountId}")
	public AccountSubResource getAccount(@PathParam("accountId") String id) {
		final Optional<? extends Account> account = accountService.getAccountById(id);
		if (account.isPresent()) {
			return new AccountSubResource(account.get());
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	@Path("~")
	public AccountSubResource getMyAccount() {
		return new AccountSubResource(accountService.createAccount());
	}

	public class AccountSubResource {
		
		private final Account account;
		
		public AccountSubResource(Account account) {
			this.account = account;
		}
	
		@GET
		@Path("invoices")
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
		@Path("invoices/{invoiceId}")
		@Produces(MediaType.APPLICATION_JSON)
		public String getInvoiceById(@PathParam("invoiceId") String id) {
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
			
			try {
				return new ObjectMapper().writeValueAsString(invoice);
			} catch (JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch (JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch (IOException e) {
				throw new WebApplicationException(e);
			}
		}
		
	}

}
