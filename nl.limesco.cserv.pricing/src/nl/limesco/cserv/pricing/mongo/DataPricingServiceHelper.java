package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.DataApplicabilityFilter;
import nl.limesco.cserv.pricing.api.DataPricingRule;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;

public class DataPricingServiceHelper extends PricingServiceHelper<DataPricingRule, DataPricingRuleImpl> {
	
	protected DataPricingServiceHelper() {
		super(DataPricingRuleImpl.class);
	}

	public void start() {
		collection().ensureIndex(new BasicDBObject()
				.append("applicability.validFrom", 1)
				.append("applicability.source", 1));
	}

	@Override
	public Optional<? extends DataPricingRule> getPricingRuleById(String id) {
		return Optional.fromNullable(collection().findOneById(id));
	}

	@Override
	public Collection<? extends DataPricingRule> getApplicablePricingRules(Calendar day, ApplicabilityFilter<DataPricingRule> filter) {
		return getApplicablePricingRules(day, (DataApplicabilityFilter) filter);
	}

	public Collection<? extends DataPricingRule> getApplicablePricingRules(Calendar day, DataApplicabilityFilter filter) {
		Query query = DBQuery.lessThanEquals("applicability.validFrom", day.getTime());
		
		if (filter.getSources().isPresent()) {
			query.in("applicability.source", filter.getSources().get());
		}
		
		query.or(DBQuery.notExists("applicability.validUntil"), DBQuery.greaterThan("applicability.validUntil", day.getTime()));
		
		return Sets.newHashSet((Iterable<DataPricingRuleImpl>) collection().find(query));
	}

}
