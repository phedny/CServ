package nl.limesco.cserv.sim.mongo;

import nl.limesco.cserv.sim.api.SIM;
import nl.limesco.cserv.sim.api.SIMAPNType;
import net.vz.mongodb.jackson.ObjectId;
import nl.limesco.cserv.account.api.Account;
import org.codehaus.jackson.annotate.JsonProperty;

public class SIMImpl implements SIM {
	private String imsi;
	private String puk;
	private String phoneNumber;
	private boolean activated;
	private Account owner;
	private String sipServer;
	private SIMAPNType apnType;
	
	public SIMImpl(String imsi, String puk) {
		this.imsi = imsi;
		this.puk = puk;
	}
	
	@ObjectId
	@JsonProperty("_id")
	public String getIMSI() {
		return imsi;
	}
	
	public void setIMSI(String s) {
		this.imsi = s;
	}
	
	public String getPUK() {
		return puk;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String n) {
		this.phoneNumber = n;
	}
	
	public boolean isActivated() {
		return activated;
	}
	
	public void setActivated(boolean a) {
		this.activated = a;
	}
	
	// TODO: must be possible for a SIM to have multiple corresponding accounts
	public Account getOwner() {
		return owner;
	}
	public void setOwner(Account a) {
		this.owner = a;
	}
	
	public String getSIPServer() {
		return sipServer;
	}
	public void setSIPServer(String s) {
		this.sipServer = s;
	}
	
	public SIMAPNType getAPNType() {
		return apnType;
	}
	
	public void setAPNType(SIMAPNType t) {
		this.apnType = t;
	}

}
