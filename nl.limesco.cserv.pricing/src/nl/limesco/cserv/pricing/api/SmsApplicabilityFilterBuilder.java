package nl.limesco.cserv.pricing.api;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.SmsCdr;

public interface SmsApplicabilityFilterBuilder extends ApplicabilityFilterBuilder<SmsPricingRule> {
	
	SmsApplicabilityFilterBuilder source(Any any);
	
	SmsApplicabilityFilterBuilder source(String source);
	
	SmsApplicabilityFilterBuilder source(String... source);
	
	SmsApplicabilityFilterBuilder source(Collection<String> source);

	SmsApplicabilityFilterBuilder cdrType(Any any);
	
	SmsApplicabilityFilterBuilder cdrType(SmsCdr.Type cdrType);
	
	SmsApplicabilityFilterBuilder cdrType(SmsCdr.Type... cdrType);
	
	SmsApplicabilityFilterBuilder cdrType(Collection<SmsCdr.Type> cdrType);

	SmsApplicabilityFilterBuilder cdr(Cdr cdr);

	SmsApplicabilityFilterBuilder cdr(Cdr... cdr);

	SmsApplicabilityFilterBuilder cdr(Collection<Cdr> cdr);
	
	SmsApplicabilityFilter build();

}
