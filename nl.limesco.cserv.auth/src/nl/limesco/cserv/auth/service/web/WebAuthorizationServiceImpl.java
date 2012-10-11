package nl.limesco.cserv.auth.service.web;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.auth.api.AuthorizationException;
import nl.limesco.cserv.auth.api.AuthorizationService;
import nl.limesco.cserv.auth.api.Role;
import nl.limesco.cserv.auth.api.WebAuthorizationService;

import com.google.common.base.Optional;

public class WebAuthorizationServiceImpl implements WebAuthorizationService {
	
	private AuthorizationService authorizationService;

	@Override
	public boolean isLoggedIn(HttpServletRequest request) {
		return authorizationService.isLoggedIn(request);
	}

	@Override
	public void requireLoggedIn(HttpServletRequest request) {
		try {
			authorizationService.requiredLoggedInUsername(request);
		} catch (AuthorizationException e) {
			throw new WebApplicationException(e, Status.FORBIDDEN);
		}
	}

	@Override
	public boolean userHasRole(HttpServletRequest request, Role role) {
		return authorizationService.userHasRole(request, role);
	}

	@Override
	public void requireUserRole(HttpServletRequest request, Role role) {
		try {
			authorizationService.requireUserRole(request, role);
		} catch (AuthorizationException e) {
			throw new WebApplicationException(e, Status.FORBIDDEN);
		}
	}

	@Override
	public Optional<String> getLoggedInUsername(HttpServletRequest request) {
		return authorizationService.getLoggedInUsername(request);
	}

	@Override
	public String requiredLoggedInUsername(HttpServletRequest request) {
		try {
			return authorizationService.requiredLoggedInUsername(request);
		} catch (AuthorizationException e) {
			throw new WebApplicationException(e, Status.FORBIDDEN);
		}
	}

	@Override
	public Optional<String> getAccountId(HttpServletRequest request) {
		return authorizationService.getAccountId(request);
	}

	@Override
	public String requiredAccountId(HttpServletRequest request) {
		try {
			return authorizationService.requiredAccountId(request);
		} catch (AuthorizationException e) {
			throw new WebApplicationException(e, Status.FORBIDDEN);
		}
	}

}
