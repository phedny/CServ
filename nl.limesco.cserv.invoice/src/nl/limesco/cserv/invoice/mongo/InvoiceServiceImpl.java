package nl.limesco.cserv.invoice.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceBuilder;
import nl.limesco.cserv.invoice.api.InvoiceService;

import org.amdatu.mongo.MongoDBService;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class InvoiceServiceImpl implements InvoiceService {

	private static final String COLLECTION = "invoices";
	
	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<InvoiceImpl, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<InvoiceImpl, String> collection = JacksonDBCollection.wrap(dbCollection, InvoiceImpl.class, String.class);
		return collection;
	}

	@Override
	public Optional<? extends Invoice> getInvoiceById(String id) {
		checkNotNull(id);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", new ObjectId(id))));
	}

	@Override
	public Optional<? extends Invoice> getInvoiceBySequentialId(String sequentialId) {
		checkNotNull(sequentialId);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("sequentialId", sequentialId)));
	}

	@Override
	public Collection<? extends Invoice> getInvoicesByAccountId(String accountId) {
		checkNotNull(accountId);
		final DBCursor<InvoiceImpl> invoiceCursor = collection().find(new BasicDBObject().append("accountId", accountId));
		return Lists.newArrayList((Iterator<InvoiceImpl>) invoiceCursor);
	}

	@Override
	public Invoice storeInvoice(Invoice invoice) {
		checkNotNull(invoice);
		checkArgument(invoice instanceof InvoiceImpl);
		checkArgument(invoice.isSound());
		final InvoiceImpl invoiceImpl = (InvoiceImpl) invoice;
		if (invoice.getId() == null) {
			final String savedId = collection().insert(invoiceImpl).getSavedId();
			invoiceImpl.setId(savedId);
			return invoice;
		} else {
			collection().updateById(invoice.getId(), invoiceImpl);
		}
		return null;
	}

	@Override
	public InvoiceBuilder buildInvoice() {
		return new InvoiceBuilderImpl();
	}

	@Override
	public Invoice createInvoiceFromJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, InvoiceImpl.class);
	}

}
