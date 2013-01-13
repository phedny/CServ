package nl.limesco.cserv.pricing.mongo;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.pricing.api.SmsApplicabilityFilter;

import com.google.common.base.Optional;

public class SmsApplicabilityFilterImpl implements SmsApplicabilityFilter {
	
	private final Collection<String> sources;
	
	private final Collection<SmsCdr.Type> cdrTypes;

	public SmsApplicabilityFilterImpl(Collection<String> sources, Collection<SmsCdr.Type> cdrTypes) {
		this.sources = sources;
		this.cdrTypes = cdrTypes;
	}

	@Override
	public Optional<Collection<String>> getSources() {
		return Optional.fromNullable(sources);
	}

	@Override
	public Optional<Collection<SmsCdr.Type>> getCdrTypes() {
		return Optional.fromNullable(cdrTypes);
	}

}
