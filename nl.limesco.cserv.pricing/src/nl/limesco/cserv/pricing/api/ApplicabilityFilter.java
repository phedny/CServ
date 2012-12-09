package nl.limesco.cserv.pricing.api;

import java.util.Collection;

import com.google.common.base.Optional;

public interface ApplicabilityFilter<PR extends PricingRule> {

	Optional<Collection<String>> getSources();

}