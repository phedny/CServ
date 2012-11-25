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
				if ("Netherlands - Mobile - Mobile".equals(info.get("9"))) {
					return Optional.of(SmsCdr.Type.MOBILE_EXT);
				}
			}
			
			return Optional.absent();
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
