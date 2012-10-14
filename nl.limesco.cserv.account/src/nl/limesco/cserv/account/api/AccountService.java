package nl.limesco.cserv.account.api;

import java.io.IOException;

import com.google.common.base.Optional;

public interface AccountService {

	Optional<? extends Account> getAccountById(String id);
	
	Account createAccount();
	
	void updateAccount(Account account);

	Account createAccountFromJson(String json) throws IOException;
	
}
