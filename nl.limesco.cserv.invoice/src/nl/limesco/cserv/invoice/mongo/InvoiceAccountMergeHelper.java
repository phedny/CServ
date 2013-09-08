package nl.limesco.cserv.invoice.mongo;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountMergeHelper;
import nl.limesco.cserv.invoice.api.InvoiceService;

public class InvoiceAccountMergeHelper implements AccountMergeHelper {

	private volatile InvoiceService invoiceService;
	
	@Override
	public void verifyAccountMerge(Account from, Account to) throws IllegalArgumentException {
		if(!invoiceService.getInvoicesByAccountId(from.getId()).isEmpty()) {
			throw new IllegalArgumentException("Cannot merge account that still has invoices.");
		}

	}

	@Override
	public void mergeAccount(Account from, Account to) throws IllegalArgumentException {
	}

}
