package nl.limesco.cserv.invoice.api;


public interface QueuedItemLine extends NormalItemLine {
	
	String queuedForAccountId();
	
}
