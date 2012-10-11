package nl.limesco.cserv.auth.api;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Optional;

public interface AuthorizationService {
	
	static final String ACCOUNT_ID_KEY = "nl.limesco.cserv.auth.accountId";

	boolean isLoggedIn(HttpServletRequest request);
	
	void requireLoggedIn(HttpServletRequest request) throws AuthorizationException;
	
	boolean userHasRole(HttpServletRequest request, Role role);
	
	void requireUserRole(HttpServletRequest request, Role role) throws AuthorizationException;
	
	Optional<String> getLoggedInUsername(HttpServletRequest request);
	
	String requiredLoggedInUsername(HttpServletRequest request) throws AuthorizationException;
	
	Optional<String> getAccountId(HttpServletRequest request);

	String requiredAccountId(HttpServletRequest request) throws AuthorizationException;
	
}
