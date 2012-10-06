package nl.limesco.cserv.account.api;

public interface Account {

	public String getId();

	public String getEmail();

	public void setEmail(String email);

	public String getCompanyName();

	public void setCompanyName(String companyName);

	public String getFullName();

	public void setFullName(String fullName);

	public Address getAddress();

	public void setAddress(Address address);
	
}
