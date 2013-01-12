package nl.limesco.cserv.pricing.test;

import java.util.Calendar;
import java.util.Map;

import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.Pricing;

import com.google.common.base.Optional;

final class StaticVoiceCdr implements VoiceCdr {
	
	private final Calendar time;
	
	private final String source;
	
	private final boolean connected;
	
	private final VoiceCdr.Type cdrType;
	
	private final long seconds;
	
	private final String destination;

	StaticVoiceCdr(Calendar time, String source, boolean connected, boolean hasCdrType, long seconds) {
		this(time, source, connected, hasCdrType ? VoiceCdr.Type.EXT_EXT : null, seconds);
	}

	StaticVoiceCdr(Calendar time, String source, boolean connected, VoiceCdr.Type cdrType, long seconds) {
		this(time, source, connected, cdrType, seconds, "Middle Earth");
	}
	
	StaticVoiceCdr(Calendar time, String source, boolean connected, VoiceCdr.Type cdrType, long seconds, String destination) {
		this.time = time;
		this.source = source;
		this.connected = connected;
		this.cdrType = cdrType;
		this.seconds = seconds;
		this.destination = destination;
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
	public boolean isConnected() {
		return connected;
	}

	@Override
	public Optional<VoiceCdr.Type> getType() {
		return Optional.fromNullable(cdrType);
	}

	@Override
	public String getDestination() {
		return destination;
	}

	@Override
	public long getSeconds() {
		return seconds;
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