package nl.limesco.cserv.pricing.rest;

import nl.limesco.cserv.pricing.api.ApplicationConstraints;
import nl.limesco.cserv.pricing.api.Pricing;
import nl.limesco.cserv.pricing.api.VoicePricingRule;

public class RestPricingRule {

	private final VoicePricingRule rule;

	public RestPricingRule(VoicePricingRule rule) {
		this.rule = rule;
	}
	
	public ApplicationConstraints getApplicability() {
		return rule.getApplicability();
	}

	public Pricing getPrice() {
		return rule.getPrice();
	}

}
