package nl.limesco.cserv.account.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;

import org.amdatu.mongo.MongoDBService;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class AccountServiceImpl implements AccountService {
	
	private static final String COLLECTION = "accounts";
	
	private static final Pattern EXTERNAL_SYSTEM_PATTERN = Pattern.compile("[a-z]+");
	
	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<AccountImpl, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<AccountImpl, String> collection = JacksonDBCollection.wrap(dbCollection, AccountImpl.class, String.class);
		return collection;
	}

	@Override
	public Optional<? extends Account> getAccountById(String id) {
		checkNotNull(id);
		if (!ObjectId.isValid(id)) {
			return Optional.absent();
		}
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", new ObjectId(id))));
	}

	@Override
	public Optional<? extends Account> getAccountByExternalAccount(String system, String externalAccount) {
		checkArgument(EXTERNAL_SYSTEM_PATTERN.matcher(system).matches());
		checkNotNull(externalAccount);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("externalAccounts." + system, externalAccount)));
	}
	
	@Override
	public Collection<? extends Account> getAccountByEmail(String email) {
		checkNotNull(email);
		final DBCursor<AccountImpl> cursor = collection().find(new BasicDBObject().append("email", email));
		return Sets.newHashSet((Iterator<AccountImpl>) cursor);
	}
	
	@Override
	public Collection<? extends Account> getAllAccounts() {
		final DBCursor<AccountImpl> cursor = collection().find();
		return Sets.newHashSet((Iterator<AccountImpl>) cursor);
	}

	@Override
	public Account createAccount() {
		final AccountImpl account = new AccountImpl();
		account.setId(collection().insert(account).getSavedId());
		return account;
	}

	@Override
	public void updateAccount(Account account) {
		checkArgument(account instanceof AccountImpl);
		if (account.getExternalAccounts() != null) {
			for (String system : account.getExternalAccounts().keySet()) {
				checkArgument(EXTERNAL_SYSTEM_PATTERN.matcher(system).matches());
			}
		}
		final AccountImpl impl = (AccountImpl) account;
		if(impl.getId() == null) {
			impl.setId(collection().insert(impl).getSavedId());
		} else {
			collection().updateById(impl.getId(), impl);
		}
	}

	@Override
	public Account createAccountFromJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, AccountImpl.class);
	}

}
