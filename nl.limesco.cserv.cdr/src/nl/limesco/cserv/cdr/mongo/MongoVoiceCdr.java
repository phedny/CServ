package nl.limesco.cserv.cdr.mongo;

import static com.google.common.base.Preconditions.checkNotNull;


import nl.limesco.cserv.cdr.api.VoiceCdr;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Optional;

public class MongoVoiceCdr extends AbstractMongoCdr implements VoiceCdr {

	private VoiceCdr.Type type;

	private long seconds;

	protected boolean connected;
	
	public MongoVoiceCdr() {
		// Default constructor.
	}

	public MongoVoiceCdr(VoiceCdr cdr) {
		super(cdr);
		connected = cdr.isConnected();
		type = cdr.getType().orNull();
		seconds = cdr.getSeconds();
	}

	@Override
	@JsonIgnore
	public Optional<VoiceCdr.Type> getType() {
		return Optional.fromNullable(type);
	}

	public void setType(VoiceCdr.Type type) {
		checkNotNull(type);
		this.type = type;
	}

	@JsonProperty("type")
	public VoiceCdr.Type getNullableType() {
		return type;
	}

	public void setNullableType(VoiceCdr.Type type) {
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
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

}
