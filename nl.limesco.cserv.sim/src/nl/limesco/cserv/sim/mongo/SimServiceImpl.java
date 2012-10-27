package nl.limesco.cserv.sim.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimService;
import nl.limesco.cserv.sim.api.SimState;

import org.amdatu.mongo.MongoDBService;
import org.bson.types.ObjectId;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class SimServiceImpl implements SimService {

	private static final String COLLECTION = "sims";
	
	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<SimImpl, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<SimImpl, String> collection = JacksonDBCollection.wrap(dbCollection, SimImpl.class, String.class);
		return collection;
	}

	@Override
	public Collection<? extends Sim> getSimsByOwnerAccountId(String accountId) {
		checkNotNull(accountId);
		final DBCursor<SimImpl> invoiceCursor = collection().find(new BasicDBObject().append("ownerAccountId", accountId));
		return Sets.newHashSet((Iterator<SimImpl>) invoiceCursor);
	}

	@Override
	public Collection<? extends Sim> getActivatedSimsWithoutActivationInvoice() {
		final DBCursor<SimImpl> invoiceCursor = collection().find(DBQuery.and(
				DBQuery.in("state", SimState.ACTIVATED, SimState.ACTIVATION_REQUESTED),
				DBQuery.notExists("activationInvoiceId")));
		return Sets.newHashSet((Iterator<SimImpl>) invoiceCursor);
	}

	@Override
	public Collection<? extends Sim> getActivatedSimsWithoutActivationInvoiceByOwnerAccountId(String accountId) {
		final DBCursor<SimImpl> invoiceCursor = collection().find(DBQuery
				.is("ownerAccountId", accountId).and(
				DBQuery.in("state", SimState.ACTIVATED, SimState.ACTIVATION_REQUESTED),
				DBQuery.notExists("activationInvoiceId")));
		return Sets.newHashSet((Iterator<SimImpl>) invoiceCursor);
	}

	@Override
	public Collection<? extends Sim> getActivatedSimsLastInvoicedBefore(Calendar monthCal) {
		int year = monthCal.get(Calendar.YEAR);
		int month = monthCal.get(Calendar.MONTH);
		final DBCursor<SimImpl> invoiceCursor = collection().find(DBQuery.or(
				DBQuery.notExists("lastMonthlyFeesInvoice"),
				DBQuery.lessThan("lastMonthlyFeesInvoice.year", year),
				DBQuery.and(
						DBQuery.lessThanEquals("lastMonthlyFeesInvoice.year", year),
						DBQuery.lessThan("lastMonthlyFeesInvoice.month", month)
				)
			));
		return Sets.newHashSet((Iterator<SimImpl>) invoiceCursor);
	}

	@Override
	public Collection<? extends Sim> getActivatedSimsLastInvoicedBeforeByOwnerAccountId(Calendar monthCal, String accountId) {
		int year = monthCal.get(Calendar.YEAR);
		int month = monthCal.get(Calendar.MONTH);
		final DBCursor<SimImpl> invoiceCursor = collection().find(DBQuery.is("ownerAccountId", accountId).or(
				DBQuery.notExists("lastMonthlyFeesInvoice"),
				DBQuery.lessThan("lastMonthlyFeesInvoice.year", year),
				DBQuery.and(
						DBQuery.lessThanEquals("lastMonthlyFeesInvoice.year", year),
						DBQuery.lessThan("lastMonthlyFeesInvoice.month", month)
				)
			));
		return Sets.newHashSet((Iterator<SimImpl>) invoiceCursor);
	}
	
	@Override
	public Optional<? extends Sim> getSimByIccid(String iccid) {
		checkNotNull(iccid);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", new ObjectId(iccid))));
	}

	@Override
	public Sim registerSim(String iccid, String puk) {
		final SimImpl sim = new SimImpl();
		sim.setIccid(iccid);
		sim.setPuk(puk);
		collection().insert(sim);
		return sim;
	}

	@Override
	public void updateSim(Sim sim) {
		checkArgument(sim instanceof SimImpl);
		collection().updateById(sim.getIccid(), (SimImpl) sim);
	}

}
