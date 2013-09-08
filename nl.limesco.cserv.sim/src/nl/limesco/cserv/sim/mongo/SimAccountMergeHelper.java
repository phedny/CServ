package nl.limesco.cserv.sim.mongo;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountMergeHelper;
import nl.limesco.cserv.sim.api.SimService;

public class SimAccountMergeHelper implements AccountMergeHelper {

	private volatile SimService simService;
	
	@Override
	public void verifyAccountMerge(Account from, Account to) throws IllegalArgumentException {
		if(!simService.getSimsByOwnerAccountId(from.getId()).isEmpty()) {
			throw new IllegalArgumentException("Cannot merge account that still has SIMs.");
		}

	}

	@Override
	public void mergeAccount(Account from, Account to) throws IllegalArgumentException {
	}

}
