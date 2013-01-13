package nl.limesco.cserv.cdr.transform.account;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrRetriever;
import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.cdr.transform.TransformedDataCdr;
import nl.limesco.cserv.cdr.transform.TransformedSmsCdr;
import nl.limesco.cserv.cdr.transform.TransformedVoiceCdr;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class ExternalAccountRetrieverAspect implements CdrRetriever {

	private volatile CdrRetriever retriever;
	
	private volatile AccountService accountService;
	
	@Override
	public Iterable<Cdr> retrieveCdrsForDay(Calendar day) throws IOException {
		return Iterables.transform(retriever.retrieveCdrsForDay(day), new TransformCdrFunction());
	}
	
	private final class TransformCdrFunction implements Function<Cdr, Cdr> {

		public Optional<String> getAccount(Cdr input) {
			final Optional<String> inputAccount = input.getAccount();
			if (inputAccount.isPresent()) {
				return inputAccount;
			}
			
			final Map<String, String> info = input.getAdditionalInfo();
			if (info == null || !info.containsKey("externalAccount")) {
				return Optional.absent();
			}
			
			final String externalAccount = info.get("externalAccount");
			final Optional<? extends Account> account = accountService.getAccountByExternalAccount(input.getSource(), externalAccount);
			if (account.isPresent()) {
				return Optional.of(account.get().getId());
			} else {
				final Account newAccount = accountService.createAccount();
				final Map<String, String> externalAccounts = Maps.newHashMap();
				externalAccounts.put(input.getSource(), externalAccount);
				newAccount.setExternalAccounts(externalAccounts);
				accountService.updateAccount(newAccount);
				return Optional.of(newAccount.getId());
			}
		}

		public Cdr apply(final Cdr input) {
			final Optional<String> account = getAccount(input);
			if (input instanceof VoiceCdr) {
				return new AccountTransformedVoiceCdr((VoiceCdr) input, account);
			} else if (input instanceof SmsCdr) {
				return new AccountTransformedSmsCdr((SmsCdr) input, account);
			} else if (input instanceof DataCdr) {
				return new AccountTransformedDataCdr((DataCdr) input, account);
			} else {
				throw new IllegalArgumentException("Cannot transform CDR");
			}
		}
	}

	private class AccountTransformedVoiceCdr extends TransformedVoiceCdr {
		
		private final Optional<String> account;
		
		private AccountTransformedVoiceCdr(VoiceCdr input, Optional<String> account) {
			super(input);
			this.account = account;
		}

		@Override
		public Optional<String> getAccount() {
			return account;
		}

	}

	private class AccountTransformedSmsCdr extends TransformedSmsCdr {
		
		private final Optional<String> account;
		
		private AccountTransformedSmsCdr(SmsCdr input, Optional<String> account) {
			super(input);
			this.account = account;
		}

		@Override
		public Optional<String> getAccount() {
			return account;
		}

	}

	private class AccountTransformedDataCdr extends TransformedDataCdr {
		
		private final Optional<String> account;
		
		private AccountTransformedDataCdr(DataCdr input, Optional<String> account) {
			super(input);
			this.account = account;
		}

		@Override
		public Optional<String> getAccount() {
			return account;
		}

	}
	
}
