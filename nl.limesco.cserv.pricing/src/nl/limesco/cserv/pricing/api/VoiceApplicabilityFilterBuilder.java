package nl.limesco.cserv.pricing.api;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

public interface VoiceApplicabilityFilterBuilder extends ApplicabilityFilterBuilder<VoicePricingRule> {
	
	VoiceApplicabilityFilterBuilder source(Any any);
	
	VoiceApplicabilityFilterBuilder source(String source);
	
	VoiceApplicabilityFilterBuilder source(String... source);
	
	VoiceApplicabilityFilterBuilder source(Collection<String> source);

	VoiceApplicabilityFilterBuilder callConnectivityType(Any any);
	
	VoiceApplicabilityFilterBuilder callConnectivityType(CallConnectivityType callConnectivityType);
	
	VoiceApplicabilityFilterBuilder callConnectivityType(CallConnectivityType... callConnectivityType);
	
	VoiceApplicabilityFilterBuilder callConnectivityType(Collection<CallConnectivityType> callConnectivityType);

	VoiceApplicabilityFilterBuilder destination(Any any);

	VoiceApplicabilityFilterBuilder destination(String destination);

	VoiceApplicabilityFilterBuilder destination(String... destination);

	VoiceApplicabilityFilterBuilder destination(Collection<String> destination);

	VoiceApplicabilityFilterBuilder cdrType(Any any);
	
	VoiceApplicabilityFilterBuilder cdrType(VoiceCdr.Type cdrType);
	
	VoiceApplicabilityFilterBuilder cdrType(VoiceCdr.Type... cdrType);
	
	VoiceApplicabilityFilterBuilder cdrType(Collection<VoiceCdr.Type> cdrType);

	VoiceApplicabilityFilterBuilder cdr(Cdr cdr);

	VoiceApplicabilityFilterBuilder cdr(Cdr... cdr);

	VoiceApplicabilityFilterBuilder cdr(Collection<Cdr> cdr);
	
	VoiceApplicabilityFilter build();

}
