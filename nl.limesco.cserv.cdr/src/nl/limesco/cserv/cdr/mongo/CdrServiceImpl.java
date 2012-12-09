package nl.limesco.cserv.cdr.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrService;
import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;

import org.amdatu.mongo.MongoDBService;
import org.bson.types.ObjectId;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class CdrServiceImpl implements CdrService {

	private static final String COLLECTION = "cdr";

	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<AbstractMongoCdr, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<AbstractMongoCdr, String> collection = JacksonDBCollection.wrap(dbCollection, AbstractMongoCdr.class, String.class);
		return collection;
	}
	
	public void start() {
		collection().ensureIndex(
				new BasicDBObject().append("source", 1).append("callId", 1).append("type", 1),
				new BasicDBObject().append("unique", true));
		
		collection().ensureIndex(new BasicDBObject().append("account", 1));
		collection().ensureIndex(new BasicDBObject().append("invoice", 1));
		collection().ensureIndex(new BasicDBObject().append("invoiceBuilder", 1));
	}
	
	@Override
	public Optional<AbstractMongoCdr> getCdrById(String id) {
		checkNotNull(id);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", new ObjectId(id))));
	}

	@Override
	public Iterable<AbstractMongoCdr> getCdrByCallId(String source, String callId) {
		checkNotNull(source);
		checkNotNull(callId);
		return collection().find(new BasicDBObject().append("source", source).append("callId", callId));
	}

	@Override
	public Collection<? extends Cdr> getUnpricedCdrs() {
		return Sets.newHashSet((Iterator<AbstractMongoCdr>) collection().find(DBQuery.notExists("pricing")));
	}

	@Override
	public Collection<? extends Cdr> getUninvoicedCdrs() {
		return Sets.newHashSet((Iterator<AbstractMongoCdr>) collection().find(DBQuery.notExists("invoice")));
	}

	@Override
	public Collection<? extends Cdr> getUninvoicedCdrsForAccount(String account, String builder) {
		collection().update(DBQuery.is("account", account).notExists("invoice"),
				new BasicDBObject("$set", new BasicDBObject("invoiceBuilder", builder)),
				false, true /* multi */);
		return Sets.newHashSet((Iterator<AbstractMongoCdr>) collection().find(DBQuery.is("invoiceBuilder", builder)));
	}

	@Override
	public void storeCdr(Cdr cdr) {
		if (cdr instanceof VoiceCdr) {
			collection().insert(new MongoVoiceCdr((VoiceCdr) cdr));
		} else if (cdr instanceof SmsCdr) {
			collection().insert(new MongoSmsCdr((SmsCdr) cdr));
		} else if (cdr instanceof DataCdr) {
			collection().insert(new MongoDataCdr((DataCdr) cdr));
		}
		
		/*
		final BasicDBObject query = new BasicDBObject()
				.append("source", cdr.getSource())
				.append("callId", cdr.getCallId());
		
		if (((VoiceCdr) cdr).getType().isPresent()) {
			query.append("type", ((VoiceCdr) cdr).getType().get().toString());
		} else {
			query.append("type", new BasicDBObject().append("$exists", false));
		}
		
		final BasicDBObject updateObj = new BasicDBObject();
		final BasicDBObject doc = new BasicDBObject()
				.append("time", cdr.getTime().getTime())
				.append("from", cdr.getFrom())
				.append("to", cdr.getTo())
				.append("connected", ((VoiceCdr) cdr).isConnected())
				.append("seconds", ((VoiceCdr) cdr).getSeconds());
		
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
		collection().update(query, updateObj, true, false);
		*/
	}

	@Override
	public void storePricingForCdr(Cdr cdr, String pricingRuleId, long price, long cost) {
		checkArgument(cdr instanceof AbstractMongoCdr);
		checkNotNull(pricingRuleId);
		
		final CdrPricingImpl pricing = new CdrPricingImpl();
		pricing.setPricingRuleId(pricingRuleId);
		pricing.setComputedPrice(price);
		pricing.setComputedCost(cost);
		
		((AbstractMongoCdr) cdr).setNullablePricing(pricing);
		
		final BasicDBObject query = new BasicDBObject()
				.append("_id", new ObjectId(((AbstractMongoCdr) cdr).getId()));
		
		final BasicDBObject doc = new BasicDBObject()
				.append("pricing.pricingRuleId", pricingRuleId)
				.append("pricing.computedPrice", price)
				.append("pricing.computedCost", cost);

		collection().update(query, new BasicDBObject("$set", doc));
	}

	@Override
	public void setInvoiceIdForBuilder(String builder, String invoiceId) {
		checkNotNull(builder);
		checkNotNull(invoiceId);
		collection().update(DBQuery.is("invoiceBuilder", builder),
				new BasicDBObject("$set", new BasicDBObject("invoice", invoiceId))
						.append("$unset", new BasicDBObject("invoiceBuilder", 1)),
				false, true /* multi */);
	}

}
