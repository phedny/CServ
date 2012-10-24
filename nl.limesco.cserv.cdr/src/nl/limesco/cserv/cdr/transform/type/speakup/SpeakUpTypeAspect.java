package nl.limesco.cserv.cdr.transform.type.speakup;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrRetriever;

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
			return new Cdr() {

				@Override
				public String getSource() {
					return input.getSource();
				}

				@Override
				public String getCallId() {
					return input.getCallId();
				}

				@Override
				public Optional<String> getAccount() {
					return input.getAccount();
				}

				@Override
				public Calendar getTime() {
					return input.getTime();
				}

				@Override
				public String getFrom() {
					return input.getFrom();
				}

				@Override
				public String getTo() {
					return input.getTo();
				}

				@Override
				public boolean isConnected() {
					return input.isConnected();
				}

				@Override
				public Optional<Type> getType() {
					final Optional<Type> inputType = input.getType();
					if (inputType.isPresent()) {
						return inputType;
					}
					
					final Map<String, String> info = input.getAdditionalInfo();
					if ("out".equals(info.get("10"))) {
						if ("Netherlands - Fixed - PBX (Mobile-On-PBX)".equals(info.get("9"))) {
							return Optional.of(Type.MOBILE_PBX);
						} else if ("Netherlands - Mobile - Handset (Mobile-On-PBX)".equals(info.get("9"))) {
							return Optional.of(Type.PBX_MOBILE);
						} else if ("Netherlands - Mobile - Handset".equals(info.get("9"))) {
							return Optional.of(Type.PBX_MOBILE);
						} else {
							return Optional.of(Type.MOBILE_EXT);
						}
					} else if ("in".equals(info.get("10"))) {
						if ("Netherlands - Mobile - SpeakUp".equals(info.get("9"))) {
							return Optional.of(Type.EXT_PBX);
						}
					}
					
					return Optional.absent();
				}

				@Override
				public long getSeconds() {
					return input.getSeconds();
				}

				@Override
				public Map<String, String> getAdditionalInfo() {
					return input.getAdditionalInfo();
				}
				
			};
		}
	}

}
