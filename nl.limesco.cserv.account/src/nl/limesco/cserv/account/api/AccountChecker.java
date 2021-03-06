package nl.limesco.cserv.account.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AccountChecker {
	protected final Account account;
	protected Set<ProposedChange> proposedChanges;
	protected boolean ran;
	
	public AccountChecker(Account account) {
		this.account = account;
		this.proposedChanges = new HashSet<ProposedChange>();
		this.ran = false;
	}
	
	public boolean completed() {
		return this.ran;
	}
	
	public boolean isSound() {
		assert(ran);
		return proposedChanges.isEmpty();
	}
	
	public Set<ProposedChange> getProposedChanges() {
		assert(ran);
		return proposedChanges;
	}
	
	public void run() {
		if(this.ran) return;
		checkDummyAccount();
		checkStateProperties();
		checkExternalReferences();
		this.ran = true;
	}
	
	public static enum ProposedChangeIdentifier {
		DUMMY_DEACTIVATE("Dummy account without external references, proposed deactivation"),
		EXTERNAL_DUMMY_MERGE("Dummy account with external references, proposed merge with real account"),
		NO_EXTERNAL_REF("Account has SIMs but no external SpeakUp reference, so can't be matched with CDRs"),
		ASK_CONFIRMATION("Ask the user for confirmation"),
		PROCESS_CONFIRMATION("Process user confirmation"),
		ADMINISTRATIVE_CONFIRMATION("Immediately set account to confirmed state"),
		NO_CONTACT_INFO("It's impossible to get contact information, disable this account"),
		GET_CONTACT_INFO("Get contact information, e.g. e-mail address, for account");
		
		private String explanation;
		private ProposedChangeIdentifier(String explanation) {
			this.explanation = explanation;
		}
		public String getExplanation() {
			return this.explanation;
		}
	}
	
	public class ProposedChange {
		private ProposedChangeIdentifier identifier;
		public Map<String,String> changes;
		private String explanation;
		public ProposedChange(ProposedChangeIdentifier s) {
			this.identifier = s;
			this.explanation = s.getExplanation();
			this.changes = new HashMap<String,String>();
		}
		public ProposedChangeIdentifier getIdentifier() {
			return identifier;
		}
		public String getExplanation() {
			return explanation;
		}
	}
	
	// Checkers //
	protected void checkDummyAccount() {
		if(account.getState() != AccountState.EXTERNAL_STUB &&
		  (account.getEmail() == null || account.getEmail().isEmpty()
		|| account.getFullName() == null || account.getFullName().getFirstName() == null
		|| account.getFullName().getFirstName().isEmpty()
		|| account.getFullName().getLastName() == null
		|| account.getFullName().getLastName().isEmpty())) {
			// It's not a real account, because essential properties are unset
			ProposedChange c = new ProposedChange(ProposedChangeIdentifier.DUMMY_DEACTIVATE);
			c.changes.put("state", AccountState.DEACTIVATED.toString());
			proposedChanges.add(c);
		}
	}
	
	protected void checkStateProperties() {
		switch(account.getState()) {
		case UNPAID:
		case UNCONFIRMED:
			if(account.getEmail() != null && !account.getEmail().isEmpty()) {
				ProposedChange c = new ProposedChange(ProposedChangeIdentifier.ASK_CONFIRMATION);
				c.changes.put("state", AccountState.CONFIRMATION_REQUESTED.toString());
				proposedChanges.add(c);
				c = new ProposedChange(ProposedChangeIdentifier.ADMINISTRATIVE_CONFIRMATION);
				c.changes.put("state", AccountState.CONFIRMED.toString());
				proposedChanges.add(c);
			} else {
				ProposedChange c = new ProposedChange(ProposedChangeIdentifier.GET_CONTACT_INFO);
				proposedChanges.add(c);
				c = new ProposedChange(ProposedChangeIdentifier.NO_CONTACT_INFO);
				c.changes.put("state", AccountState.CONFIRMATION_IMPOSSIBLE.toString());
				proposedChanges.add(c);
			}
			break;
		case CONFIRMATION_REQUESTED: {
			ProposedChange c = new ProposedChange(ProposedChangeIdentifier.PROCESS_CONFIRMATION);
			c.changes.put("state", AccountState.CONFIRMED.toString());
			proposedChanges.add(c);
			break;
		}
		case EXTERNAL_STUB: {
			ProposedChange c = new ProposedChange(ProposedChangeIdentifier.EXTERNAL_DUMMY_MERGE);
			proposedChanges.add(c);
			break;
		}
		case CONFIRMED:
		case CONFIRMATION_IMPOSSIBLE:
		case DEACTIVATED:
			// nothing to be done
			break;
		}
	}
	
	protected void checkExternalReferences() {
		// TODO, issue #74: does this account have an activated SIM?
		if(!account.getExternalAccounts().containsKey("speakup")) {
			ProposedChange c = new ProposedChange(ProposedChangeIdentifier.NO_EXTERNAL_REF);
			proposedChanges.add(c);
		}
	}
}
