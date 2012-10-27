package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.ApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.api.NoApplicablePricingRuleException;
import nl.limesco.cserv.pricing.api.PricingRule;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public class PricingServiceImpl implements PricingService {

	private final PricingServiceHelper helper;
	
	public PricingServiceImpl() {
		this(new PricingServiceHelper());
	}
	
	public PricingServiceImpl(PricingServiceHelper helper) {
		this.helper = helper;
	}
	
	public Object[] getComposition() {
		return new Object[] { this, helper };
	}

	@Override
	public Optional<? extends PricingRule> getPricingRuleById(String id) {
		return helper.getPricingRuleById(id);
	}

	@Override
	public Collection<? extends PricingRule> getApplicablePricingRules(Calendar day) {
		return getApplicablePricingRules(day,
				buildApplicabilityFilter()
				.source(ApplicabilityFilterBuilder.ANY)
				.callConnectivityType(ApplicabilityFilterBuilder.ANY)
				.cdrType(ApplicabilityFilterBuilder.ANY)
				.build());
	}

	@Override
	public Collection<? extends PricingRule> getApplicablePricingRules(Calendar day, ApplicabilityFilter filter) {
		return helper.getApplicablePricingRules(day, filter);
	}

	@Override
	public Collection<? extends PricingRule> getApplicablePricingRules(Cdr cdr) {
		return getApplicablePricingRules(cdr.getTime(),
				buildApplicabilityFilter()
				.callConnectivityType(ApplicabilityFilterBuilder.ANY)
				.cdr(cdr)
				.build());
	}

	@Override
	public Collection<? extends PricingRule> getApplicablePricingRules(Cdr cdr, CallConnectivityType callConnectivityType) {
		return getApplicablePricingRules(cdr.getTime(),
				buildApplicabilityFilter()
				.callConnectivityType(callConnectivityType)
				.cdr(cdr)
				.build());
	}

	@Override
	public ApplicabilityFilterBuilder buildApplicabilityFilter() {
		return new ApplicabilityFilterBuilderImpl();
	}

	@Override
	public PricingRule getApplicablePricingRule(Cdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException {
		final Collection<? extends PricingRule> rules = getApplicablePricingRules(cdr, callConnectivityType);
		if (rules.size() == 1) {
			return rules.iterator().next();
		} else {
			throw new NoApplicablePricingRuleException();
		}
	}

	@Override
	public long getApplicablePrice(Cdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException {
		try {
			return getApplicablePricingRule(cdr, callConnectivityType).getPriceForCdr(cdr, callConnectivityType);
		} catch (PricingRuleNotApplicableException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public long getApplicableCost(Cdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException {
		try {
			return getApplicablePricingRule(cdr, callConnectivityType).getCostForCdr(cdr, callConnectivityType);
		} catch (PricingRuleNotApplicableException e) {
			throw Throwables.propagate(e);
		}
	}

}
