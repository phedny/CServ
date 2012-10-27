package nl.limesco.cserv.cdr.api;

import java.util.Calendar;
import java.util.Map;

import com.google.common.base.Optional;

public interface Cdr {
	
	public enum Type {
		EXT_EXT     ,  PBX_EXT     ,  MOBILE_EXT     ,
		EXT_PBX     ,  PBX_PBX     ,  MOBILE_PBX     ,
		EXT_MOBILE  ,  PBX_MOBILE  ,  MOBILE_MOBILE  ,
	}

	String getSource();
	
	String getCallId();
	
	Optional<String> getAccount();
	
	Calendar getTime();
	
	String getFrom();
	
	String getTo();
	
	boolean isConnected();
	
	Optional<Type> getType();
	
	long getSeconds();
	
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
