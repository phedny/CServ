package nl.limesco.cserv.ideal.targetpay;

import java.net.URL;

import nl.limesco.cserv.ideal.api.Currency;
import nl.limesco.cserv.ideal.api.Issuer;
import nl.limesco.cserv.ideal.api.Transaction;

public class TransactionImpl implements Transaction {

	private final String transactionId;
		
	private final Issuer issuer;
	
	private final Currency currency;
	
	private final int amount;
	
	private final URL returnUrl;
	
	private final URL redirectUrl;
	
	public TransactionImpl(String transactionId, Issuer issuer, Currency currency, int amount, URL returnUrl, URL redirectUrl) {
		this.transactionId = transactionId;
		this.issuer = issuer;
		this.currency = currency;
		this.amount = amount;
		this.returnUrl = returnUrl;
		this.redirectUrl = redirectUrl;
	}

	@Override
	public String getTransactionId() {
		return transactionId;
	}

	@Override
	public Issuer getIssuer() {
		return issuer;
	}

	@Override
	public Currency getCurrency() {
		return currency;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public URL getReturnUrl() {
		return returnUrl;
	}
	
	@Override
	public URL getRedirectUrl() {
		return redirectUrl;
	}

}
