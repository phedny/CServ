package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;

import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.pricing.api.ApplicabilityFilter;
import nl.limesco.cserv.pricing.api.PricingRule;

import org.amdatu.mongo.MongoDBService;

import com.google.common.base.Optional;
import com.mongodb.DBCollection;

public abstract class PricingServiceHelper<PR extends PricingRule, PRI extends AbstractPricingRuleImpl> {

	private static final String COLLECTION = "pricing";
	
	private final Class<PRI> pricingRuleImpl;
	
	private volatile MongoDBService mongoDBService;
	
	protected PricingServiceHelper(Class<PRI> pricingRuleImpl) {
		this.pricingRuleImpl = pricingRuleImpl;
	}

	protected JacksonDBCollection<PRI, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<PRI, String> collection = JacksonDBCollection.wrap(dbCollection, pricingRuleImpl, String.class);
		return collection;
	}

	public abstract Optional<? extends PR> getPricingRuleById(String id);
	
	public abstract Collection<? extends PR> getApplicablePricingRules(Calendar day, ApplicabilityFilter<PR> filter);
	
}