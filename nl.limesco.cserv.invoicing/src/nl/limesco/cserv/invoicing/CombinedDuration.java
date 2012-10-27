package nl.limesco.cserv.invoicing;

import nl.limesco.cserv.cdr.api.Cdr;

public class CombinedDuration {

	private long count;
	
	private long seconds;

	public long getCount() {
		return count;
	}

	public long getSeconds() {
		return seconds;
	}
	
	public void addCdr(Cdr cdr) {
		count++;
		seconds += cdr.getSeconds();
	}
	
}
