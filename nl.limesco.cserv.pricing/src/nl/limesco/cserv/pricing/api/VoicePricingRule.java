package nl.limesco.cserv.pricing.api;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

public interface VoicePricingRule extends PricingRule {

	VoicePricing getPrice();

	VoicePricing getCost();

	VoiceApplicationConstraints getApplicability();
	
	long getPriceForCdr(Cdr cdr, CallConnectivityType callConnectivityType) throws PricingRuleNotApplicableException;

	long getCostForCdr(Cdr cdr, CallConnectivityType callConnectivityType) throws PricingRuleNotApplicableException;
	
}
