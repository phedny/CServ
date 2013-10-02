package nl.limesco.cserv.cdr.transform.type.speakup;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrRetriever;
import nl.limesco.cserv.cdr.api.DataCdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.cdr.transform.AbstractTransformedCdr;
import nl.limesco.cserv.cdr.transform.TransformedDataCdr;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

public class SpeakUpTypeAspect implements CdrRetriever {

	private volatile CdrRetriever retriever;
	
	@Override
	public Iterable<Cdr> retrieveCdrsForDay(Calendar day) throws IOException {
		return Iterables.transform(retriever.retrieveCdrsForDay(day), new TransformCdrFunction());
	}

	private final class TransformCdrFunction implements Function<Cdr, Cdr> {
		
		public Cdr apply(final Cdr input) {
			if (input instanceof VoiceCdr) {
				if (input.getCallId().endsWith("@sms")) {
					return new TransformedVoiceToSmsCdr((VoiceCdr) input);
				} else if (input.getCallId().endsWith("@data")) {
					return new TransformedVoiceToDataCdr((VoiceCdr) input);
				}
				return new TransformedSpeakUpVoiceCdr((VoiceCdr) input);
			} else if (input instanceof SmsCdr) {
				return new TransformedSpeakUpSmsCdr((SmsCdr) input);
			} else if (input instanceof DataCdr) {
				return new TransformedDataCdr((DataCdr) input);
			} else {
				throw new IllegalArgumentException("Cannot transform CDR");
			}
		}
	}
	
	private final class TransformedVoiceToSmsCdr extends AbstractTransformedCdr implements SmsCdr {
		private final VoiceCdr input;

		private TransformedVoiceToSmsCdr(VoiceCdr input) {
			super(input);
			this.input = input;
		}

		@Override
		public Optional<SmsCdr.Type> getType() {
			final Map<String, String> info = input.getAdditionalInfo();
			if ("out".equals(info.get("10"))) {
				// By now, the CID shows this is an SMS message and the Direction is "out"
				// Previously, Destination would also be set to "Netherlands - Mobile - Mobile" in this case
				// Since October 19 2012, Destination seems to be empty. We only allow these two values:
				if(!input.getDestination().equals("Netherlands - Mobile - Mobile")
				&& !input.getDestination().equals("")) {
					throw new IllegalArgumentException("SMS Direction is OUT, but Destination is not recognised");
				}
				return Optional.of(SmsCdr.Type.MOBILE_EXT);
			} else if ("in".equals(info.get("10"))) {
				// Same as above: direction is "in", Destination would be set to "Netherlands - Mobile - SpeakUp"
				// but is empty since October 19 2012. We also allow only these two values here.
				if(!input.getDestination().equals("Netherlands - Mobile - SpeakUp")
				&& !input.getDestination().equals("")) {
					throw new IllegalArgumentException("SMS Direction is IN, but Destination is not recognised");
				}
				return Optional.of(SmsCdr.Type.EXT_MOBILE);
			} else {
				throw new IllegalArgumentException("SMS Direction is not recognised");
			}
		}

	}

	private final class TransformedVoiceToDataCdr extends AbstractTransformedCdr implements DataCdr {
		private final VoiceCdr input;

		private TransformedVoiceToDataCdr(VoiceCdr input) {
			super(input);
			this.input = input;
		}

		@Override
		public long getKilobytes() {
			return input.getSeconds();
		}

	}
	
}
