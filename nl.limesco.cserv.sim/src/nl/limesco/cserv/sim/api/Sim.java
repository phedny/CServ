package nl.limesco.cserv.sim.api;

import java.util.Calendar;

import com.google.common.base.Optional;

public interface Sim {
	public String getIccid();
	public String getPuk();
	
	public Optional<Calendar> getContractStartDate();
	public void setContractStartDate(Calendar calendar);
	
	public Optional<CallConnectivityType> getCallConnectivityType();
	public void setCallConnectivityType(CallConnectivityType callConnectivityType);
	
	public String getPhoneNumber();
	public void setPhoneNumber(String n);
	
	public SimState getState();
	public void setState(SimState state);
	
	public Optional<String> getOwnerAccountId();
	public void setOwnerAccountId(String a);
	
	public Optional<SipSettings> getSipSettings();
	public void setSipSettings(SipSettings settings);
	public void unsetSipSettings();
	
	public SimApnType getApnType();
	public void setApnType(SimApnType t);
	
	public PortingState getPortingState();
	public void setPortingState(PortingState t);
	
	public boolean isExemptFromCostContribution();
	public void setExemptFromCostContribution(boolean exemptFromCostContribution);
	
	public Optional<String> getActivationInvoiceId();
	public void setActivationInvoiceId(String string);

	public Optional<MonthedInvoice> getLastMonthlyFeesInvoice();
	public void setLastMonthlyFeesInvoice(MonthedInvoice invoice);
}