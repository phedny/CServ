package nl.limesco.cserv.invoice.api;

public interface InvoiceBuilder {

	InvoiceBuilder id(String id);
	
	InvoiceBuilder sequentialId(String sequentialId);
	
	InvoiceBuilder accountId(String accountId);
	
	InvoiceBuilder currency(InvoiceCurrency currency);
	
	InvoiceBuilder itemLine(ItemLine itemLine);

	InvoiceBuilder normalItemLine(String description, long itemCount, long itemPrice, double taxRate);

	InvoiceBuilder durationItemLine(String description, long pricePerCall, long pricePerMinute, long numberOfCalls, long numberOfSeconds, double taxRate);

	Invoice build();

}
