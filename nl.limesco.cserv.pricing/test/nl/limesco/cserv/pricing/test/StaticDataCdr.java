package nl.limesco.cserv.pricing.test;

import java.util.Calendar;
import java.util.Map;

import nl.limesco.cserv.cdr.api.DataCdr;

import com.google.common.base.Optional;

final class StaticDataCdr implements DataCdr {
	
	private final Calendar time;
	
	private final String source;
	
	private final long kilobytes;
	
	StaticDataCdr(Calendar time, String source, long kilobytes) {
		this.time = time;
		this.source = source;
		this.kilobytes = kilobytes;
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
	public long getKilobytes() {
		return kilobytes;
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