package nl.limesco.cserv.pricing.api;

import nl.limesco.cserv.cdr.api.Cdr;

public interface SmsPricingRule extends PricingRule {

	SmsPricing getPrice();

	SmsPricing getCost();

	SmsApplicationConstraints getApplicability();
	
	long getPriceForCdr(Cdr cdr) throws PricingRuleNotApplicableException;
	
	long getCostForCdr(Cdr cdr) throws PricingRuleNotApplicableException;
	
}
