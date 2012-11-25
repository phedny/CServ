package nl.limesco.cserv.pricing.api;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

public interface ApplicabilityFilterBuilder {
	
	static final Any ANY = new Any();
	
	ApplicabilityFilterBuilder source(Any any);
	
	ApplicabilityFilterBuilder source(String source);
	
	ApplicabilityFilterBuilder source(String... source);
	
	ApplicabilityFilterBuilder source(Collection<String> source);

	ApplicabilityFilterBuilder callConnectivityType(Any any);
	
	ApplicabilityFilterBuilder callConnectivityType(CallConnectivityType callConnectivityType);
	
	ApplicabilityFilterBuilder callConnectivityType(CallConnectivityType... callConnectivityType);
	
	ApplicabilityFilterBuilder callConnectivityType(Collection<CallConnectivityType> callConnectivityType);

	ApplicabilityFilterBuilder cdrType(Any any);
	
	ApplicabilityFilterBuilder cdrType(VoiceCdr.Type cdrType);
	
	ApplicabilityFilterBuilder cdrType(VoiceCdr.Type... cdrType);
	
	ApplicabilityFilterBuilder cdrType(Collection<VoiceCdr.Type> cdrType);

	ApplicabilityFilterBuilder cdr(Cdr cdr);

	ApplicabilityFilterBuilder cdr(Cdr... cdr);

	ApplicabilityFilterBuilder cdr(Collection<Cdr> cdr);
	
	ApplicabilityFilter build();

	static final class Any {
		private Any() {
			// Prevent external instantiation.
		}
	};
	
}
