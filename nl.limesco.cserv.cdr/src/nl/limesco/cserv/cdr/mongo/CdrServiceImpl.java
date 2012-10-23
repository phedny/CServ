package nl.limesco.cserv.cdr.mongo;

import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrService;

import org.amdatu.mongo.MongoDBService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class CdrServiceImpl implements CdrService {

	private static final String COLLECTION = "cdr";

	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<Cdr, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<Cdr, String> collection = JacksonDBCollection.wrap(dbCollection, Cdr.class, String.class);
		return collection;
	}
	
	public void start() {
		collection().ensureIndex(
				new BasicDBObject().append("source", 1).append("callId", 1).append("type", 1),
				new BasicDBObject().append("unique", true));
	}

	@Override
	public void insertCdr(Cdr... cdr) {
		final Cdr[] insertCdr = new Cdr[cdr.length];
		for (int i = 0; i < cdr.length; i++) {
			insertCdr[i] = new MongoCdr(cdr[i]);
		}
		collection().insert(insertCdr);
	}

}
