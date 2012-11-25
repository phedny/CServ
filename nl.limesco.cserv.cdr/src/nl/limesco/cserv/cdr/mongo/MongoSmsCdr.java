package nl.limesco.cserv.cdr.mongo;

import static com.google.common.base.Preconditions.checkNotNull;
import nl.limesco.cserv.cdr.api.SmsCdr;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Optional;

public class MongoSmsCdr extends AbstractMongoCdr implements SmsCdr {

	private SmsCdr.Type type;

	public MongoSmsCdr() {
		// Default constructor.
	}

	public MongoSmsCdr(SmsCdr cdr) {
		super(cdr);
		type = cdr.getType().orNull();
	}

	@Override
	@JsonIgnore
	public Optional<SmsCdr.Type> getType() {
		return Optional.fromNullable(type);
	}

	public void setType(SmsCdr.Type type) {
		checkNotNull(type);
		this.type = type;
	}

	@JsonProperty("type")
	public SmsCdr.Type getNullableType() {
		return type;
	}

	public void setNullableType(SmsCdr.Type type) {
		this.type = type;
	}

}
