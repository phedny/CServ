package nl.limesco.cserv.sim.api;

import java.util.Calendar;

import com.google.common.base.Optional;

public interface Sim {
	public String getIccid();
	public String getPuk();
	
	public Optional<Calendar> getContractStartDate();
	public void setContractStartDate(Calendar calendar);
	
	public String getPhoneNumber();
	public void setPhoneNumber(String n);
	
	public SimState getSimState();
	public void setSimState(SimState state);
	
	public String getOwnerAccountId();
	public void setOwnerAccountId(String a);
	
	public Optional<SipSettings> getSipSettings();
	public void setSipSettings(SipSettings settings);
	public void unsetSipSettings();
	
	public SimApnType getApnType();
	public void setApnType(SimApnType t);
	
	public Optional<String> getActivationInvoiceId();
	public void setActivationInvoiceId(String string);

	public Optional<MonthedInvoice> getLastMonthlyFeesInvoice();
	public void setLastMonthlyFeesInvoice(MonthedInvoice invoice);
}