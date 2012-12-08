package nl.limesco.cserv.pricing.mongo;

import java.util.Collection;

import nl.limesco.cserv.pricing.api.DataApplicabilityFilter;

import com.google.common.base.Optional;

public class DataApplicabilityFilterImpl implements DataApplicabilityFilter {
	
	private final Collection<String> sources;
	
	public DataApplicabilityFilterImpl(Collection<String> sources) {
		this.sources = sources;
	}

	@Override
	public Optional<Collection<String>> getSources() {
		return Optional.fromNullable(sources);
	}

}
