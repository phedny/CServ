package nl.limesco.cserv.pricing.mongo;

import java.util.Calendar;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.pricing.api.DataApplicationConstraints;

public class DataApplicationConstraintsImpl extends ApplicationConstraintsImpl implements DataApplicationConstraints {

	@Override
	public boolean isApplicable(Calendar date, String source) {
		if (date.before(validFrom) || (validUntil != null && validUntil.before(date))) {
			return false;
		}
		
		if (sources != null && !sources.contains(source)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isApplicable(Cdr cdr) {
		return isApplicable(cdr.getTime(), cdr.getSource());
	}

}
