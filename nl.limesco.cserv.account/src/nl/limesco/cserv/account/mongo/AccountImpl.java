package nl.limesco.cserv.account.mongo;

import net.vz.mongodb.jackson.ObjectId;
import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.Address;
import nl.limesco.cserv.account.api.Name;

import org.codehaus.jackson.annotate.JsonProperty;

public class AccountImpl implements Account {

	private String id;
	
	private String email;
	
	private String companyName;
	
	private Name fullName;
	
	private Address address;

	@ObjectId
	@JsonProperty("_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Name getFullName() {
		return fullName;
	}

	public void setFullName(Name fullName) {
		this.fullName = fullName;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
}
