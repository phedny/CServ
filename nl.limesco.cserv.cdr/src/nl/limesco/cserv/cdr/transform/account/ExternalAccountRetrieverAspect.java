package nl.limesco.cserv.cdr.transform.account;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrRetriever;

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
		public Cdr apply(final Cdr input) {
			return new Cdr() {

				@Override
				public String getSource() {
					return input.getSource();
				}

				@Override
				public String getCallId() {
					return input.getCallId();
				}

				@Override
				public Optional<String> getAccount() {
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

				@Override
				public Calendar getTime() {
					return input.getTime();
				}

				@Override
				public String getFrom() {
					return input.getFrom();
				}

				@Override
				public String getTo() {
					return input.getTo();
				}

				@Override
				public boolean isConnected() {
					return input.isConnected();
				}

				@Override
				public Optional<Type> getType() {
					return input.getType();
				}

				@Override
				public long getSeconds() {
					return input.getSeconds();
				}

				@Override
				public Map<String, String> getAdditionalInfo() {
					return input.getAdditionalInfo();
				}

				@Override
				public Optional<String> getInvoice() {
					return input.getInvoice();
				}

				@Override
				public Optional<String> getInvoiceBuilder() {
					return input.getInvoiceBuilder();
				}

				@Override
				public Optional<Cdr.Pricing> getPricing() {
					return input.getPricing();
				}
				
			};
		}
	}

}
