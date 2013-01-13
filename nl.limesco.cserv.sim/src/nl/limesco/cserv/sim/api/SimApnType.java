package nl.limesco.cserv.sim.api;

public enum SimApnType {
	APN_NODATA("geen internet", 28926, 0),
	APN_500MB("500 MB", 138843, 500 * 1024),
	APN_2000MB("2000 MB", 238843, 2000 * 1024);
	
	private final long monthlyPrice;
	
	private final long bundleKilobytes;
	
	private final String friendlyName;
	
	SimApnType(String friendlyName, long monthlyPrice, long bundleKilobytes) {
		this.friendlyName = friendlyName;
		this.monthlyPrice = monthlyPrice;
		this.bundleKilobytes = bundleKilobytes;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public long getMonthlyPrice() {
		return monthlyPrice;
	}

	public long getBundleKilobytes() {
		return bundleKilobytes;
	}

}
