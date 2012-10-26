package nl.limesco.cserv.sim.api;

public enum CallConnectivityType {

	OOTB("Out-of-the-Box"), DIY("Do-it-Yourself");
	
	private String longName;
	
	CallConnectivityType(String longName) {
		this.longName = longName;
	}
	
	public String getLongName() {
		return longName;
	}
	
}
