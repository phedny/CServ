package nl.limesco.cserv.cdr.api;

import java.util.Calendar;
import java.util.Map;

import com.google.common.base.Optional;

public interface Cdr {
	
	String getSource();
	
	String getCallId();
	
	Optional<String> getAccount();
	
	Calendar getTime();
	
	String getFrom();
	
	String getTo();
	
	Map<String, String> getAdditionalInfo();
	
	Optional<String> getInvoice();
	
	Optional<String> getInvoiceBuilder();
	
	Optional<Pricing> getPricing();
	
	public interface Pricing {

		String getPricingRuleId();
		
		long getComputedPrice();
		
		long getComputedCost();
		
	}

}
