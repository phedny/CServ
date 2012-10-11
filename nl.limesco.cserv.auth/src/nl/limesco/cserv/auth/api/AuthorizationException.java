package nl.limesco.cserv.auth.api;

public class AuthorizationException extends RuntimeException {

	public AuthorizationException() {
	}

	public AuthorizationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthorizationException(String message) {
		super(message);
	}

	public AuthorizationException(Throwable cause) {
		super(cause);
	}
	
}
