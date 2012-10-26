package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.PricingRule;

import org.amdatu.mongo.MongoDBService;

import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class PricingServiceHelper {
	
	private static final String COLLECTION = "pricing";

	private volatile MongoDBService mongoDBService;
	
	private JacksonDBCollection<PricingRuleImpl, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<PricingRuleImpl, String> collection = JacksonDBCollection.wrap(dbCollection, PricingRuleImpl.class, String.class);
		return collection;
	}

	public void start() {
		collection().ensureIndex(new BasicDBObject()
				.append("applicability.validFrom", 1)
				.append("applicability.source", 1)
				.append("applicability.callConnectivityType", 1)
				.append("applicability.cdrType", 1));
	}

	public Collection<? extends PricingRule> getApplicablePricingRules(Calendar day, ApplicabilityFilter filter) {
		Query query = DBQuery.lessThanEquals("applicability.validFrom", day.getTime());
		
		if (filter.getSources().isPresent()) {
			query.in("applicability.source", filter.getSources().get());
		}
		
		if (filter.getCallConnectivityTypes().isPresent()) {
			query.in("applicability.callConnectivityType", filter.getCallConnectivityTypes().get());
		}
		
		if (filter.getCdrTypes().isPresent()) {
			query.in("applicability.cdrType", filter.getCdrTypes().get());
		}
		
		query.or(DBQuery.notExists("applicability.validUntil"), DBQuery.greaterThan("applicability.validUntil", day.getTime()));
		
		return Sets.newHashSet((Iterable<PricingRuleImpl>) collection().find(query));
	}

}
