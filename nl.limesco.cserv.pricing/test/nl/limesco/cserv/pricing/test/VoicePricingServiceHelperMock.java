package nl.limesco.cserv.pricing.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.VoiceApplicabilityFilter;
import nl.limesco.cserv.pricing.api.VoiceApplicationConstraints;
import nl.limesco.cserv.pricing.api.VoicePricingRule;
import nl.limesco.cserv.pricing.mongo.VoicePricingServiceHelper;
import nl.limesco.cserv.pricing.mongo.VoiceApplicationConstraintsImpl;
import nl.limesco.cserv.pricing.mongo.VoicePricingImpl;
import nl.limesco.cserv.pricing.mongo.VoicePricingRuleImpl;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

final class VoicePricingServiceHelperMock extends VoicePricingServiceHelper {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	@Override
	public Collection<? extends VoicePricingRule> getApplicablePricingRules(final Calendar day, final VoiceApplicabilityFilter filter) {
		return Collections2.filter(PRICING_RULES, new Predicate<VoicePricingRule>() {
			
			@Override
			public boolean apply(VoicePricingRule rule) {
				final VoiceApplicationConstraints applicability = rule.getApplicability();
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

	static final Collection<VoicePricingRule> PRICING_RULES = new HashSet<VoicePricingRule>() {{
		
		try {
			
			add(new VoicePricingRuleImpl() {{
				setApplicabilityImpl(new VoiceApplicationConstraintsImpl() {{
					setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
					setNullableValidUntilAsDate(DAY_FORMAT.parse("01-01-2012"));
					setSourcesAsSet(Sets.newHashSet("source1", "source2"));
					setCallConnectivityTypesAsSet(Sets.newHashSet(CallConnectivityType.OOTB));
					setCdrTypesAsSet(Sets.newHashSet(VoiceCdr.Type.EXT_EXT));
					setDestinationsAsSet(Sets.newHashSet("Middle Earth"));
				}});
				setPriceImpl(new VoicePricingImpl() {{
					setPerCall(400);
					setPerMinute(710);
				}});
				setCostImpl(new VoicePricingImpl() {{
					setPerCall(300);
					setPerMinute(700);
				}});
			}});
			
			add(new VoicePricingRuleImpl() {{
				setApplicabilityImpl(new VoiceApplicationConstraintsImpl() {{
					setValidFromAsDate(DAY_FORMAT.parse("01-01-2011"));
					setSourcesAsSet(Sets.newHashSet("source1", "source2"));
					setCallConnectivityTypesAsSet(Sets.newHashSet(CallConnectivityType.OOTB));
					setCdrTypesAsSet(Sets.newHashSet(VoiceCdr.Type.EXT_EXT));
					setDestinationsAsSet(Sets.newHashSet("Middle Earth"));
				}});
				setPriceImpl(new VoicePricingImpl() {{
					setPerCall(350);
					setPerMinute(710);
				}});
				setCostImpl(new VoicePricingImpl() {{
					setPerCall(300);
					setPerMinute(700);
				}});
			}});
			
		} catch (Exception e) {
		}
		
	}};
	
}