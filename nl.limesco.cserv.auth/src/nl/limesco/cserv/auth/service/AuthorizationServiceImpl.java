package nl.limesco.cserv.auth.service;

import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import nl.limesco.cserv.auth.api.AuthorizationException;
import nl.limesco.cserv.auth.api.AuthorizationService;
import nl.limesco.cserv.auth.api.Role;

import org.amdatu.auth.tokenprovider.InvalidTokenException;
import org.amdatu.auth.tokenprovider.TokenProvider;
import org.amdatu.auth.tokenprovider.TokenProviderException;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import com.google.common.base.Optional;

public class AuthorizationServiceImpl implements AuthorizationService {
	
	private volatile UserAdmin userAdmin;
	
	private volatile TokenProvider tokenProvider;
	
	private Optional<String> getTokenFromRequest(HttpServletRequest request) {
		final String headerToken = request.getHeader("X-Limesco-Token");
		if (headerToken != null) {
			return Optional.of(headerToken);
		}
		
		return Optional.absent();
	}
	
	private Optional<SortedMap<String, String>> getTokenPropertiesFromRequest(HttpServletRequest request) {
		final Optional<String> token = getTokenFromRequest(request);
		if (token.isPresent()) {
			try {
				final SortedMap<String, String> tokenProperties = tokenProvider.verifyToken(token.get());
				if (tokenProperties == null) {
					return Optional.absent();
				} else {
					return Optional.of(tokenProperties);
				}
			} catch (TokenProviderException e) {
				return Optional.absent();
			} catch (InvalidTokenException e) {
				return Optional.absent();
			}
		} else {
			return Optional.absent();
		}
	}
	
	private Optional<User> getLoggedInUser(HttpServletRequest request) {
		final Optional<String> username = getLoggedInUsername(request);
		if (username.isPresent()) {
			final org.osgi.service.useradmin.Role role = userAdmin.getRole(username.get());
			if (role.getType() == org.osgi.service.useradmin.Role.USER) {
				return Optional.of((User) role);
			}
		}
		return Optional.absent();
	}
	
	@Override
	public boolean isLoggedIn(HttpServletRequest request) {
		final Optional<SortedMap<String, String>> tokenProperties = getTokenPropertiesFromRequest(request);
		return tokenProperties.isPresent();
	}

	@Override
	public void requireLoggedIn(HttpServletRequest request) {
		if (!isLoggedIn(request)) {
			throw new AuthorizationException();
		}
	}

	@Override
	public boolean userHasRole(HttpServletRequest request, Role role) {
		final Optional<User> user = getLoggedInUser(request);
		if (user.isPresent()) {
			final Group group = (Group) userAdmin.getRole(role.toString());
			final org.osgi.service.useradmin.Role[] members = group.getMembers();
			if (members != null) {
				for (org.osgi.service.useradmin.Role member : members) {
					if (member.equals(user.get())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void requireUserRole(HttpServletRequest request, Role role) {
		if (!userHasRole(request, role)) {
			throw new AuthorizationException();
		}
	}

	@Override
	public Optional<String> getLoggedInUsername(HttpServletRequest request) {
		final Optional<SortedMap<String, String>> tokenProperties = getTokenPropertiesFromRequest(request);
		if (tokenProperties.isPresent()) {
			return Optional.of(tokenProperties.get().get(TokenProvider.USERNAME));
		} else {
			return Optional.absent();
		}
	}

	@Override
	public String requiredLoggedInUsername(HttpServletRequest request) throws AuthorizationException {
		final Optional<String> username = getLoggedInUsername(request);
		if (username.isPresent()) {
			return username.get();
		} else {
			throw new AuthorizationException();
		}
	}

	@Override
	public Optional<String> getAccountId(HttpServletRequest request) {
		final Optional<User> loggedInUser = getLoggedInUser(request);
		if (loggedInUser.isPresent()) {
			return Optional.fromNullable((String) loggedInUser.get().getProperties().get(AuthorizationService.ACCOUNT_ID_KEY));
		} else {
			return Optional.absent();
		}
	}

	@Override
	public String requiredAccountId(HttpServletRequest request) throws AuthorizationException {
		final Optional<String> accountId = getAccountId(request);
		if (accountId.isPresent()) {
			return accountId.get();
		} else {
			throw new AuthorizationException();
		}
	}

}
