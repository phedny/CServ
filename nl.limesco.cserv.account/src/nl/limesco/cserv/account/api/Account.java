package nl.limesco.cserv.account.api;

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
	
}
