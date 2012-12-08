package nl.limesco.cserv.pricing.api;

import java.util.Collection;

public interface ApplicabilityFilterBuilder<PR extends PricingRule> {

	static final Any ANY = new Any();

	ApplicabilityFilterBuilder<PR> source(Any any);

	ApplicabilityFilterBuilder<PR> source(String source);

	ApplicabilityFilterBuilder<PR> source(String... source);

	ApplicabilityFilterBuilder<PR> source(Collection<String> source);

	ApplicabilityFilter<PR> build();

	static final class Any {
		private Any() {
			// Prevent external instantiation.
		}
	};
	
}