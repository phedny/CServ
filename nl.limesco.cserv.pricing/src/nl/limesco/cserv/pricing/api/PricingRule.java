package nl.limesco.cserv.pricing.api;

public interface PricingRule {

	String getId();

	String getDescription();

	Pricing getPrice();

	Pricing getCost();

	boolean isHidden();

	ApplicationConstraints getApplicability();
	
}