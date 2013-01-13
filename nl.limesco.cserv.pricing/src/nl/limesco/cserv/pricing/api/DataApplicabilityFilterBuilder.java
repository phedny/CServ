package nl.limesco.cserv.pricing.api;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;

public interface DataApplicabilityFilterBuilder extends ApplicabilityFilterBuilder<DataPricingRule> {

	DataApplicabilityFilterBuilder source(Any any);

	DataApplicabilityFilterBuilder source(String source);

	DataApplicabilityFilterBuilder source(String... source);

	DataApplicabilityFilterBuilder source(Collection<String> source);

	DataApplicabilityFilterBuilder cdr(Cdr cdr);

	DataApplicabilityFilterBuilder cdr(Cdr... cdr);

	DataApplicabilityFilterBuilder cdr(Collection<Cdr> cdr);
	
	DataApplicabilityFilter build();

}
