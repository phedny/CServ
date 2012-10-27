package nl.limesco.cserv.cdr.mongo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import net.vz.mongodb.jackson.ObjectId;
import nl.limesco.cserv.cdr.api.Cdr;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Optional;

public class MongoCdr implements Cdr {
	
	private String id;
	
	private String source;
	
	private String callId;
	
	private String account;
	
	private Calendar time;
	
	private String from;
	
	private String to;
	
	private boolean connected;
	
	private Type type;
	
	private long seconds;
	
	private Map<String, String> additionalInfo;
	
	private String invoice;
	
	private String invoiceBuilder;
	
	private CdrPricingImpl pricing;
	
	public MongoCdr() {
		// Default constructor.
	}

	public MongoCdr(Cdr cdr) {
		source = cdr.getSource();
		callId = cdr.getCallId();
		account = cdr.getAccount().orNull();
		time = cdr.getTime();
		from = cdr.getFrom();
		to = cdr.getTo();
		connected = cdr.isConnected();
		type = cdr.getType().orNull();
		seconds = cdr.getSeconds();
		additionalInfo = cdr.getAdditionalInfo();
	}

	@ObjectId
	@JsonProperty("_id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	@Override
	@JsonIgnore
	public Optional<String> getAccount() {
		return Optional.fromNullable(account);
	}

	public void setAccount(String account) {
		checkNotNull(account);
		this.account = account;
	}
	
	@JsonProperty("account")
	public String getNullableAccount() {
		return account;
	}
	
	public void setNullableAccount(String account) {
		this.account = account;
	}

	@Override
	@JsonIgnore
	public Calendar getTime() {
		return time;
	}
	
	@JsonIgnore
	public void setTime(Calendar time) {
		this.time = time;
	}

	@JsonProperty("time")
	public Date getTimeAsDate() {
		return time.getTime();
	}
	
	public void setTimeAsDate(Date time) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		this.time = calendar;
	}

	@Override
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@Override
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	@JsonIgnore
	public Optional<Type> getType() {
		return Optional.fromNullable(type);
	}

	public void setType(Type type) {
		checkNotNull(type);
		this.type = type;
	}
	
	@JsonProperty("type")
	public Type getNullableType() {
		return type;
	}
	
	public void setNullableType(Type type) {
		this.type = type;
	}

	@Override
	public long getSeconds() {
		return seconds;
	}

	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}

	@Override
	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	@Override
	@JsonIgnore
	public Optional<String> getInvoice() {
		return Optional.fromNullable(invoice);
	}
	
	@JsonProperty("invoice")
	public String getNullableInvoice() {
		return invoice;
	}
	
	public void setNullableInvoice(String invoice) {
		this.invoice = invoice;
	}

	@Override
	@JsonIgnore
	public Optional<String> getInvoiceBuilder() {
		return Optional.fromNullable(invoiceBuilder);
	}
	
	@JsonProperty("invoiceBuilder")
	public String getNullableInvoiceBuilder() {
		return invoiceBuilder;
	}
	
	public void setNullableInvoiceBuilder(String invoiceBuilder) {
		this.invoiceBuilder = invoiceBuilder;
	}

	@Override
	@JsonIgnore
	public Optional<Pricing> getPricing() {
		return Optional.fromNullable((Pricing) pricing);
	}
	
	@JsonProperty("pricing")
	public CdrPricingImpl getNullablePricing() {
		return pricing;
	}
	
	public void setNullablePricing(CdrPricingImpl pricing) {
		this.pricing = pricing;
	}

}
