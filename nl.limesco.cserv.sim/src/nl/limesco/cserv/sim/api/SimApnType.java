package nl.limesco.cserv.sim.api;

public enum SimApnType {
	APN_NODATA("geen internet", 28926),
	APN_500MB("500 MB", 138843),
	APN_2000MB("2000 MB", 238843);
	
	private final long monthlyPrice;
	
	private final String friendlyName;
	
	SimApnType(String friendlyName, long monthlyPrice) {
		this.friendlyName = friendlyName;
		this.monthlyPrice = monthlyPrice;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public long getMonthlyPrice() {
		return monthlyPrice;
	}

}
