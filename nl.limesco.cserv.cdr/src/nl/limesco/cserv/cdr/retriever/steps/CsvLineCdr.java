package nl.limesco.cserv.cdr.retriever.steps;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class CsvLineCdr implements VoiceCdr {
	
	private final String source;
	
	private final String callId;
	
	private final Calendar time;
	
	private final String from;
	
	private final String to;
	
	private final boolean connected;
	
	private final int seconds;
	
	private String destination;
	
	private final Map<String, String> unparsedColumns = Maps.newHashMap();

	public CsvLineCdr(String source, String[] fields, String[] values) throws ParseException {
		checkArgument(fields.length == values.length);
		this.source = source;
		
		String callId = null;
		String time = null;
		SimpleDateFormat timePattern = null;
		String from = null;
		String to = null;
		String destination = null;
		String seconds = null;
		
		for (int i = 0; i < fields.length; i++) {
			final String field = fields[i];
			final String value = values[i];
			if (!Strings.isNullOrEmpty(field) && !"-".equals(field)) {
				if ("callId".equals(field)) {
					callId = value;
				} else if ("account".equals(field)) {
					unparsedColumns.put("externalAccount", value);
				} else if ("from".equals(field)) {
					from = value;
				} else if ("to".equals(field)) {
					to = value;
				} else if ("destination".equals(field)) {
					destination = value;
				} else if ("seconds".equals(field)) {
					seconds = value;
				} else if (field.startsWith("time ")) {
					time = value;
					timePattern = new SimpleDateFormat(field.substring(5));
					timePattern.setTimeZone(TimeZone.getTimeZone("UTC"));
				} else {
					throw new IllegalArgumentException("Unrecognized field name");
				}
			} else {
				unparsedColumns.put(String.valueOf(i), value);
			}
		}
		
		checkNotNull(callId, "No call ID in CDR CSV line");
		checkNotNull(from, "No from phone number in CDR CSV line");
		checkNotNull(to, "No from phone number in CDR CSV line");
		checkNotNull(destination, "No destination in CDR CSV line");
		
		this.callId = callId;
		this.from = from;
		this.to = to;
		this.destination = destination;
		if (Strings.isNullOrEmpty(seconds)) {
			this.seconds = 0;
			this.connected = false;
		} else {
			this.seconds = Integer.parseInt(seconds);
			this.connected = true;
		}
		this.time = Calendar.getInstance();
		this.time.setTime(timePattern.parse(time));
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getCallId() {
		return callId;
	}

	@Override
	public Optional<String> getAccount() {
		return Optional.absent();
	}

	@Override
	public Calendar getTime() {
		return (Calendar) time.clone();
	}
	
	@Override
	public String getFrom() {
		return from;
	}

	@Override
	public String getTo() {
		return to;
	}

	@Override
	public String getDestination() {
		return destination;
	}
	
	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public Optional<Type> getType() {
		return Optional.absent();
	}

	@Override
	public long getSeconds() {
		return seconds;
	}
	
	public Map<String, String> getAdditionalInfo() {
		return unparsedColumns;
	}

	@Override
	public Optional<String> getInvoice() {
		return Optional.absent();
	}

	@Override
	public Optional<String> getInvoiceBuilder() {
		return Optional.absent();
	}

	@Override
	public Optional<Cdr.Pricing> getPricing() {
		return Optional.absent();
	}

}
