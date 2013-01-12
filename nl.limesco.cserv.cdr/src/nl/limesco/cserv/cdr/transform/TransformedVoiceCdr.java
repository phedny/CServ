package nl.limesco.cserv.cdr.transform;

import nl.limesco.cserv.cdr.api.VoiceCdr;

import com.google.common.base.Optional;

public class TransformedVoiceCdr extends AbstractTransformedCdr implements VoiceCdr {
	
	private final VoiceCdr input;

	public TransformedVoiceCdr(VoiceCdr input) {
		super(input);
		this.input = input;
	}

	@Override
	public boolean isConnected() {
		return input.isConnected();
	}

	@Override
	public Optional<VoiceCdr.Type> getType() {
		return input.getType();
	}

	@Override
	public long getSeconds() {
		return input.getSeconds();
	}

	@Override
	public String getDestination() {
		return input.getDestination();
	}

}