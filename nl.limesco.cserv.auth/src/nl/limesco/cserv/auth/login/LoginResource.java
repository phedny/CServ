package nl.limesco.cserv.auth.login;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.amdatu.auth.tokenprovider.InvalidTokenException;
import org.amdatu.auth.tokenprovider.TokenProviderException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;

@Path("auth/login")
public class LoginResource {
	
	private volatile LoginHelperService loginHelper;

	@POST
	@Path("obtainToken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String obtainToken(String json) throws IOException, TokenProviderException {
		final Optional<String> token = loginHelper.obtainToken(json);
		if (token.isPresent()) {
			return token.get();
		} else {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
	}

	@POST
	@Path("obtainToken")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String obtainToken(@FormParam("username") String username, @FormParam("password") String password) throws TokenProviderException {
		final Optional<String> token = loginHelper.obtainToken(username, password);
		if (token.isPresent()) {
			return token.get();
		} else {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
	}
	
	@GET
	@Path("username")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUsername(@QueryParam("token") String token) throws TokenProviderException, InvalidTokenException {
		final Optional<String> username = loginHelper.getUsername(token);
		if (username.isPresent()) {
			return username.get();
		} else {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
	}
	
}
