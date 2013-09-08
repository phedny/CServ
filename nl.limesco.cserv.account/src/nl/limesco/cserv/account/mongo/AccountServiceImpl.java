package nl.limesco.cserv.account.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountMergeHelper;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.account.api.AccountState;

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

	private List<AccountMergeHelper> mergeHelpers = new ArrayList<AccountMergeHelper>();

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
	
	public void mergeHelperAdded(AccountMergeHelper helper) {
		mergeHelpers.add(helper);
	}
	
	public void mergeHelperRemoved(AccountMergeHelper helper) {
		mergeHelpers.remove(helper);
	}
	
	@Override
	public void mergeExternalAccount(Account actual, Account external) throws IllegalArgumentException {
		// Sanity checks:
		if(external.getState() != AccountState.EXTERNAL_STUB) throw new IllegalArgumentException("External Account must be of EXTERNAL_STUB state");
		if(actual.getState() == AccountState.EXTERNAL_STUB) throw new IllegalArgumentException("Real Account must not be of EXTERNAL_STUB state");
		if(external.getExternalAccounts().size() == 0) throw new IllegalArgumentException("External Account must have external references");
		for(Map.Entry<String, String> entry : external.getExternalAccounts().entrySet()) {
			if(actual.getExternalAccounts().containsKey(entry.getKey())) {
				throw new IllegalArgumentException("Real Account already has one of ExternalAccount's external references: " + entry.getKey());
			}
			actual.getExternalAccounts().put(entry.getKey(), entry.getValue());
		}
		
		for(AccountMergeHelper mergeHelper : mergeHelpers) {
			// Will throw if the merge is not allowed.
			mergeHelper.verifyAccountMerge(external, actual);
		}
		
		for(AccountMergeHelper mergeHelper : mergeHelpers) {
			mergeHelper.mergeAccount(external, actual);
		}

		updateAccount(actual);
		collection().remove(new BasicDBObject().append("_id", new ObjectId(external.getId())));
	}

}
