package nl.limesco.cserv.pricing.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.ApplicationConstraints;
import nl.limesco.cserv.pricing.api.PricingRule;
import nl.limesco.cserv.pricing.mongo.ApplicationConstraintsImpl;
import nl.limesco.cserv.pricing.mongo.PricingImpl;
import nl.limesco.cserv.pricing.mongo.PricingRuleImpl;
import nl.limesco.cserv.pricing.mongo.PricingServiceHelper;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

final class PricingServiceHelperMock extends PricingServiceHelper {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	@Override
	public Collection<? extends PricingRule> getApplicablePricingRules(final Calendar day, final ApplicabilityFilter filter) {
		return Collections2.filter(PRICING_RULES, new Predicate<PricingRule>() {
			
			@Override
			public boolean apply(PricingRule rule) {
				final ApplicationConstraints applicability = rule.getApplicability();
				if (day.before(applicability.getValidFrom()) || (applicability.getValidUntil().isPresent() && applicability.getValidUntil().get().before(day))) {
					return false;
				}
				
				if (filter.getSources().isPresent() && applicability.getSources().isPresent()
						&& Sets.intersection(
								Sets.newHashSet(filter.getSources().get()),
								Sets.newHashSet(applicability.getSources().get()))
								.isEmpty()) {
					return false;
				}
				
				if (filter.getCallConnectivityTypes().isPresent() && applicability.getCallConnectivityTypes().isPresent()
						&& Sets.intersection(
								Sets.newHashSet(filter.getCallConnectivityTypes().get()),
								Sets.newHashSet(applicability.getCallConnectivityTypes().get()))
								.isEmpty()) {
					return false;
				}
				
				if (filter.getCdrTypes().isPresent()
						&& Sets.intersection(
								Sets.newHashSet(filter.getCdrTypes().get()),
								Sets.newHashSet(applicability.getCdrTypes()))
								.isEmpty()) {
					return false;
				}
				
				return true;
			}
			
		});
	}

	static final Collection<PricingRule> PRICING_RULES = new HashSet<PricingRule>() {{
		
		try {
			
			add(new PricingRuleImpl() {{
				setApplicabilityImpl(new ApplicationConstraintsImpl() {{
					setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
					setNullableValidUntilAsDate(DAY_FORMAT.parse("01-01-2012"));
					setSourcesAsSet(Sets.newHashSet("source1", "source2"));
					setCallConnectivityTypesAsSet(Sets.newHashSet(CallConnectivityType.OOTB));
					setCdrTypesAsSet(Sets.newHashSet(Cdr.Type.EXT_EXT));
				}});
				setPriceImpl(new PricingImpl() {{
					setPerCall(400);
					setPerMinute(710);
				}});
				setCostImpl(new PricingImpl() {{
					setPerCall(300);
					setPerMinute(700);
				}});
			}});
			
			add(new PricingRuleImpl() {{
				setApplicabilityImpl(new ApplicationConstraintsImpl() {{
					setValidFromAsDate(DAY_FORMAT.parse("01-01-2011"));
					setSourcesAsSet(Sets.newHashSet("source1", "source2"));
					setCallConnectivityTypesAsSet(Sets.newHashSet(CallConnectivityType.OOTB));
					setCdrTypesAsSet(Sets.newHashSet(Cdr.Type.EXT_EXT));
				}});
				setPriceImpl(new PricingImpl() {{
					setPerCall(350);
					setPerMinute(710);
				}});
				setCostImpl(new PricingImpl() {{
					setPerCall(300);
					setPerMinute(700);
				}});
			}});
			
		} catch (Exception e) {
		}
		
	}};
	
}