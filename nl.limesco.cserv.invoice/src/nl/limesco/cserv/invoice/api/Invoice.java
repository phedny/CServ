package nl.limesco.cserv.invoice.api;

import java.util.List;

public interface Invoice {

	String getId();
	
	String getSequentialId();
	
	String getAccountId();
	
	InvoiceCurrency getCurrency();
	
	List<? extends ItemLine> getItemLines();
	
	List<? extends TaxLine> getTaxLines();
	
	long getTotalWithoutTaxes();
	
	long getTotalWithTaxes();
	
	boolean isSound();

}
