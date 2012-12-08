package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.SmsApplicabilityFilter;
import nl.limesco.cserv.pricing.api.SmsPricingRule;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;

public class SmsPricingServiceHelper extends PricingServiceHelper<SmsPricingRule, SmsPricingRuleImpl> {
	
	protected SmsPricingServiceHelper() {
		super(SmsPricingRuleImpl.class);
	}

	public void start() {
		collection().ensureIndex(new BasicDBObject()
				.append("applicability.validFrom", 1)
				.append("applicability.source", 1)
				.append("applicability.cdrType", 1));
	}

	@Override
	public Optional<? extends SmsPricingRule> getPricingRuleById(String id) {
		return Optional.fromNullable(collection().findOneById(id));
	}

	@Override
	public Collection<? extends SmsPricingRule> getApplicablePricingRules(Calendar day, ApplicabilityFilter<SmsPricingRule> filter) {
		return getApplicablePricingRules(day, (SmsApplicabilityFilter) filter);
	}

	public Collection<? extends SmsPricingRule> getApplicablePricingRules(Calendar day, SmsApplicabilityFilter filter) {
		Query query = DBQuery.lessThanEquals("applicability.validFrom", day.getTime());
		
		if (filter.getSources().isPresent()) {
			query.in("applicability.source", filter.getSources().get());
		}
		
		if (filter.getCdrTypes().isPresent()) {
			query.in("applicability.cdrType", filter.getCdrTypes().get());
		}
		
		query.or(DBQuery.notExists("applicability.validUntil"), DBQuery.greaterThan("applicability.validUntil", day.getTime()));
		
		return Sets.newHashSet((Iterable<SmsPricingRuleImpl>) collection().find(query));
	}

}
