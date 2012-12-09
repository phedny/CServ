package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.ApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.api.DataApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.NoApplicablePricingRuleException;
import nl.limesco.cserv.pricing.api.PricingRule;
import nl.limesco.cserv.pricing.api.PricingRuleNotApplicableException;
import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.pricing.api.SmsApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.api.VoiceApplicabilityFilterBuilder;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PricingServiceImpl implements PricingService {

	@SuppressWarnings("rawtypes")
	private final Map<Class, PricingServiceHelper> helpers;
	
	public PricingServiceImpl() {
		this.helpers = Maps.newHashMap();
		helpers.put(VoicePricingRule.class, new VoicePricingServiceHelper());
		helpers.put(SmsPricingRule.class, new SmsPricingServiceHelper());
		helpers.put(DataPricingRule.class, new DataPricingServiceHelper());
	}
	
	@SuppressWarnings("rawtypes")
	public PricingServiceImpl(Map<Class, PricingServiceHelper> helpers) {
		this.helpers = Maps.newHashMap(helpers);
	}
	
	public Object[] getComposition() {
		final List<Object> composition = Lists.newArrayList((Object) this);
		composition.addAll(helpers.values());
		return composition.toArray();
	}
	
	@SuppressWarnings("unchecked")
	private <PR extends PricingRule> PricingServiceHelper<PR, ?> getHelper(Class<PR> clazz) {
		return helpers.get(clazz);
	}

	@Override
	public <T extends PricingRule> Optional<? extends T> getPricingRuleById(Class<T> clazz, String id) {
		return getHelper(clazz).getPricingRuleById(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends PricingRule> Collection<? extends T> getApplicablePricingRules(Class<T> clazz, Calendar day) {
		if (VoicePricingRule.class.isAssignableFrom(clazz)) {
			return (Collection<T>) (Object) getApplicableVoicePricingRules(day);
		} else if (SmsPricingRule.class.isAssignableFrom(clazz)) {
			return (Collection<T>) (Object) getApplicableSmsPricingRules(day);
		} else if (DataPricingRule.class.isAssignableFrom(clazz)) {
			return (Collection<T>) (Object) getApplicableDataPricingRules(day);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private Collection<? extends VoicePricingRule> getApplicableVoicePricingRules(Calendar day) {
		return getApplicablePricingRules(VoicePricingRule.class,
				day,
				buildVoiceApplicabilityFilter()
					.source(ApplicabilityFilterBuilder.ANY)
					.callConnectivityType(ApplicabilityFilterBuilder.ANY)
					.cdrType(ApplicabilityFilterBuilder.ANY)
					.build()
				);
	}

	private Collection<? extends SmsPricingRule> getApplicableSmsPricingRules(Calendar day) {
		return getApplicablePricingRules(SmsPricingRule.class,
				day,
				buildSmsApplicabilityFilter()
					.source(ApplicabilityFilterBuilder.ANY)
					.cdrType(ApplicabilityFilterBuilder.ANY)
					.build()
				);
	}

	private Collection<? extends DataPricingRule> getApplicableDataPricingRules(Calendar day) {
		return getApplicablePricingRules(DataPricingRule.class,
				day,
				buildDataApplicabilityFilter()
					.source(ApplicabilityFilterBuilder.ANY)
					.build()
				);
	}

	@Override
	public <T extends PricingRule> Collection<? extends T> getApplicablePricingRules(Class<T> clazz, Calendar day, ApplicabilityFilter<T> filter) {
		return getHelper(clazz).getApplicablePricingRules(day, filter);
	}

	@Override
	public Collection<? extends VoicePricingRule> getApplicablePricingRules(VoiceCdr cdr) {
		return getApplicablePricingRules(VoicePricingRule.class,
				cdr.getTime(),
				buildVoiceApplicabilityFilter()
					.callConnectivityType(VoiceApplicabilityFilterBuilder.ANY)
					.cdr(cdr)
					.build()
				);
	}

	@Override
	public Collection<? extends VoicePricingRule> getApplicablePricingRules(VoiceCdr cdr, CallConnectivityType callConnectivityType) {
		return getApplicablePricingRules(VoicePricingRule.class,
				cdr.getTime(),
				buildVoiceApplicabilityFilter()
					.callConnectivityType(callConnectivityType)
					.cdr(cdr)
					.build()
				);
	}

	@Override
	public Collection<? extends SmsPricingRule> getApplicablePricingRules(SmsCdr cdr) {
		return getApplicablePricingRules(SmsPricingRule.class,
				cdr.getTime(),
				buildSmsApplicabilityFilter()
					.cdr(cdr)
					.build()
				);
	}

	@Override
	public Collection<? extends DataPricingRule> getApplicablePricingRules(DataCdr cdr) {
		return getApplicablePricingRules(DataPricingRule.class,
				cdr.getTime(),
				buildDataApplicabilityFilter()
					.cdr(cdr)
					.build()
				);
	}

	@Override
	public VoiceApplicabilityFilterBuilder buildVoiceApplicabilityFilter() {
		return new VoiceApplicabilityFilterBuilderImpl();
	}

	@Override
	public SmsApplicabilityFilterBuilder buildSmsApplicabilityFilter() {
		return new SmsApplicabilityFilterBuilderImpl();
	}

	@Override
	public DataApplicabilityFilterBuilder buildDataApplicabilityFilter() {
		return new DataApplicabilityFilterBuilderImpl();
	}

	@Override
	public VoicePricingRule getApplicablePricingRule(VoiceCdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException {
		if (!cdr.getType().isPresent()) {
			throw new NoApplicablePricingRuleException();
		}
		final Collection<? extends VoicePricingRule> rules = getApplicablePricingRules(cdr, callConnectivityType);
		if (rules.size() == 1) {
			return rules.iterator().next();
		} else {
			throw new NoApplicablePricingRuleException();
		}
	}

	@Override
	public SmsPricingRule getApplicablePricingRule(SmsCdr cdr) throws NoApplicablePricingRuleException {
		if (!cdr.getType().isPresent()) {
			throw new NoApplicablePricingRuleException();
		}
		final Collection<? extends SmsPricingRule> rules = getApplicablePricingRules(cdr);
		if (rules.size() == 1) {
			return rules.iterator().next();
		} else {
			throw new NoApplicablePricingRuleException();
		}
	}

	@Override
	public DataPricingRule getApplicablePricingRule(DataCdr cdr) throws NoApplicablePricingRuleException {
		final Collection<? extends DataPricingRule> rules = getApplicablePricingRules(cdr);
		if (rules.size() == 1) {
			return rules.iterator().next();
		} else {
			throw new NoApplicablePricingRuleException();
		}
	}

	@Override
	public long getApplicablePrice(VoiceCdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException {
		try {
			return getApplicablePricingRule(cdr, callConnectivityType).getPriceForCdr(cdr, callConnectivityType);
		} catch (PricingRuleNotApplicableException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public long getApplicableCost(VoiceCdr cdr, CallConnectivityType callConnectivityType) throws NoApplicablePricingRuleException {
		try {
			return getApplicablePricingRule(cdr, callConnectivityType).getCostForCdr(cdr, callConnectivityType);
		} catch (PricingRuleNotApplicableException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public long getApplicablePrice(SmsCdr cdr) throws NoApplicablePricingRuleException {
		try {
			return getApplicablePricingRule(cdr).getPriceForCdr(cdr);
		} catch (PricingRuleNotApplicableException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public long getApplicableCost(SmsCdr cdr) throws NoApplicablePricingRuleException {
		try {
			return getApplicablePricingRule(cdr).getCostForCdr(cdr);
		} catch (PricingRuleNotApplicableException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public long getApplicablePrice(DataCdr cdr) throws NoApplicablePricingRuleException {
		try {
			return getApplicablePricingRule(cdr).getPriceForCdr(cdr);
		} catch (PricingRuleNotApplicableException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public long getApplicableCost(DataCdr cdr) throws NoApplicablePricingRuleException {
		try {
			return getApplicablePricingRule(cdr).getCostForCdr(cdr);
		} catch (PricingRuleNotApplicableException e) {
			throw Throwables.propagate(e);
		}
	}

}
