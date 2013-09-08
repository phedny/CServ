package nl.limesco.cserv.payment.mongo;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountMergeHelper;
import nl.limesco.cserv.payment.api.PaymentService;

public class PaymentAccountMergeHelper implements AccountMergeHelper {

	private volatile PaymentService paymentService;
	
	@Override
	public void verifyAccountMerge(Account from, Account to) throws IllegalArgumentException {
		if(!paymentService.getPaymentsByAccountId(from.getId()).isEmpty()) {
			throw new IllegalArgumentException("Cannot merge account that still has payments.");
		}

	}

	@Override
	public void mergeAccount(Account from, Account to) throws IllegalArgumentException {
	}

}
