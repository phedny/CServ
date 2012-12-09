package nl.limesco.cserv.pricing.test;

import java.util.Calendar;
import java.util.Map;

import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.pricing.api.Pricing;

import com.google.common.base.Optional;

final class StaticSmsCdr implements SmsCdr {
	
	private final Calendar time;
	
	private final String source;
	
	private final SmsCdr.Type cdrType;
	
	StaticSmsCdr(Calendar time, String source, boolean hasCdrType) {
		this(time, source, hasCdrType ? SmsCdr.Type.MOBILE_EXT : null);
	}

	StaticSmsCdr(Calendar time, String source, SmsCdr.Type cdrType) {
		this.time = time;
		this.source = source;
		this.cdrType = cdrType;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getCallId() {
		return null;
	}

	@Override
	public Optional<String> getAccount() {
		return Optional.absent();
	}

	@Override
	public Calendar getTime() {
		return time;
	}

	@Override
	public String getFrom() {
		return null;
	}

	@Override
	public String getTo() {
		return null;
	}

	@Override
	public Optional<SmsCdr.Type> getType() {
		return Optional.fromNullable(cdrType);
	}

	@Override
	public Map<String, String> getAdditionalInfo() {
		return null;
	}

	@Override
	public Optional<String> getInvoice() {
		return null;
	}

	@Override
	public Optional<String> getInvoiceBuilder() {
		return null;
	}

	@Override
	public Optional<Pricing> getPricing() {
		return null;
	}
}