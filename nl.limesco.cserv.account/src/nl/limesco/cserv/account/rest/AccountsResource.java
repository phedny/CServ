package nl.limesco.cserv.account.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.account.api.AccountState;
import nl.limesco.cserv.auth.api.Role;
import nl.limesco.cserv.auth.api.WebAuthorizationService;
import nl.limesco.cserv.invoice.api.IdAllocationException;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceBuilder;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.ItemLine;
import nl.limesco.cserv.payment.api.Payment;
import nl.limesco.cserv.payment.api.PaymentBuilder;
import nl.limesco.cserv.payment.api.PaymentService;
import nl.limesco.cserv.util.pdflatex.PdfLatex;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@Path("accounts")
public class AccountsResource {
	
	private volatile WebAuthorizationService authorizationService;
	
	private volatile PdfLatex pdfLatex;
	
	private volatile AccountService accountService;
	
	private volatile InvoiceService invoiceService;
	
	private volatile PaymentService paymentService;

	@Path("{accountId}")
	public AccountSubResource getAccount(@PathParam("accountId") String id, @Context HttpServletRequest request) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		return getAccount(id, true);
	}

	@Path("~")
	public AccountSubResource getMyAccount(@Context HttpServletRequest request) {
		return getAccount(authorizationService.requiredAccountId(request), false);
	}
	
	@POST
	@Path("find")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String findAccounts(String json, @Context HttpServletRequest request) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String,String> req = om.readValue(json, Map.class);
			// TODO: understand more keys
			final Collection<? extends Account> accounts;
			if(req.size() == 0) {
				accounts = accountService.getAllAccounts();
			} else if(req.size() == 1 && req.containsKey("email")) {
				accounts = accountService.getAccountByEmail(req.get("email"));
			} else {
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
			return om.writeValueAsString(accounts);
		} catch(JsonGenerationException e) {
			throw new WebApplicationException(e);
		} catch(JsonMappingException e) {
			throw new WebApplicationException(e);
		} catch(IOException e) {
			throw new WebApplicationException(e);
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewAccount(String json, @Context HttpServletRequest request) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		
		try {
			final Account newAccount = accountService.createAccountFromJson(json);
			if (newAccount.getId() != null) {
				// Account must not have an ID
				throw new WebApplicationException(Status.BAD_REQUEST);
			}
			accountService.updateAccount(newAccount);
			return Response.created(new URI(newAccount.getId())).build();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e);
		}
	}


	private AccountSubResource getAccount(String id, boolean admin) {
		final Optional<? extends Account> account = accountService.getAccountById(id);
		if (account.isPresent()) {
			return new AccountSubResource(account.get(), admin);
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	public class AccountSubResource {
		
		private final Account account;
		
		private final boolean admin;
		
		public AccountSubResource(Account account, boolean admin) {
			this.account = account;
			this.admin = admin;
		}
	
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public String getAccount() {
			try {
				return new ObjectMapper().writeValueAsString(this.account);
			} catch(JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch(JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch(IOException e) {
				throw new WebApplicationException(e);
			}
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
		public String getInvoiceByIdAsJson(@PathParam("invoiceId") String id) {
			final Invoice invoice = getInvoiceByIdForRest(id);
			
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

		@GET
		@Path("invoices/{invoiceId}")
		@Produces("application/x-tex")
		public String getInvoiceByIdAsTex(@PathParam("invoiceId") String id) throws IOException {
			final Invoice invoice = getInvoiceByIdForRest(id);
			
			final VelocityEngine engine = new VelocityEngine();
			engine.setProperty(Velocity.RESOURCE_LOADER, "string");
			engine.setProperty("string.resource.loader.class", StringResourceLoader.class.getName());
			engine.init();

			final StringResourceRepository repository = StringResourceLoader.getRepository();
			repository.putStringResource("invoice.tex", getFileContents("resources/invoice.tex"));
			
			final Template template = engine.getTemplate("invoice.tex");
			final VelocityContext context = new VelocityContext();
			context.put("invoice", invoice);
			context.put("account", account);
			context.put("util", VelocityUtils.class);
			
			final StringWriter writer = new StringWriter();
			template.merge(context, writer);
			return writer.toString();
		}

		@GET
		@Path("invoices/{invoiceId}")
		@Produces("application/pdf")
		public byte[] getInvoiceByIdAsPdf(@PathParam("invoiceId") String id) throws IOException, InterruptedException {
			final String invoiceAsTex = getInvoiceByIdAsTex(id);
			return pdfLatex.compile(invoiceAsTex);
		}
		
		public String getFileContents(String filename) throws IOException {
			final StringWriter writer = new StringWriter();
			final Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename), "UTF-8");
			try {
				final char buffer[] = new char[1024];
				int chars;
				while ((chars = reader.read(buffer)) > 0) {
					writer.write(buffer, 0, chars);
				}
			} finally {
				reader.close();
			}
			return writer.toString();
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
		@Path("invoices")
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
				
				final Invoice invoice = builder.build();
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
		
		@GET
		@Path("payments")
		@Produces(MediaType.APPLICATION_JSON)
		public String getPayments() {
			try {
				return new ObjectMapper().writeValueAsString(paymentService.getPaymentsByAccountId(account.getId()));
			} catch(JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch(JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch(IOException e) {
				throw new WebApplicationException(e);
			}
		}
		
		@GET
		@Path("payments/{paymentId}")
		@Produces(MediaType.APPLICATION_JSON)
		public String getPaymentById(@PathParam("paymentId") String id) {
			final Optional<? extends Payment> optionalPayment = paymentService.getPaymentById(id);
			if (!optionalPayment.isPresent()) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			
			final Payment payment = optionalPayment.get();
			if (!payment.getAccountId().equals(account.getId())) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			
			try {
				return new ObjectMapper().writeValueAsString(payment);
			} catch (JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch (JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch (IOException e) {
				throw new WebApplicationException(e);
			}
		}
		
		@POST
		@Path("payments")
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
		
		@POST
		@Path("addExternalAccounts")
		@Consumes(MediaType.APPLICATION_JSON)
		public void addExternalAccounts(String json) {
			if (!admin) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			
			try {
				ObjectMapper om = new ObjectMapper();
				Map<String,String> req = om.readValue(json, Map.class);
				Map<String,String> externalAccounts = account.getExternalAccounts();
				externalAccounts.putAll(req);
				account.setExternalAccounts(externalAccounts);
				accountService.updateAccount(account);
			} catch(JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch(JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch(IOException e) {
				throw new WebApplicationException(e);
			}
		}
		
		@POST
		@Path("setState")
		@Consumes(MediaType.APPLICATION_JSON)
		public void setConfirmed(String json) {
			if(!admin) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			
			try {
				Map<String,String> req = new ObjectMapper().readValue(json, Map.class);
				if(req.containsKey("state")) {
					AccountState state = AccountState.valueOf(req.get("state"));
					account.setState(state);
					accountService.updateAccount(account);
				}
			} catch (JsonParseException e) {
				throw new WebApplicationException(e);
			} catch (JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch (IOException e) {
				throw new WebApplicationException(e);
			}
		}
	}

}
