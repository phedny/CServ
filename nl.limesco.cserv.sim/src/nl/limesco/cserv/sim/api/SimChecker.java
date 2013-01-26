package nl.limesco.cserv.sim.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimChecker {
	protected final Sim sim;
	protected Set<ProposedChange> proposedChanges;
	protected boolean ran;
	
	public SimChecker(Sim sim) {
		this.sim = sim;
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
		checkStateProperties();
		this.ran = true;
	}
	
	public static enum ProposedChangeIdentifier {
		SIM_ICCID_PUK_INVALID("ICCID or PUK invalid. Proposed deactivation."),
		SIM_STOCK_WITH_PROPERTIES("Stock SIM should have no set properties."),
		REQUEST_ACTIVATION("Request activation at the SpeakUp portal."),
		PROCESS_ACTIVATION("Process activation response from e-mail.");
		
		// none
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
	protected void checkIccidPuk() {
		if(sim.getState() == SimState.DISABLED) return;
		
		if(sim.getIccid() == null || sim.getIccid().isEmpty()
		|| sim.getPuk() == null   || sim.getPuk().isEmpty()
		|| sim.getIccid().length() != 18 || !sim.getIccid().substring(0, 9).equals("893105029")
		|| sim.getPuk().length() != 8) {
			ProposedChange c = new ProposedChange(ProposedChangeIdentifier.SIM_ICCID_PUK_INVALID);
			c.changes.put("state", SimState.DISABLED.toString());
			return;
		}
	}
	protected void checkStateProperties() {
		switch(sim.getState()) {
		case STOCK: {
			// ICCID and PUK are already set; SIMs in this state should have no other properties
			ProposedChange c = new ProposedChange(ProposedChangeIdentifier.SIM_STOCK_WITH_PROPERTIES);
			if(sim.getOwnerAccountId().isPresent()
			|| sim.getContractStartDate().isPresent()
			|| sim.getCallConnectivityType().isPresent()
			|| sim.getApnType() != null
			|| sim.getPortingState() != null
			|| sim.getActivationInvoiceId().isPresent()
			|| sim.getLastMonthlyFeesInvoice().isPresent()) {
				c.changes.put("state", SimState.DISABLED.toString());
				proposedChanges.add(c);
				break;
			}
			// non-fatal (reversible) changes
			if(sim.getPhoneNumber() != null && !sim.getPhoneNumber().isEmpty()) {
				c.changes.put("phoneNumber", "");
			}
			if(sim.getSipSettings().isPresent()) {
				c.changes.put("sipSettings", "ABSENT");
			}
			if(sim.isExemptFromCostContribution()) {
				c.changes.put("exemptFromCostContribution", "false");
			}
			if(!c.changes.isEmpty()) {
				proposedChanges.add(c);
			}
			break;
		}
		case ALLOCATED: {
			ProposedChange c = new ProposedChange(ProposedChangeIdentifier.REQUEST_ACTIVATION);
			c.changes.put("state", SimState.ACTIVATION_REQUESTED.toString());
			proposedChanges.add(c);
			break;
		}
		case ACTIVATION_REQUESTED: {
			ProposedChange c = new ProposedChange(ProposedChangeIdentifier.PROCESS_ACTIVATION);
			c.changes.put("state", SimState.ACTIVATED.toString());
			proposedChanges.add(c);
			break;
		}
		case ACTIVATED:
		case DISABLED:
			// nothing needs to be done
		}
	}
}
