package nl.limesco.cserv.payment.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.payment.api.Payment;
import nl.limesco.cserv.payment.api.PaymentBuilder;
import nl.limesco.cserv.payment.api.PaymentService;

import org.amdatu.mongo.MongoDBService;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class PaymentServiceImpl implements PaymentService {
	private static final String COLLECTION = "payment";
	
	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<PaymentImpl, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<PaymentImpl, String> collection = JacksonDBCollection.wrap(dbCollection, PaymentImpl.class, String.class);
		return collection;
	}

	@Override
	public Optional<? extends Payment> getPaymentById(String id) {
		checkNotNull(id);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", new ObjectId(id))));
	}
	
	@Override
	public Collection<? extends Payment> getPaymentsByAccountId(String accountId) {
		checkNotNull(accountId);
		final DBCursor<PaymentImpl> invoiceCursor = collection().find(new BasicDBObject().append("accountId", accountId));
		return Lists.newArrayList((Iterator<PaymentImpl>) invoiceCursor);
	}
	
	@Override
	public PaymentBuilder buildPayment() {
		return new PaymentBuilderImpl();
	}
	
	@Override
	public PaymentBuilder buildPayment(Payment p) {
		return new PaymentBuilderImpl(p);
	}

	@Override
	public void updatePayment(Payment payment) {
		checkNotNull(payment);
		checkArgument(payment instanceof PaymentImpl);
		
		final PaymentImpl paymentImpl = (PaymentImpl) payment;
		
		if (paymentImpl.getId() != null) {
			collection().updateById(paymentImpl.getId(), paymentImpl);
		} else {
			paymentImpl.setId(collection().insert(paymentImpl).getSavedId());
		}
	}

	@Override
	public Payment createPaymentFromJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, PaymentImpl.class);
	}
	
}
