package nl.limesco.cserv.cdr.retriever.steps;

import static com.google.common.base.Preconditions.checkArgument;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import nl.limesco.cserv.cdr.api.Cdr;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class CsvLineCdr implements Cdr {
	
	private final String source;
	
	private final String callId;
	
	private final String account;
	
	private final Calendar time;
	
	private final String from;
	
	private final String to;
	
	private final boolean connected;
	
	private final int seconds;
	
	private final Map<String, String> unparsedColumns = Maps.newHashMap();

	public CsvLineCdr(String source, String[] fields, String[] values) throws ParseException {
		checkArgument(fields.length == values.length);
		this.source = source;
		
		String callId = null;
		String account = null;
		String time = null;
		SimpleDateFormat timePattern = null;
		String from = null;
		String to = null;
		String seconds = null;
		
		for (int i = 0; i < fields.length; i++) {
			final String field = fields[i];
			final String value = values[i];
			if (!Strings.isNullOrEmpty(field) && !"-".equals(field)) {
				if ("callId".equals(field)) {
					callId = value;
				} else if ("account".equals(field)) {
					account = value;
				} else if ("from".equals(field)) {
					from = value;
				} else if ("to".equals(field)) {
					to = value;
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
		
		this.callId = callId;
		this.account = account;
		this.from = from;
		this.to = to;
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
	public String getAccount() {
		return account;
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
	public boolean isConnected() {
		return connected;
	}

	@Override
	public Type getType() {
		return Type.UNKNOWN;
	}

	@Override
	public long getSeconds() {
		return seconds;
	}
	
	public Map<String, String> getAdditionalInfo() {
		return unparsedColumns;
	}

}
