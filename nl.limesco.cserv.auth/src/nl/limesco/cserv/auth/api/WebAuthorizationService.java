package nl.limesco.cserv.auth.api;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Optional;

public interface WebAuthorizationService {
	
	boolean isLoggedIn(HttpServletRequest request);
	
	void requireLoggedIn(HttpServletRequest request);
	
	boolean userHasRole(HttpServletRequest request, Role role);
	
	void requireUserRole(HttpServletRequest request, Role role);
	
	Optional<String> getLoggedInUsername(HttpServletRequest request);
	
	String requiredLoggedInUsername(HttpServletRequest request);
	
	Optional<String> getAccountId(HttpServletRequest request);
	
	String requiredAccountId(HttpServletRequest request);
		
}
