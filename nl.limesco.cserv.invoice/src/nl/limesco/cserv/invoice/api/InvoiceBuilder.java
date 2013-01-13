package nl.limesco.cserv.invoice.api;

import java.util.Calendar;
import java.util.List;

public interface InvoiceBuilder {

	InvoiceBuilder id(String id);
	
	InvoiceBuilder accountId(String accountId);
	
	InvoiceBuilder creationDate(Calendar creationDate);
	
	InvoiceBuilder currency(InvoiceCurrency currency);
	
	InvoiceBuilder itemLine(ItemLine itemLine);

	InvoiceBuilder normalItemLine(String description, long itemCount, long itemPrice, double taxRate);

	InvoiceBuilder normalItemLine(String description, List<String> multilineDescription, long itemCount, long itemPrice, double taxRate);

	InvoiceBuilder durationItemLine(String description, long pricePerCall, long pricePerMinute, long numberOfCalls, long numberOfSeconds, double taxRate);

	InvoiceBuilder durationItemLine(String description, List<String> multilineDescription, long pricePerCall, long pricePerMinute, long numberOfCalls, long numberOfSeconds, double taxRate);

	Invoice build();

}
