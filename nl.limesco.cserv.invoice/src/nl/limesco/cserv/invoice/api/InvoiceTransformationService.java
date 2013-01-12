package nl.limesco.cserv.invoice.api;

import nl.limesco.cserv.account.api.Account;

public interface InvoiceTransformationService {
	
	String transformToJson(Invoice invoice);
	
	String transformToTex(Invoice invoice, Account account);
	
	byte[] transformToPdf(Invoice invoice, Account account);

}
