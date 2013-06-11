package nl.limesco.cserv.invoice.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import nl.limesco.cserv.invoice.api.IdAllocationException;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceBuilder;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.api.QueuedItemLine;

import org.amdatu.mongo.MongoDBService;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;

public class InvoiceServiceImpl implements InvoiceService {
	
	private static final Pattern PREFIX_PATTERN = Pattern.compile("[A-Za-z0-9]+");

	private static final String COLLECTION = "invoices";
	private static final String ITEMLINE_QUEUE_COLLECTION = "queued_itemlines";
	
	private static final int INVOICE_ID_LENGTH = 9; 
	
	private static final int NUMBER_OF_SAVE_TIMES = 5;
	
	private volatile MongoDBService mongoDBService;
	
	private Lock lock = new ReentrantLock();

	private JacksonDBCollection<InvoiceImpl, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<InvoiceImpl, String> collection = JacksonDBCollection.wrap(dbCollection, InvoiceImpl.class, String.class);
		return collection;
	}

	@Override
	public Optional<? extends Invoice> getInvoiceById(String id) {
		checkNotNull(id);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", id)));
	}

	@Override
	public Collection<? extends Invoice> getInvoicesByAccountId(String accountId) {
		checkNotNull(accountId);
		final DBCursor<InvoiceImpl> invoiceCursor = collection().find(new BasicDBObject().append("accountId", accountId));
		return Lists.newArrayList((Iterator<InvoiceImpl>) invoiceCursor);
	}

	@Override
	public Collection<? extends Invoice> getInvoicesByPeriod(Calendar start, Calendar end) {
		checkNotNull(start);
		checkNotNull(end);
		
		final DBCursor<InvoiceImpl> invoiceCursor = collection().find(DBQuery
				.greaterThanEquals("creationDate", start.getTime())
				.lessThan("creationDate", end.getTime())
				);
		return Lists.newArrayList((Iterator<InvoiceImpl>) invoiceCursor);
	}

	@Override
	public Invoice storeInvoice(Invoice invoice) throws IdAllocationException {
		checkNotNull(invoice);
		checkArgument(invoice instanceof InvoiceImpl);
		checkArgument(invoice.isSound());
		
		final InvoiceImpl invoiceImpl = (InvoiceImpl) invoice;
		
		if (invoiceImpl.getId() != null) {
			collection().updateById(invoiceImpl.getId(), invoiceImpl);
		} else {
			// Try saving the invoice a couple of time
			final String prefix = getInvoicePrefix();
			for (int i = 0; i < NUMBER_OF_SAVE_TIMES; i++) {
				invoiceImpl.setId(allocateInvoiceId(prefix, INVOICE_ID_LENGTH));
				final WriteResult<InvoiceImpl, String> result = collection().insert(invoiceImpl);
				final CommandResult lastError = result.getLastError(WriteConcern.FSYNC_SAFE);
				if (!lastError.containsField("code") || lastError.getInt("code") != 11000 /* dup key */) {
					return invoiceImpl;
				}
			}
			
			// Failed to save after several tries
			throw new IdAllocationException("Failed to allocate ID for invoice");
		}
		return invoiceImpl;
	}
	
	private String getInvoicePrefix() {
		return (Calendar.getInstance().get(Calendar.YEAR) % 100) + "C";
	}

	private String allocateInvoiceId(String prefix, int length) {
		checkArgument(PREFIX_PATTERN.matcher(prefix).matches());
		
		// Determine highest existing invoice sequence with the required prefix
		final DBCursor<InvoiceImpl> cursor = collection().find(DBQuery.regex("_id", Pattern.compile("^" + prefix)), DBQuery.is("_id", 1)).
				sort(DBQuery.is("_id", -1)).limit(1);
		
		final int sequenceId;
		if (cursor.hasNext()) {
			sequenceId = Integer.parseInt(cursor.next().getId().substring(prefix.length()).replaceFirst("^0+", ""));
		} else {
			sequenceId = 0;
		}
		
		return String.format("%s%0" + (length - prefix.length()) + "d", prefix, sequenceId + 1);
	}

	@Override
	public InvoiceBuilder buildInvoice() {
		return new InvoiceBuilderImpl();
	}

	@Override
	public Invoice createInvoiceFromJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, InvoiceImpl.class);
	}
	
	private JacksonDBCollection<QueuedItemLineImpl, String> queuedItemLinesCollection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(ITEMLINE_QUEUE_COLLECTION);
		final JacksonDBCollection<QueuedItemLineImpl, String> collection = JacksonDBCollection.wrap(dbCollection, QueuedItemLineImpl.class, String.class);
		return collection;
	}

	@Override
	public Collection<? extends QueuedItemLine> getQueuedItemLinesByAccountId(String accountId) {
		checkNotNull(accountId);
		final DBCursor<QueuedItemLineImpl> invoiceCursor = queuedItemLinesCollection().find(new BasicDBObject().append("queuedForAccountId", accountId));
		return Lists.newArrayList((Iterator<QueuedItemLineImpl>) invoiceCursor);
	}

	@Override
	public void clearQueuedItemLinesByAccountId(String accountId) {
		queuedItemLinesCollection().remove(new BasicDBObject().append("accountId", accountId));
	}
	
	@Override
	public QueuedItemLine createQueuedItemLineFromJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, QueuedItemLineImpl.class);
	}

	@Override
	public void addQueuedItemLineToAccountId(QueuedItemLine itemLine) {
		QueuedItemLineImpl impl = (QueuedItemLineImpl) itemLine;
		impl.setTotalPrice();
		queuedItemLinesCollection().insert(impl);
	} 
	
	@Override
	public void lock() {
		lock.lock();
	}
	
	@Override
	public void unlock() {
		lock.unlock();
	}

}
