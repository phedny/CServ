package nl.limesco.cserv.pricing.api;

public class PricingRuleNotApplicableException extends Exception {

	public PricingRuleNotApplicableException() {
		super();
	}

	public PricingRuleNotApplicableException(String message, Throwable cause) {
		super(message, cause);
	}

	public PricingRuleNotApplicableException(String message) {
		super(message);
	}

	public PricingRuleNotApplicableException(Throwable cause) {
		super(cause);
	}

}
