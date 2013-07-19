package nl.limesco.cserv.invoice.api;

import java.util.Calendar;

public interface InvoiceConstructor {
	public Invoice constructInvoiceForAccount(Calendar day, String accountId, boolean dry_run) throws IdAllocationException, PhoneNumberMissingException;
}
