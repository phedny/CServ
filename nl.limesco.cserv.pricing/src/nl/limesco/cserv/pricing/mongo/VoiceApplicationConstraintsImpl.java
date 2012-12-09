package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.pricing.api.VoiceApplicationConstraints;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Optional;

public class VoiceApplicationConstraintsImpl extends ApplicationConstraintsImpl implements VoiceApplicationConstraints {

	private Set<CallConnectivityType> connectivityTypes;
	
	private Set<VoiceCdr.Type> cdrTypes;
	
	@Override
	@JsonIgnore
	public Optional<Collection<CallConnectivityType>> getCallConnectivityTypes() {
		if (connectivityTypes == null) {
			return Optional.absent();
		} else {
			return Optional.of(Collections.unmodifiableCollection(connectivityTypes));
		}
	}
	
	@JsonProperty("callConnectivityType")
	public Set<CallConnectivityType> getCallConnectivityTypesAsSet() {
		return connectivityTypes;
	}
	
	public void setCallConnectivityTypesAsSet(Set<CallConnectivityType> callConnectivityTypes) {
		this.connectivityTypes = callConnectivityTypes;
	}

	@Override
	@JsonIgnore
	public Collection<VoiceCdr.Type> getCdrTypes() {
		return Collections.unmodifiableCollection(cdrTypes);
	}
	
	@JsonProperty("cdrType")
	public Set<VoiceCdr.Type> getCdrTypesAsSet() {
		return cdrTypes;
	}
	
	public void setCdrTypesAsSet(Set<VoiceCdr.Type> cdrTypes) {
		this.cdrTypes = cdrTypes;
	}

	@Override
	public boolean isApplicable(Calendar date, String source, CallConnectivityType callConnectivityType, VoiceCdr.Type cdrType) {
		if (date.before(validFrom) || (validUntil != null && validUntil.before(date))) {
			return false;
		}
		
		if (sources != null && !sources.contains(source)) {
			return false;
		}
		
		if (connectivityTypes != null && !connectivityTypes.contains(callConnectivityType)) {
			return false;
		}
		
		if (cdrTypes != null && !cdrTypes.contains(cdrType)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isApplicable(Cdr cdr, CallConnectivityType callConnectivityType) {
		if (!((VoiceCdr) cdr).isConnected() || !((VoiceCdr) cdr).getType().isPresent()) {
			return false;
		}
		
		return isApplicable(cdr.getTime(), cdr.getSource(), callConnectivityType, ((VoiceCdr) cdr).getType().get());
	}

}
