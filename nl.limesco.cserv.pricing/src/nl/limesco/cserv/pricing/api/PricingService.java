package nl.limesco.cserv.pricing.api;

import java.util.Calendar;
import java.util.Collection;

import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Optional;

public interface PricingService {

	<T extends PricingRule> Optional<? extends T> getPricingRuleById(Class<T> clazz, String id);

	<T extends PricingRule> Collection<? extends T> getApplicablePricingRules(Class<T> clazz, Calendar day);

	<T extends PricingRule> Collection<? extends T> getApplicablePricingRules(Class<T> clazz, Calendar day, ApplicabilityFilter<T> filter);
	
	Collection<? extends VoicePricingRule> getApplicablePricingRules(VoiceCdr cdr);

	Collection<? extends VoicePricingRule> getApplicablePricingRules(VoiceCdr cdr, CallConnectivityType callConnectivityType);
	
	Collection<? extends SmsPricingRule> getApplicablePricingRules(SmsCdr cdr);
	
	Collection<? extends DataPricingRule> getApplicablePricingRules(DataCdr cdr);

	VoiceApplicabilityFilterBuilder buildVoiceApplicabilityFilter();

	SmsApplicabilityFilterBuilder buildSmsApplicabilityFilter();

	DataApplicabilityFilterBuilder buildDataApplicabilityFilter();

	VoicePricingRule getApplicablePricingRule(VoiceCdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException;

	SmsPricingRule getApplicablePricingRule(SmsCdr cdr) throws NoApplicablePricingRuleException;

	DataPricingRule getApplicablePricingRule(DataCdr cdr) throws NoApplicablePricingRuleException;

	long getApplicablePrice(VoiceCdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException;

	long getApplicableCost(VoiceCdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException;

	long getApplicablePrice(SmsCdr cdr) throws NoApplicablePricingRuleException;

	long getApplicableCost(SmsCdr cdr) throws NoApplicablePricingRuleException;

	long getApplicablePrice(DataCdr cdr) throws NoApplicablePricingRuleException;

	long getApplicableCost(DataCdr cdr) throws NoApplicablePricingRuleException;
	
}
