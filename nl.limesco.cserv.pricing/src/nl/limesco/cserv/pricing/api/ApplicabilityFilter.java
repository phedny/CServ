package nl.limesco.cserv.pricing.api;

import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Optional;

public interface ApplicabilityFilter {

	Optional<Collection<String>> getSources();
	
	Optional<Collection<CallConnectivityType>> getCallConnectivityTypes();
	
	Optional<Collection<Cdr.Type>> getCdrTypes();
	
}