package nl.limesco.cserv.pricing.api;

import java.util.Calendar;
import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

public interface PricingService {

	Collection<? extends PricingRule> getApplicablePricingRules(Calendar day);
	
	Collection<? extends PricingRule> getApplicablePricingRules(Calendar day, ApplicabilityFilter filter);
	
	Collection<? extends PricingRule> getApplicablePricingRules(Cdr cdr);

	Collection<? extends PricingRule> getApplicablePricingRules(Cdr cdr, CallConnectivityType callConnectivityType);
	
	ApplicabilityFilterBuilder buildApplicabilityFilter();

	PricingRule getApplicablePricingRule(Cdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException;
	
	long getApplicablePrice(Cdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException;

	long getApplicableCost(Cdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException;
	
}
