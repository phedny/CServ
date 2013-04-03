package nl.limesco.cserv.account.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
import nl.limesco.cserv.account.api.AccountChecker;
import nl.limesco.cserv.account.api.AccountChecker.ProposedChange;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.account.api.AccountState;
import nl.limesco.cserv.auth.api.Role;
import nl.limesco.cserv.auth.api.WebAuthorizationService;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@Path("accounts")
public class AccountsResource {
	
	private final Map<String, AccountResourceExtension> extensions = Maps.newConcurrentMap();
	
	private volatile WebAuthorizationService authorizationService;
	
	private volatile AccountService accountService;

	public void extensionAdded(AccountResourceExtension extension) {
		final Path pathAnnotation = extension.getClass().getAnnotation(Path.class);
		if (pathAnnotation != null) {
			extensions.put(pathAnnotation.value(), extension);
		}
	}
	
	public void extensionRemoved(AccountResourceExtension extension) {
		final Path pathAnnotation = extension.getClass().getAnnotation(Path.class);
		if (pathAnnotation != null) {
			extensions.remove(pathAnnotation.value());
		}
	}
	
	@Path("{accountId}")
	public AccountResource getAccount(@PathParam("accountId") String id, @Context HttpServletRequest request) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		return getAccount(id, true);
	}

	@Path("~")
	public AccountResource getMyAccount(@Context HttpServletRequest request) {
		return getAccount(authorizationService.requiredAccountId(request), false);
	}
	
	@POST
	@Path("find")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<? extends Account> findAccounts(String json, @Context HttpServletRequest request) {
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
			return accounts;
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

	private AccountResource getAccount(String id, boolean admin) {
		final Optional<? extends Account> account = accountService.getAccountById(id);
		if (account.isPresent()) {
			return new AccountResource(account.get(), admin);
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}
	
	public class AccountResource {
		
		private final Account account;
		
		private final boolean admin;
		
		public AccountResource(Account account, boolean admin) {
			this.account = account;
			this.admin = admin;
		}
	
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Account getAccount() {
			return this.account;
		}
		
		@PUT
		@Consumes(MediaType.APPLICATION_JSON)
		public void updateAccount(String json) {
			if(!admin) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			try {
				Account data = accountService.createAccountFromJson(json);
				if(data.getId() == null || !data.getId().equals(account.getId())) {
					throw new WebApplicationException(Status.BAD_REQUEST);
				}
				accountService.updateAccount(data);
			} catch(JsonGenerationException e) {
				throw new WebApplicationException(e);
			} catch(JsonMappingException e) {
				throw new WebApplicationException(e);
			} catch(IOException e) {
				throw new WebApplicationException(e);
			}
		}
		
		@Path("{extension}")
		public Object getExtension(@PathParam("extension") String extensionName) {
			final AccountResourceExtension extension = extensions.get(extensionName);
			if (extension == null) {
				return null;
			} else {
				return extension.getAccountResourceExtention(account, admin);
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
		
		@GET
		@Path("validate")
		@Produces(MediaType.APPLICATION_JSON)
		public Set<ProposedChange> validateAccount() {
			if(!admin) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			
			AccountChecker c = new AccountChecker(this.account);
			c.run();
			if(c.isSound()) {
				throw new WebApplicationException(Status.NO_CONTENT);
			}
			return c.getProposedChanges();
		}
	}

}
