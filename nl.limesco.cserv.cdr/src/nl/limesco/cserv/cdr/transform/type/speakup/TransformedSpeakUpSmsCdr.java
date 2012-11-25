package nl.limesco.cserv.cdr.transform.type.speakup;

import java.util.Map;

import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.transform.TransformedSmsCdr;

import com.google.common.base.Optional;

final class TransformedSpeakUpSmsCdr extends TransformedSmsCdr {
	
	private final SmsCdr input;

	TransformedSpeakUpSmsCdr(SmsCdr input) {
		super(input);
		this.input = input;
	}

	@Override
	public Optional<SmsCdr.Type> getType() {
		final Optional<SmsCdr.Type> inputType = input.getType();
		if (inputType.isPresent()) {
			return inputType;
		}
		
		final Map<String, String> info = input.getAdditionalInfo();
		if ("out".equals(info.get("10"))) {
			if ("Netherlands - Mobile - Mobile".equals(info.get("9"))) {
				return Optional.of(SmsCdr.Type.MOBILE_EXT);
			}
		}
		
		return Optional.absent();
	}

}