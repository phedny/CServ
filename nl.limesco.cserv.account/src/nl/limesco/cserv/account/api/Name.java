package nl.limesco.cserv.account.api;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Name {
	private String firstName;
	/// Last name, including surname prefix (tussenvoegsel)
	private String lastName;
	
	@JsonIgnore
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String fn) {
		firstName = fn;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String ln) {
		lastName = ln;
	}
}
