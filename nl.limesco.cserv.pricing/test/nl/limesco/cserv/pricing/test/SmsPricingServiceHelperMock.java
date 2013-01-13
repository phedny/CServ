package nl.limesco.cserv.pricing.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.pricing.api.SmsApplicabilityFilter;
import nl.limesco.cserv.pricing.api.SmsApplicationConstraints;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.mongo.SmsApplicationConstraintsImpl;
import nl.limesco.cserv.pricing.mongo.SmsPricingImpl;
import nl.limesco.cserv.pricing.mongo.SmsPricingRuleImpl;
import nl.limesco.cserv.pricing.mongo.SmsPricingServiceHelper;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

final class SmsPricingServiceHelperMock extends SmsPricingServiceHelper {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	@Override
	public Collection<? extends SmsPricingRule> getApplicablePricingRules(final Calendar day, final SmsApplicabilityFilter filter) {
		return Collections2.filter(PRICING_RULES, new Predicate<SmsPricingRule>() {
			
			@Override
			public boolean apply(SmsPricingRule rule) {
				final SmsApplicationConstraints applicability = rule.getApplicability();
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

	static final Collection<SmsPricingRule> PRICING_RULES = new HashSet<SmsPricingRule>() {{
		
		try {
			
			add(new SmsPricingRuleImpl() {{
				setApplicabilityImpl(new SmsApplicationConstraintsImpl() {{
					setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
					setNullableValidUntilAsDate(DAY_FORMAT.parse("01-01-2012"));
					setSourcesAsSet(Sets.newHashSet("source1", "source2"));
					setCdrTypesAsSet(Sets.newHashSet(SmsCdr.Type.MOBILE_EXT));
				}});
				setPriceImpl(new SmsPricingImpl() {{
					setPerSms(710);
				}});
				setCostImpl(new SmsPricingImpl() {{
					setPerSms(700);
				}});
			}});
			
			add(new SmsPricingRuleImpl() {{
				setApplicabilityImpl(new SmsApplicationConstraintsImpl() {{
					setValidFromAsDate(DAY_FORMAT.parse("01-01-2011"));
					setSourcesAsSet(Sets.newHashSet("source1", "source2"));
					setCdrTypesAsSet(Sets.newHashSet(SmsCdr.Type.MOBILE_EXT));
				}});
				setPriceImpl(new SmsPricingImpl() {{
					setPerSms(710);
				}});
				setCostImpl(new SmsPricingImpl() {{
					setPerSms(700);
				}});
			}});
			
		} catch (Exception e) {
		}
		
	}};
	
}