package nl.limesco.cserv.account.rest;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountState;
import nl.limesco.cserv.account.api.Address;
import nl.limesco.cserv.account.api.Name;

public class AccountWithExtensions implements Account {
	
	private final Account account;
	
	private final Map<String, Object> extensions;
	
	public AccountWithExtensions(Account account, Map<String, Object> extensions) {
		this.account = account;
		this.extensions = extensions;
	}
	
	@Override
	public String getId() {
		return account.getId();
	}

	@Override
	public String getEmail() {
		return account.getEmail();
	}

	@Override
	public AccountState getState() {
		return account.getState();
	}

	@Override
	public void setState(AccountState state) {
		account.setState(state);
	}

	@Override
	public void setEmail(String email) {
		account.setEmail(email);
	}

	@Override
	public String getCompanyName() {
		return account.getCompanyName();
	}

	@Override
	public void setCompanyName(String companyName) {
		account.setCompanyName(companyName);
	}

	@Override
	public Name getFullName() {
		return account.getFullName();
	}

	@Override
	public void setFullName(Name fullName) {
		account.setFullName(fullName);
	}

	@Override
	public Address getAddress() {
		return account.getAddress();
	}

	@Override
	public void setAddress(Address address) {
		account.setAddress(address);
	}

	@Override
	public Map<String, String> getExternalAccounts() {
		return account.getExternalAccounts();
	}

	@Override
	public void setExternalAccounts(Map<String, String> externalAccounts) {
		account.setExternalAccounts(externalAccounts);
	}

	@JsonAnyGetter
	public Map<String, Object> getExtensions() {
		return extensions;
	}

}
