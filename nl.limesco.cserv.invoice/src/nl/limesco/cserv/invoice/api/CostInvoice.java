package nl.limesco.cserv.invoice.api;

import java.util.Calendar;
import java.util.List;

public interface CostInvoice {

	String getId();
	
	String getAccountId();
	
	String getCustomerInvoiceId();
	
	Calendar getCreationDate();
	
	InvoiceCurrency getCurrency();
	
	List<? extends ItemLine> getItemLines();

	long getTotal();
	
	boolean isSound();

}
