package nl.limesco.cserv.pricing.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.TimeZone;

import nl.limesco.cserv.pricing.api.DataApplicabilityFilter;
import nl.limesco.cserv.pricing.api.DataApplicationConstraints;
import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.mongo.DataApplicationConstraintsImpl;
import nl.limesco.cserv.pricing.mongo.DataPricingImpl;
import nl.limesco.cserv.pricing.mongo.DataPricingRuleImpl;
import nl.limesco.cserv.pricing.mongo.DataPricingServiceHelper;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

final class DataPricingServiceHelperMock extends DataPricingServiceHelper {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	@Override
	public Collection<? extends DataPricingRule> getApplicablePricingRules(final Calendar day, final DataApplicabilityFilter filter) {
		return Collections2.filter(PRICING_RULES, new Predicate<DataPricingRule>() {
			
			@Override
			public boolean apply(DataPricingRule rule) {
				final DataApplicationConstraints applicability = rule.getApplicability();
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
				
				return true;
			}
			
		});
	}

	static final Collection<DataPricingRule> PRICING_RULES = new HashSet<DataPricingRule>() {{
		
		try {
			
			add(new DataPricingRuleImpl() {{
				setApplicabilityImpl(new DataApplicationConstraintsImpl() {{
					setValidFromAsDate(DAY_FORMAT.parse("01-01-2010"));
					setNullableValidUntilAsDate(DAY_FORMAT.parse("01-01-2012"));
					setSourcesAsSet(Sets.newHashSet("source1", "source2"));
				}});
				setPriceImpl(new DataPricingImpl() {{
					setPerKilobyte(710);
				}});
				setCostImpl(new DataPricingImpl() {{
					setPerKilobyte(700);
				}});
			}});
			
			add(new DataPricingRuleImpl() {{
				setApplicabilityImpl(new DataApplicationConstraintsImpl() {{
					setValidFromAsDate(DAY_FORMAT.parse("01-01-2011"));
					setSourcesAsSet(Sets.newHashSet("source1", "source2"));
				}});
				setPriceImpl(new DataPricingImpl() {{
					setPerKilobyte(710);
				}});
				setCostImpl(new DataPricingImpl() {{
					setPerKilobyte(700);
				}});
			}});
			
		} catch (Exception e) {
		}
		
	}};
	
}