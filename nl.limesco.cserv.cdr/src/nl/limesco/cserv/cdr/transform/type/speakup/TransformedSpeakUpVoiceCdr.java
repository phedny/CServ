package nl.limesco.cserv.cdr.transform.type.speakup;

import java.util.Map;

import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.cdr.transform.TransformedVoiceCdr;

import com.google.common.base.Optional;

final class TransformedSpeakUpVoiceCdr extends TransformedVoiceCdr {
	
	private final VoiceCdr input;

	TransformedSpeakUpVoiceCdr(VoiceCdr input) {
		super(input);
		this.input = input;
	}

	@Override
	public Optional<VoiceCdr.Type> getType() {
		final Optional<VoiceCdr.Type> inputType = input.getType();
		if (inputType.isPresent()) {
			return inputType;
		}
		
		final Map<String, String> info = input.getAdditionalInfo();
		if ("out".equals(info.get("10"))) {
			if ("Netherlands - Fixed - PBX (Mobile-On-PBX)".equals(input.getDestination())) {
				return Optional.of(VoiceCdr.Type.MOBILE_PBX);
			} else if ("Netherlands - Mobile - Handset (Mobile-On-PBX)".equals(input.getDestination())) {
				return Optional.of(VoiceCdr.Type.PBX_MOBILE);
			} else if ("Netherlands - Mobile - Handset".equals(input.getDestination())) {
				return Optional.of(VoiceCdr.Type.PBX_MOBILE);
			} else {
				return Optional.of(VoiceCdr.Type.MOBILE_EXT);
			}
		} else if ("in".equals(info.get("10"))) {
			if ("Netherlands - Mobile - SpeakUp".equals(input.getDestination())) {
				return Optional.of(VoiceCdr.Type.EXT_PBX);
			}
		}
		
		return Optional.absent();
	}

}