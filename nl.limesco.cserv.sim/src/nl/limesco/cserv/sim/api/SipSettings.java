package nl.limesco.cserv.sim.api;

public class SipSettings {
	private String realm;
	private String username;
	private String authenticationUsername;
	private String password;
	private String uri;
	private String speakupTrunkPassword;
	private int expiry;
	
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
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getSpeakupTrunkPassword() {
		return speakupTrunkPassword;
	}
	public void setSpeakupTrunkPassword(String speakupTrunkPassword) {
		this.speakupTrunkPassword = speakupTrunkPassword;
	}
	public int getExpiry() {
		return expiry;
	}
	public void setExpiry(int expiry) {
		this.expiry = expiry;
	}
}
