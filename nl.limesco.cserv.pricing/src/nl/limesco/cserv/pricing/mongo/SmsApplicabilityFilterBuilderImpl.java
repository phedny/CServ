package nl.limesco.cserv.pricing.mongo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.pricing.api.SmsApplicabilityFilter;
import nl.limesco.cserv.pricing.api.SmsApplicabilityFilterBuilder;

import com.google.common.collect.Sets;

public class SmsApplicabilityFilterBuilderImpl implements SmsApplicabilityFilterBuilder {
	
	private Set<String> sources = Sets.newHashSet();
	
	private Set<SmsCdr.Type> cdrTypes = Sets.newHashSet();

	@Override
	public SmsApplicabilityFilterBuilder source(Any any) {
		checkState(sources != null && sources.isEmpty());
		sources = null;
		return this;
	}

	@Override
	public SmsApplicabilityFilterBuilder source(String source) {
		checkNotNull(source);
		checkState(sources != null);
		sources.add(source);
		return this;
	}

	@Override
	public SmsApplicabilityFilterBuilder source(String... source) {
		return source(Arrays.asList(source));
	}

	@Override
	public SmsApplicabilityFilterBuilder source(Collection<String> source) {
		for (String s : source) {
			checkNotNull(s);
		}
		checkState(sources != null);
		sources.addAll(source);
		return this;
	}

	@Override
	public SmsApplicabilityFilterBuilder cdrType(Any any) {
		checkState(cdrTypes != null && cdrTypes.isEmpty());
		cdrTypes = null;
		return this;
	}

	@Override
	public SmsApplicabilityFilterBuilder cdrType(SmsCdr.Type cdrType) {
		checkNotNull(cdrType);
		checkState(cdrTypes != null);
		cdrTypes.add(cdrType);
		return this;
	}

	@Override
	public SmsApplicabilityFilterBuilder cdrType(SmsCdr.Type... cdrType) {
		return cdrType(Arrays.asList(cdrType));
	}

	@Override
	public SmsApplicabilityFilterBuilder cdrType(Collection<SmsCdr.Type> cdrType) {
		for (SmsCdr.Type t : cdrType) {
			checkNotNull(t);
		}
		checkState(cdrType != null);
		cdrTypes.addAll(cdrType);
		return this;
	}

	@Override
	public SmsApplicabilityFilterBuilder cdr(Cdr cdr) {
		return source(cdr.getSource()).cdrType(((SmsCdr) cdr).getType().get());
	}

	@Override
	public SmsApplicabilityFilterBuilder cdr(Cdr... cdr) {
		return cdr(Arrays.asList(cdr));
	}

	@Override
	public SmsApplicabilityFilterBuilder cdr(Collection<Cdr> cdr) {
		for (Cdr c : cdr) {
			cdr(c);
		}
		return this;
	}

	@Override
	public SmsApplicabilityFilter build() {
		return new SmsApplicabilityFilterImpl(sources, cdrTypes);
	}

}
