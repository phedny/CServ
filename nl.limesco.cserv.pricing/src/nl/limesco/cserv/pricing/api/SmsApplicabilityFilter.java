package nl.limesco.cserv.pricing.api;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.SmsCdr;

import com.google.common.base.Optional;

public interface SmsApplicabilityFilter extends ApplicabilityFilter<SmsPricingRule> {
	
	Optional<Collection<SmsCdr.Type>> getCdrTypes();
	
}
