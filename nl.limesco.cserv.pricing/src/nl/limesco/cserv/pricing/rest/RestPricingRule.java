package nl.limesco.cserv.pricing.rest;

import nl.limesco.cserv.pricing.api.ApplicationConstraints;
import nl.limesco.cserv.pricing.api.Pricing;
import nl.limesco.cserv.pricing.api.PricingRule;

public class RestPricingRule {

	private final PricingRule rule;

	public RestPricingRule(PricingRule rule) {
		this.rule = rule;
	}
	
	public ApplicationConstraints getApplicability() {
		return rule.getApplicability();
	}

	public Pricing getPrice() {
		return rule.getPrice();
	}

}
