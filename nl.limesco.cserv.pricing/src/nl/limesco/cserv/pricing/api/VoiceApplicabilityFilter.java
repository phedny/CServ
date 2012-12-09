package nl.limesco.cserv.pricing.api;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Optional;

public interface VoiceApplicabilityFilter extends ApplicabilityFilter<VoicePricingRule> {
	
	Optional<Collection<CallConnectivityType>> getCallConnectivityTypes();
	
	Optional<Collection<VoiceCdr.Type>> getCdrTypes();
	
}
