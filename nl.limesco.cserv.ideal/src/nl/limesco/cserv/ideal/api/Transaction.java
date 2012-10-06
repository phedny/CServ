package nl.limesco.cserv.ideal.api;

import java.net.URL;

public interface Transaction {

	String getTransactionId();
	
	Issuer getIssuer();
	
	Currency getCurrency();
	
	int getAmount();
	
	URL getReturnUrl();
	
	URL getRedirectUrl();
	
}
