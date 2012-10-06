package nl.limesco.cserv.ideal.api;

import java.net.URL;

/**
 * An implementation of this service is capable of initiating transactions using
 * the iDeal payment method and tracking the status of those transactions.
 */
public interface IdealService {

	/**
	 * Request an acquiring bank to initiate a new transaction.
	 * @param issuer The issuer to use for this transaction
	 * @param currency The currency to use for this transaction
	 * @param amount The amount to be paid, expressed in the base unit of the selected currency
	 * @param description The description to include with this transaction
	 * @param returnUrl The URL to return the customer to after finishing the transaction
	 * @return a representation of the created transaction
	 * @throws IdealException when creating the transaction fails
	 */
	Transaction createTransaction(Issuer issuer, Currency currency, int amount, String description, URL returnUrl) throws IdealException;

}
