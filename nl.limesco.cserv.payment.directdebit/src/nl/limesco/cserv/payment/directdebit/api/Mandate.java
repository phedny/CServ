package nl.limesco.cserv.payment.directdebit.api;

import java.util.Calendar;

public interface Mandate {

	String getId();
	
	String getCreditorId();
	
	String getAccountId();
	
	boolean isActive();
	
	String getName();
	
	String getAddress();
	
	String getPostalCode();
	
	String getLocality();
	
	String getCountry();
	
	String getIban();
	
	String getBic();
	
	String getSignatureLocality();
	
	Calendar getSignatureDate();
	
}
