package nl.limesco.cserv.pricing.mongo;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Optional;

public class ApplicabilityFilterImpl implements ApplicabilityFilter {
	
	private final Collection<String> sources;
	
	private final Collection<CallConnectivityType> callConnectivityTypes;
	
	private final Collection<VoiceCdr.Type> cdrTypes;

	public ApplicabilityFilterImpl(Collection<String> sources, Collection<CallConnectivityType> callConnectivityTypes, Collection<VoiceCdr.Type> cdrTypes) {
		this.sources = sources;
		this.callConnectivityTypes = callConnectivityTypes;
		this.cdrTypes = cdrTypes;
	}

	@Override
	public Optional<Collection<String>> getSources() {
		return Optional.fromNullable(sources);
	}

	@Override
	public Optional<Collection<CallConnectivityType>> getCallConnectivityTypes() {
		return Optional.fromNullable(callConnectivityTypes);
	}

	@Override
	public Optional<Collection<VoiceCdr.Type>> getCdrTypes() {
		return Optional.fromNullable(cdrTypes);
	}

}
