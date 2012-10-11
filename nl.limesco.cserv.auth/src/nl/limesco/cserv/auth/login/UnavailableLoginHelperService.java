package nl.limesco.cserv.auth.login;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.google.common.base.Optional;

public class UnavailableLoginHelperService implements LoginHelperService {

	@Override
	public Optional<String> obtainToken(String json) {
		throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
	}

	@Override
	public Optional<String> obtainToken(String username, String password) {
		throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
	}

	@Override
	public Optional<String> getUsername(String token) {
		throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
	}

}
