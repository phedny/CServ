package nl.limesco.cserv.pricing.api;

import java.util.Calendar;
import java.util.Collection;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import com.google.common.base.Optional;

public interface ApplicationConstraints {

	Calendar getValidFrom();
	
	Optional<Calendar> getValidUntil();
	
	Optional<Collection<String>> getSources();
	
	Optional<Collection<CallConnectivityType>> getCallConnectivityTypes();
	
	Collection<Cdr.Type> getCdrTypes();
	
	boolean isApplicable(Calendar date, String source, CallConnectivityType callConnectivityType, Cdr.Type cdrType);
	
	boolean isApplicable(Cdr cdr, CallConnectivityType callConnectivityType);
	
}
