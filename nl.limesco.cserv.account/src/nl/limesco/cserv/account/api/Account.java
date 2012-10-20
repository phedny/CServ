package nl.limesco.cserv.account.api;

import java.util.Map;

public interface Account {

	public String getId();

	public String getEmail();

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
