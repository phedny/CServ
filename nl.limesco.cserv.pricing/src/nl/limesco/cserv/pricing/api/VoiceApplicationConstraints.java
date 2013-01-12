package nl.limesco.cserv.pricing.api;

import java.util.Calendar;
import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.VoiceCdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Optional;

public interface VoiceApplicationConstraints extends ApplicationConstraints {

	Optional<Collection<CallConnectivityType>> getCallConnectivityTypes();
	
	Collection<VoiceCdr.Type> getCdrTypes();
	
	Collection<String> getDestinations();
	
	boolean isApplicable(Calendar date, String source, CallConnectivityType callConnectivityType, VoiceCdr.Type cdrType, String destination);
	
	boolean isApplicable(Cdr cdr, CallConnectivityType callConnectivityType);
	
}
