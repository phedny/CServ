package nl.limesco.cserv.payment.api;

import java.util.Set;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;

public interface PaymentBuilder {
	PaymentBuilder accountId(String accountId);
	PaymentBuilder currency(InvoiceCurrency currency);
	PaymentBuilder amount(int amount);
	PaymentBuilder transactionId(String id);
	PaymentBuilder destination(String destination);
	PaymentBuilder paymentType(PaymentType type);
	PaymentBuilder setInvoiceIds(Set<String> invoiceIds);
	PaymentBuilder addInvoiceId(String invoiceId);

	Payment build();
}