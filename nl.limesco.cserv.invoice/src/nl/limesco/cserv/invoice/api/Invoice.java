package nl.limesco.cserv.invoice.api;

import java.util.Calendar;
import java.util.List;

import com.google.common.base.Optional;

public interface Invoice {

	String getId();
	
	String getAccountId();
	
	Optional<Calendar> getCreationDate();
	
	InvoiceCurrency getCurrency();
	
	List<? extends ItemLine> getItemLines();
	
	List<? extends TaxLine> getTaxLines();
	
	long getTotalWithoutTaxes();
	
	long getTotalWithTaxes();
	
	boolean isSound();

}
