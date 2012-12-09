package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import nl.limesco.cserv.pricing.api.ApplicationConstraints;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Optional;

public class ApplicationConstraintsImpl implements ApplicationConstraints {

	protected Calendar validFrom;
	
	protected Calendar validUntil;
	
	protected Set<String> sources;

	@Override
	@JsonIgnore
	public Calendar getValidFrom() {
		return (Calendar) validFrom.clone();
	}

//	@JsonProperty("validFrom")
	@JsonIgnore // Not what we want, but not needed at the moment and things break in unexplainable ways :(
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

//	@JsonProperty("validUntil")
	@JsonIgnore // Not what we want, but not needed at the moment and things break in unexplainable ways :(
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

}