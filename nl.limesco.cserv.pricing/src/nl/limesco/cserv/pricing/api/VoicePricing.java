package nl.limesco.cserv.pricing.api;

public interface VoicePricing extends Pricing {

	long getPerCall();
	
	long getPerMinute();
	
}
