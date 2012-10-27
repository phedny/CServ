package nl.limesco.cserv.invoicing;

import java.util.Calendar;

import com.google.common.base.Objects;

import nl.limesco.cserv.sim.api.SimApnType;

public class SubscriptionKey {

	private final Calendar start;
	
	private final int days;
	
	private final SimApnType apnType;

	public SubscriptionKey(Calendar start, int days, SimApnType apnType) {
		this.start = start;
		this.days = days;
		this.apnType = apnType;
	}

	public Calendar getStart() {
		return start;
	}

	public int getDays() {
		return days;
	}

	public SimApnType getApnType() {
		return apnType;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(start, days, apnType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		SubscriptionKey other = (SubscriptionKey) obj;
		return Objects.equal(start, other.start)
				&& Objects.equal(days, other.days)
				&& Objects.equal(apnType, other.apnType);
	}
	
}
