package nl.limesco.cserv.cdr.transform;

import nl.limesco.cserv.cdr.api.SmsCdr;

import com.google.common.base.Optional;

public class TransformedSmsCdr extends AbstractTransformedCdr implements SmsCdr {
	
	private final SmsCdr input;

	public TransformedSmsCdr(SmsCdr input) {
		super(input);
		this.input = input;
	}

	@Override
	public Optional<SmsCdr.Type> getType() {
		return input.getType();
	}

}