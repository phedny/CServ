package nl.limesco.cserv.sim.api;

import nl.limesco.cserv.account.api.Account;

public interface SIM {
	public String getIMSI();
	public String getPUK();
	
	public String getPhoneNumber();
	public void setPhoneNumber(String n);
	
	public boolean isActivated();
	public void setActivated(boolean a);
	
	public Account getOwner();
	public void setOwner(Account a);
	
	public String getSIPServer();
	public void setSIPServer(String s);
	
	public SIMAPNType getAPNType();
	public void setAPNType(SIMAPNType t);
}

