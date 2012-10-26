package nl.limesco.cserv.payment.api;

import java.util.Set;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;

public interface Payment {
	String getId();
	String getAccountId();
	InvoiceCurrency getCurrency();
	PaymentType getPaymentType();
	String getDestination();
	String getTransactionId();
	int getAmount();
	
	PaymentStatus getStatus();
	void setStatus(PaymentStatus status);
	
	Set<String> getInvoiceIds();
	void setInvoiceIds(Set<String> invoiceIds);
	void addInvoiceId(String invoiceId);
}
