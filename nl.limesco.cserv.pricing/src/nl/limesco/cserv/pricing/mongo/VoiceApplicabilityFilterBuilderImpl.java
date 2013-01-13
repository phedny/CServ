package nl.limesco.cserv.pricing.mongo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.ApplicabilityFilterBuilder.Any;
import nl.limesco.cserv.pricing.api.VoiceApplicabilityFilter;
import nl.limesco.cserv.pricing.api.VoiceApplicabilityFilterBuilder;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.collect.Sets;

public class VoiceApplicabilityFilterBuilderImpl implements VoiceApplicabilityFilterBuilder {
	
	private Set<String> sources = Sets.newHashSet();
	
	private Set<CallConnectivityType> callConnectivityTypes = Sets.newHashSet();
	
	private Set<VoiceCdr.Type> cdrTypes = Sets.newHashSet();
	
	private Set<String> destinations = Sets.newHashSet();

	@Override
	public VoiceApplicabilityFilterBuilder source(Any any) {
		checkState(sources != null && sources.isEmpty());
		sources = null;
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder source(String source) {
		checkNotNull(source);
		checkState(sources != null);
		sources.add(source);
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder source(String... source) {
		return source(Arrays.asList(source));
	}

	@Override
	public VoiceApplicabilityFilterBuilder source(Collection<String> source) {
		for (String s : source) {
			checkNotNull(s);
		}
		checkState(sources != null);
		sources.addAll(source);
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder callConnectivityType(Any any) {
		checkState(callConnectivityTypes != null && callConnectivityTypes.isEmpty());
		callConnectivityTypes = null;
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder callConnectivityType(CallConnectivityType callConnectivityType) {
		checkNotNull(callConnectivityType);
		checkState(callConnectivityTypes != null);
		callConnectivityTypes.add(callConnectivityType);
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder callConnectivityType(CallConnectivityType... callConnectivityType) {
		return callConnectivityType(Arrays.asList(callConnectivityType));
	}

	@Override
	public VoiceApplicabilityFilterBuilder callConnectivityType(Collection<CallConnectivityType> callConnectivityType) {
		for (CallConnectivityType c : callConnectivityType) {
			checkNotNull(c);
		}
		checkState(callConnectivityTypes != null);
		callConnectivityTypes.addAll(callConnectivityType);
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder cdrType(Any any) {
		checkState(cdrTypes != null && cdrTypes.isEmpty());
		cdrTypes = null;
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder cdrType(VoiceCdr.Type cdrType) {
		checkNotNull(cdrType);
		checkState(cdrTypes != null);
		cdrTypes.add(cdrType);
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder cdrType(VoiceCdr.Type... cdrType) {
		return cdrType(Arrays.asList(cdrType));
	}

	@Override
	public VoiceApplicabilityFilterBuilder cdrType(Collection<VoiceCdr.Type> cdrType) {
		for (VoiceCdr.Type t : cdrType) {
			checkNotNull(t);
		}
		checkState(cdrType != null);
		cdrTypes.addAll(cdrType);
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder destination(Any any) {
		checkState(destinations != null && destinations.isEmpty());
		destinations = null;
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder destination(String destination) {
		checkNotNull(destinations);
		checkState(destinations != null);
		destinations.add(destination);
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder destination(String... destination) {
		return destination(Arrays.asList(destination));
	}

	@Override
	public VoiceApplicabilityFilterBuilder destination(Collection<String> destination) {
		for (String t : destination) {
			checkNotNull(t);
		}
		checkState(destinations != null);
		destinations.addAll(destination);
		return this;
	}

	@Override
	public VoiceApplicabilityFilterBuilder cdr(Cdr cdr) {
		return source(cdr.getSource()).cdrType(((VoiceCdr) cdr).getType().get()).destination(((VoiceCdr) cdr).getDestination());
	}

	@Override
	public VoiceApplicabilityFilterBuilder cdr(Cdr... cdr) {
		return cdr(Arrays.asList(cdr));
	}

	@Override
	public VoiceApplicabilityFilterBuilder cdr(Collection<Cdr> cdr) {
		for (Cdr c : cdr) {
			cdr(c);
		}
		return this;
	}

	@Override
	public VoiceApplicabilityFilter build() {
		return new VoiceApplicabilityFilterImpl(sources, callConnectivityTypes, cdrTypes, destinations);
	}

}
