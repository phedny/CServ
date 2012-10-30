package nl.limesco.cserv.pricing.api;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

public interface PricingRule {
	
	String getId();
	
	String getDescription();

	ApplicationConstraints getApplicability();
	
	Pricing getPrice();
	
	Pricing getCost();

	long getPriceForCdr(Cdr cdr, CallConnectivityType callConnectivityType) throws PricingRuleNotApplicableException;

	long getCostForCdr(Cdr cdr, CallConnectivityType callConnectivityType) throws PricingRuleNotApplicableException;
	
}
