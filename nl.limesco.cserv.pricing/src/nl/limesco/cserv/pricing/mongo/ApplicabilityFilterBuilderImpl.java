package nl.limesco.cserv.pricing.mongo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.ApplicabilityFilterBuilder;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.collect.Sets;

public class ApplicabilityFilterBuilderImpl implements ApplicabilityFilterBuilder {
	
	private Set<String> sources = Sets.newHashSet();
	
	private Set<CallConnectivityType> callConnectivityTypes = Sets.newHashSet();
	
	private Set<Cdr.Type> cdrTypes = Sets.newHashSet();

	@Override
	public ApplicabilityFilterBuilder source(Any any) {
		checkState(sources != null && sources.isEmpty());
		sources = null;
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder source(String source) {
		checkNotNull(source);
		checkState(sources != null);
		sources.add(source);
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder source(String... source) {
		return source(Arrays.asList(source));
	}

	@Override
	public ApplicabilityFilterBuilder source(Collection<String> source) {
		for (String s : source) {
			checkNotNull(s);
		}
		checkState(sources != null);
		sources.addAll(source);
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder callConnectivityType(Any any) {
		checkState(callConnectivityTypes != null && callConnectivityTypes.isEmpty());
		callConnectivityTypes = null;
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder callConnectivityType(CallConnectivityType callConnectivityType) {
		checkNotNull(callConnectivityType);
		checkState(callConnectivityTypes != null);
		callConnectivityTypes.add(callConnectivityType);
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder callConnectivityType(CallConnectivityType... callConnectivityType) {
		return callConnectivityType(Arrays.asList(callConnectivityType));
	}

	@Override
	public ApplicabilityFilterBuilder callConnectivityType(Collection<CallConnectivityType> callConnectivityType) {
		for (CallConnectivityType c : callConnectivityType) {
			checkNotNull(c);
		}
		checkState(callConnectivityTypes != null);
		callConnectivityTypes.addAll(callConnectivityType);
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder cdrType(Any any) {
		checkState(cdrTypes != null && cdrTypes.isEmpty());
		cdrTypes = null;
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder cdrType(Cdr.Type cdrType) {
		checkNotNull(cdrType);
		checkState(cdrTypes != null);
		cdrTypes.add(cdrType);
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder cdrType(Cdr.Type... cdrType) {
		return cdrType(Arrays.asList(cdrType));
	}

	@Override
	public ApplicabilityFilterBuilder cdrType(Collection<Cdr.Type> cdrType) {
		for (Cdr.Type t : cdrType) {
			checkNotNull(t);
		}
		checkState(cdrType != null);
		cdrTypes.addAll(cdrType);
		return this;
	}

	@Override
	public ApplicabilityFilterBuilder cdr(Cdr cdr) {
		return source(cdr.getSource()).cdrType(cdr.getType().get());
	}

	@Override
	public ApplicabilityFilterBuilder cdr(Cdr... cdr) {
		return cdr(Arrays.asList(cdr));
	}

	@Override
	public ApplicabilityFilterBuilder cdr(Collection<Cdr> cdr) {
		for (Cdr c : cdr) {
			cdr(c);
		}
		return this;
	}

	@Override
	public ApplicabilityFilter build() {
		return new ApplicabilityFilterImpl(sources, callConnectivityTypes, cdrTypes);
	}

}
