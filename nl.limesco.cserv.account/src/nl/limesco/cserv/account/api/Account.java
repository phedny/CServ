package nl.limesco.cserv.account.api;

import java.util.List;

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
	
	public List<String> getSIMs();
	public void setSIMs(List<String> sims);
	
}
