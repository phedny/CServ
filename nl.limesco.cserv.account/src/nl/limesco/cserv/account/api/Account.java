package nl.limesco.cserv.account.api;

import java.util.Map;

/**
 * An Account represents a specific customer: a person or a company. A person
 * or company has no more than one Account. The Account saves all relevant
 * addressing information for the person or company, as well as a one-to-one
 * mapping to third-party accounts (such as SpeakUp).
 * 
 * An Account may have at most one User that can actually log in to use the
 * API. The relevant Account is stored for every User inside its properties
 * (see the AuthorizationService).
 */
public interface Account {

	public String getId();

	public String getEmail();

	public AccountState getState();
	public void setState(AccountState state);
	
	public void setEmail(String email);

	public String getCompanyName();

	public void setCompanyName(String companyName);

	public Name getFullName();

	public void setFullName(Name fullName);

	public Address getAddress();

	public void setAddress(Address address);
	
	public Map<String, String> getExternalAccounts();
	
	public void setExternalAccounts(Map<String, String> externalAccounts);
	
}
