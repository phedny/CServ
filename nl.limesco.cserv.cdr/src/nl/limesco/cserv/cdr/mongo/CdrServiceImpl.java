package nl.limesco.cserv.cdr.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrService;

import org.amdatu.mongo.MongoDBService;
import org.bson.types.ObjectId;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class CdrServiceImpl implements CdrService {

	private static final String COLLECTION = "cdr";

	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<MongoCdr, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<MongoCdr, String> collection = JacksonDBCollection.wrap(dbCollection, MongoCdr.class, String.class);
		return collection;
	}
	
	public void start() {
		collection().ensureIndex(
				new BasicDBObject().append("source", 1).append("callId", 1).append("type", 1),
				new BasicDBObject().append("unique", true));
	}
	
	@Override
	public Optional<MongoCdr> getCdrById(String id) {
		checkNotNull(id);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", new ObjectId(id))));
	}

	@Override
	public Iterable<MongoCdr> getCdrByCallId(String source, String callId) {
		checkNotNull(source);
		checkNotNull(callId);
		return collection().find(new BasicDBObject().append("source", source).append("callId", callId));
	}

	@Override
	public Collection<? extends Cdr> getUnpricedCdrs() {
		return Sets.newHashSet((Iterator<MongoCdr>) collection().find(DBQuery.notExists("pricing")));
	}

	@Override
	public Collection<? extends Cdr> getUninvoicedCdrs() {
		return Sets.newHashSet((Iterator<MongoCdr>) collection().find(DBQuery.notExists("invoice")));
	}

	@Override
	public void storeCdr(Cdr cdr) {
		final BasicDBObject query = new BasicDBObject()
				.append("source", cdr.getSource())
				.append("callId", cdr.getCallId());
		
		if (cdr.getType().isPresent()) {
			query.append("type", cdr.getType().get().toString());
		} else {
			query.append("type", new BasicDBObject().append("$exists", false));
		}
		
		final BasicDBObject updateObj = new BasicDBObject();
		final BasicDBObject doc = new BasicDBObject()
				.append("time", cdr.getTime().getTime())
				.append("from", cdr.getFrom())
				.append("to", cdr.getTo())
				.append("connected", cdr.isConnected())
				.append("seconds", cdr.getSeconds());
		
		if (cdr.getAccount().isPresent()) {
			doc.append("account", cdr.getAccount().get());
		} else {
			updateObj.append("$unset", new BasicDBObject().append("account", 1));
		}
		
		if (cdr.getAdditionalInfo() != null) {
			for (Entry<String, String> info : cdr.getAdditionalInfo().entrySet()) {
				doc.append("additionalInfo." + info.getKey(), info.getValue());
			}
		}
		
		updateObj.append("$set", doc);
		collection().update(query, updateObj, true /* upsert */, false);
	}

	@Override
	public void storePricingForCdr(Cdr cdr, String pricingRuleId, long price, long cost) {
		checkArgument(cdr instanceof MongoCdr);
		checkNotNull(pricingRuleId);
		
		final CdrPricingImpl pricing = new CdrPricingImpl();
		pricing.setPricingRuleId(pricingRuleId);
		pricing.setComputedPrice(price);
		pricing.setComputedCost(cost);
		
		((MongoCdr) cdr).setNullablePricing(pricing);
		
		final BasicDBObject query = new BasicDBObject()
				.append("_id", new ObjectId(((MongoCdr) cdr).getId()));
		
		final BasicDBObject doc = new BasicDBObject()
				.append("pricing.pricingRuleId", pricingRuleId)
				.append("pricing.computedPrice", price)
				.append("pricing.computedCost", cost);

		collection().update(query, new BasicDBObject("$set", doc));
	}

}
