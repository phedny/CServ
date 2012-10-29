package nl.limesco.cserv.invoice.api;

import java.util.Calendar;
import java.util.List;

public interface Invoice {

	String getId();
	
	String getAccountId();
	
	Calendar getCreationDate();
	
	InvoiceCurrency getCurrency();
	
	List<? extends ItemLine> getItemLines();
	
	List<? extends TaxLine> getTaxLines();
	
	long getTotalWithoutTaxes();
	
	long getTotalWithTaxes();
	
	boolean isSound();

}
