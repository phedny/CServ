package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.SmsCdr;
import nl.limesco.cserv.pricing.api.SmsApplicationConstraints;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class SmsApplicationConstraintsImpl extends ApplicationConstraintsImpl implements SmsApplicationConstraints {

	private Set<SmsCdr.Type> cdrTypes;
	
	@Override
	@JsonIgnore
	public Collection<SmsCdr.Type> getCdrTypes() {
		return Collections.unmodifiableCollection(cdrTypes);
	}
	
	@JsonProperty("cdrType")
	public Set<SmsCdr.Type> getCdrTypesAsSet() {
		return cdrTypes;
	}
	
	public void setCdrTypesAsSet(Set<SmsCdr.Type> cdrTypes) {
		this.cdrTypes = cdrTypes;
	}

	@Override
	public boolean isApplicable(Calendar date, String source, SmsCdr.Type cdrType) {
		if (date.before(validFrom) || (validUntil != null && validUntil.before(date))) {
			return false;
		}
		
		if (sources != null && !sources.contains(source)) {
			return false;
		}
		
		if (cdrTypes != null && !cdrTypes.contains(cdrType)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isApplicable(Cdr cdr) {
		if (!((SmsCdr) cdr).getType().isPresent()) {
			return false;
		}
		
		return isApplicable(cdr.getTime(), cdr.getSource(), ((SmsCdr) cdr).getType().get());
	}

}
