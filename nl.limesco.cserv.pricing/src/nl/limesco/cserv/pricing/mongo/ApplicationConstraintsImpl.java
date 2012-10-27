package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.Cdr.Type;
import nl.limesco.cserv.pricing.api.ApplicationConstraints;
import nl.limesco.cserv.sim.api.CallConnectivityType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Optional;

public class ApplicationConstraintsImpl implements ApplicationConstraints {

	private Calendar validFrom;
	
	private Calendar validUntil;
	
	private Set<String> sources;
	
	private Set<CallConnectivityType> connectivityTypes;
	
	private Set<Cdr.Type> cdrTypes;
	
	@Override
	@JsonIgnore
	public Calendar getValidFrom() {
		return (Calendar) validFrom.clone();
	}
	
	@JsonProperty("validFrom")
	public Date getValidFromAsDate() {
		return validFrom.getTime();
	}
	
	public void setValidFromAsDate(Date validFrom) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(validFrom);
		this.validFrom = calendar;
	}

	@Override
	@JsonIgnore
	public Optional<Calendar> getValidUntil() {
		return Optional.fromNullable(validUntil);
	}
	
	@JsonProperty("validUntil")
	public Date getNullableValidUntilAsDate() {
		if (validUntil == null) { 
			return null;
		} else {
			return validUntil.getTime();
		}
	}
	
	public void setNullableValidUntilAsDate(Date validUntil) {
		if (validUntil == null) {
			this.validUntil = null;
		} else {
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(validUntil);
			this.validUntil = calendar;
		}
	}

	@Override
	@JsonIgnore
	public Optional<Collection<String>> getSources() {
		if (sources == null) {
			return Optional.absent();
		} else {
			return Optional.of(Collections.unmodifiableCollection(sources));
		}
	}
	
	@JsonProperty("source")
	public Set<String> getSourcesAsSet() {
		return sources;
	}
	
	public void setSourcesAsSet(Set<String> sources) {
		this.sources = sources;
	}

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
	public Collection<Cdr.Type> getCdrTypes() {
		return Collections.unmodifiableCollection(cdrTypes);
	}
	
	@JsonProperty("cdrType")
	public Set<Cdr.Type> getCdrTypesAsSet() {
		return cdrTypes;
	}
	
	public void setCdrTypesAsSet(Set<Cdr.Type> cdrTypes) {
		this.cdrTypes = cdrTypes;
	}

	@Override
	public boolean isApplicable(Calendar date, String source, CallConnectivityType callConnectivityType, Type cdrType) {
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
		if (!cdr.isConnected() || !cdr.getType().isPresent()) {
			return false;
		}
		
		return isApplicable(cdr.getTime(), cdr.getSource(), callConnectivityType, cdr.getType().get());
	}

}
