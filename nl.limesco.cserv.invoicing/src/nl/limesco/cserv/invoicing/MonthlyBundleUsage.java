package nl.limesco.cserv.invoicing;

public class MonthlyBundleUsage {
	
	private final long bundle;
	
	private long count;

	public MonthlyBundleUsage(long bundle) {
		this.bundle = bundle;
	}
	
	public void add(long count) {
		this.count += count;
	}

	public long getBundle() {
		return bundle;
	}

	public long getCount() {
		return count;
	}

}
