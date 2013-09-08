package nl.limesco.cserv.cdr.mongo;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountMergeHelper;
import nl.limesco.cserv.cdr.api.CdrService;

public class CdrAccountMergeHelper implements AccountMergeHelper {

	private volatile CdrService cdrService;
	
	@Override
	public void verifyAccountMerge(Account from, Account to) throws IllegalArgumentException {
		// Allow any merge
	}
	
	@Override
	public void mergeAccount(Account from, Account to) throws IllegalArgumentException {
		cdrService.moveCdrs(from, to);
	}

}
