package nl.limesco.cserv.payment.directdebit.mongo;

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.payment.directdebit.api.Mandate;
import nl.limesco.cserv.payment.directdebit.api.MandateService;

import org.amdatu.mongo.MongoDBService;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.mongodb.DBCollection;

public class MongoMandateService implements MandateService, ManagedService {

	public static final String PID = "nl.limesco.cserv.payment.directdebit";

	private static final String COLLECTION = "ddmandate";
	
	private volatile MongoDBService mongoDBService;
	
	private volatile String creditorId;

	private JacksonDBCollection<MongoMandate, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<MongoMandate, String> collection = JacksonDBCollection.wrap(dbCollection, MongoMandate.class, String.class);
		return collection;
	}
	
	@Override
	public Optional<? extends Mandate> getMandateById(String mandateId) {
		return Optional.of(collection().findOneById(mandateId));
	}

	@Override
	public Collection<? extends Mandate> getMandatesForAccount(String accountId) {
		return Sets.newHashSet((Iterator<MongoMandate>) collection().find(DBQuery.is("accountId", accountId)));
	}

	@Override
	public Collection<? extends Mandate> getActiveMandatesForAccount(String accountId) {
		return Sets.newHashSet((Iterator<MongoMandate>) collection().find(DBQuery.is("accountId", accountId).is("active", Boolean.TRUE)));
	}

	@Override
	public Mandate createMandateForAccount(String accountId) {
		final MongoMandate mandate = new MongoMandate();
		mandate.setAccountId(accountId);
		mandate.setCreditorId(creditorId);
		
		mandate.setId(collection().insert(mandate).getSavedId());
		return mandate;
	}

	@Override
	public void updateMandate(Mandate mandate) {
		collection().updateById(mandate.getId(), (MongoMandate) mandate);
	}

	@Override
	public Mandate createMandateFromJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, MongoMandate.class);
	}

	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties == null) {
			creditorId = null;
		} else {
			String creditorId = (String) properties.get("creditor_id");
			if (creditorId == null || creditorId.length() == 0) {
				throw new ConfigurationException("creditor_id", "Field must not be empty");
			}
			this.creditorId = creditorId;
		}
	}

}
