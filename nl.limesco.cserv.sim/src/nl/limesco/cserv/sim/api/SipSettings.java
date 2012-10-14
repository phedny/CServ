package nl.limesco.cserv.sim.api;

public class SipSettings {
	private String hostname;
	private String realm;
	private String username;
	private String authenticationUsername;
	private String password;
	private String sipUri;
	private int expiry;
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAuthenticationUsername() {
		return authenticationUsername;
	}
	public void setAuthenticationUsername(String authenticationUsername) {
		this.authenticationUsername = authenticationUsername;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSipUri() {
		return sipUri;
	}
	public void setSipUri(String sipUri) {
		this.sipUri = sipUri;
	}
	public int getExpiry() {
		return expiry;
	}
	public void setExpiry(int expiry) {
		this.expiry = expiry;
	}
}
