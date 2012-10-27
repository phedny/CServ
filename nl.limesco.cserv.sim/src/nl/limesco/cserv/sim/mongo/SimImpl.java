package nl.limesco.cserv.sim.mongo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;

import nl.limesco.cserv.sim.api.MonthedInvoice;
import nl.limesco.cserv.sim.api.Sim;
import nl.limesco.cserv.sim.api.SimApnType;
import nl.limesco.cserv.sim.api.SimState;
import nl.limesco.cserv.sim.api.SipSettings;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Optional;

public class SimImpl implements Sim {
	private String iccid;
	private String puk;
	private String phoneNumber;
	private SimState state;
	private String owner;
	private SipSettings sipSettings;
	private SimApnType apnType;
	private Calendar contractStartDate;
	private String activationInvoiceId;
	private MonthedInvoice lastMonthlyFeesInvoice;

	@JsonIgnore
	public Optional<Calendar> getContractStartDate() {
		return Optional.fromNullable(contractStartDate);
	}
	public void setContractStartDate(Calendar contractStartDate) {
		checkNotNull(contractStartDate);
		this.contractStartDate = contractStartDate;
	}
	public Calendar getNullableContractStartDate() {
		return contractStartDate;
	}
	public void setNullableContractStartDate(Calendar contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	@JsonProperty("_id")
	public String getIccid() {
		return iccid;
	}

	public void setIccid(String s) {
		this.iccid = s;
	}
	
	public String getPuk() {
		return puk;
	}
	public void setPuk(String puk) {
		this.puk = puk;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String n) {
		this.phoneNumber = n;
	}
	
	// TODO: must be possible for a SIM to have multiple corresponding accounts
	public String getOwnerAccountId() {
		return owner;
	}
	public void setOwnerAccountId(String a) {
		this.owner = a;
	}
	
	@JsonIgnore
	public Optional<SipSettings> getSipSettings() {
		return Optional.fromNullable(sipSettings);
	}
	public void setSipSettings(SipSettings s) {
		checkNotNull(s);
		this.sipSettings = s;
	}
	public void unsetSipSettings() {
		this.sipSettings = null;
	}
	
	@JsonProperty("sipSettings")
	public SipSettings getNullableSipSettings() {
		return sipSettings;
	}
	public void setNullableSipSettings(SipSettings s) {
		this.sipSettings = s;
	}
	
	public SimApnType getApnType() {
		return apnType;
	}
	
	public void setApnType(SimApnType t) {
		this.apnType = t;
	}

	public SimState getSimState() {
		return state;
	}

	public void setSimState(SimState state) {
		this.state = state;
	}
	
	@JsonIgnore
	public Optional<String> getActivationInvoiceId() {
		return Optional.fromNullable(activationInvoiceId);
	}
	public String getNullableActivationInvoiceId() {
		return activationInvoiceId;
	}
	public void setActivationInvoiceId(String id) {
		checkNotNull(id);
		this.activationInvoiceId = id;
	}
	public void setNullableActivationInvoiceId(String id) {
		this.activationInvoiceId = id;
	}
	
	@JsonIgnore
	public Optional<MonthedInvoice> getLastMonthlyFeesInvoice() {
		return Optional.fromNullable(lastMonthlyFeesInvoice);
	}
	public MonthedInvoice getNullableLastMonthlyFeesInvoice() {
		return lastMonthlyFeesInvoice;
	}
	public void setLastMonthlyFeesInvoice(MonthedInvoice invoice) {
		checkNotNull(invoice);
		this.lastMonthlyFeesInvoice = invoice;
	}
	public void setNullableLastMonthlyFeesInvoice(MonthedInvoice invoice) {
		this.lastMonthlyFeesInvoice = invoice;
	}
}
