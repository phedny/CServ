package nl.limesco.cserv.pricing.mongo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.DataApplicabilityFilter;
import nl.limesco.cserv.pricing.api.DataApplicabilityFilterBuilder;

import com.google.common.collect.Sets;

public class DataApplicabilityFilterBuilderImpl implements DataApplicabilityFilterBuilder {
	
	private Set<String> sources = Sets.newHashSet();
	
	@Override
	public DataApplicabilityFilterBuilder source(Any any) {
		checkState(sources != null && sources.isEmpty());
		sources = null;
		return this;
	}

	@Override
	public DataApplicabilityFilterBuilder source(String source) {
		checkNotNull(source);
		checkState(sources != null);
		sources.add(source);
		return this;
	}

	@Override
	public DataApplicabilityFilterBuilder source(String... source) {
		return source(Arrays.asList(source));
	}

	@Override
	public DataApplicabilityFilterBuilder source(Collection<String> source) {
		for (String s : source) {
			checkNotNull(s);
		}
		checkState(sources != null);
		sources.addAll(source);
		return this;
	}

	@Override
	public DataApplicabilityFilterBuilder cdr(Cdr cdr) {
		return source(cdr.getSource());
	}

	@Override
	public DataApplicabilityFilterBuilder cdr(Cdr... cdr) {
		return cdr(Arrays.asList(cdr));
	}

	@Override
	public DataApplicabilityFilterBuilder cdr(Collection<Cdr> cdr) {
		for (Cdr c : cdr) {
			cdr(c);
		}
		return this;
	}

	@Override
	public DataApplicabilityFilter build() {
		return new DataApplicabilityFilterImpl(sources);
	}

}
