package nl.limesco.cserv.ideal.api;

/**
 * When starting an iDeal transaction, the customer must select his issuer from
 * a list provided by the merchant. All available issuers are represented by an
 * implementation of this class.
 */
public interface Issuer {

	/**
	 * @return the unique identifier with which this issuer is known
	 */
	String getIdentifier();

	/**
	 * @return a human-readable name by which this issuer is known
	 */
	String getName();

}
