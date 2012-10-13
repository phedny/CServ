package nl.limesco.cserv.sim.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.sim.api.SIM;
import nl.limesco.cserv.sim.api.SIMService;

import org.amdatu.mongo.MongoDBService;
import org.bson.types.ObjectId;

import com.google.common.base.Optional;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class SIMServiceImpl implements SIMService {

	private static final String COLLECTION = "sims";
	
	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<SIMImpl, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<SIMImpl, String> collection = JacksonDBCollection.wrap(dbCollection, SIMImpl.class, String.class);
		return collection;
	}

	@Override
	public List<SIM> getSIMsFromAccount(Account a) {
		checkNotNull(a);
		List<String> sims = a.getSIMs();
		ArrayList<SIM> res = new ArrayList<SIM>();
		for(String imsi : sims) {
			Optional<? extends SIM> sim = getSIMByIMSI(imsi);
			if(sim.isPresent())
				res.add(sim.get());
		}
		return res;
	}

	@Override
	public Optional<? extends SIM> getSIMByIMSI(String imsi) {
		checkNotNull(imsi);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", new ObjectId(imsi))));
	}

	@Override
	public SIM registerSIM(String imsi, String puk) {
		final SIMImpl sim = new SIMImpl(imsi, puk);
		collection().insert(sim);
		return sim;
	}

	@Override
	public void updateSIM(SIM sim) {
		checkArgument(sim instanceof SIMImpl);
		collection().updateById(sim.getIMSI(), (SIMImpl) sim);
	}

}
