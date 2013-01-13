package nl.limesco.cserv.invoicing;

import nl.limesco.cserv.cdr.api.VoiceCdr;

public class CombinedDuration {

	private long count;
	
	private long seconds;

	public long getCount() {
		return count;
	}

	public long getSeconds() {
		return seconds;
	}
	
	public void addCdr(VoiceCdr cdr) {
		count++;
		seconds += cdr.getSeconds();
	}
	
}
