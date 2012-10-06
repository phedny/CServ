package nl.limesco.cserv.account.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;

import org.amdatu.mongo.MongoDBService;
import org.bson.types.ObjectId;

import com.google.common.base.Optional;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class AccountServiceImpl implements AccountService {
	
	private static final String COLLECTION = "accounts";
	
	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<AccountImpl, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<AccountImpl, String> collection = JacksonDBCollection.wrap(dbCollection, AccountImpl.class, String.class);
		return collection;
	}

	@Override
	public Optional<? extends Account> getAccountById(String id) {
		checkNotNull(id);
		return Optional.fromNullable(collection().findOne(new BasicDBObject().append("_id", new ObjectId(id))));
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
		collection().updateById(account.getId(), (AccountImpl) account);
	}

}
