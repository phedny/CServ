package nl.limesco.cserv.pricing.api;

import nl.limesco.cserv.cdr.api.Cdr;

public interface DataPricingRule extends PricingRule {

	DataPricing getPrice();

	DataPricing getCost();

	DataApplicationConstraints getApplicability();
	
	long getPriceForCdr(Cdr cdr) throws PricingRuleNotApplicableException;
	
	long getCostForCdr(Cdr cdr) throws PricingRuleNotApplicableException;
	
}
