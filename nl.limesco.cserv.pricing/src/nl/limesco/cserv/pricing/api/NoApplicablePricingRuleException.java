package nl.limesco.cserv.pricing.api;

public class NoApplicablePricingRuleException extends Exception {

	public NoApplicablePricingRuleException() {
		super();
	}

	public NoApplicablePricingRuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoApplicablePricingRuleException(String message) {
		super(message);
	}

	public NoApplicablePricingRuleException(Throwable cause) {
		super(cause);
	}

}
