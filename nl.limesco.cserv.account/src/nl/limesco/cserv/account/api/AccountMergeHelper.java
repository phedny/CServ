package nl.limesco.cserv.account.api;

public interface AccountMergeHelper {
	void verifyAccountMerge(Account from, Account to) throws IllegalArgumentException;
	void mergeAccount(Account from, Account to) throws IllegalArgumentException;
}
