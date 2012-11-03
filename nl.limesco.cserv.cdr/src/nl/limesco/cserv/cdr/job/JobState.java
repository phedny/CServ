package nl.limesco.cserv.cdr.job;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import net.vz.mongodb.jackson.Id;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class JobState {

	private String source;
	
	private Calendar earliestDay;
	
	private Map<String, Calendar> lastRetrieved;

	@Id
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@JsonIgnore
	public Calendar getEarliestDay() {
		return earliestDay;
	}

	public void setEarliestDay(Calendar earliestDay) {
		this.earliestDay = earliestDay;
	}
	
	@JsonProperty("earliestDay")
	public Date getEarliestDayAsDate() {
		return earliestDay.getTime();
	}
	
	public void setEarliestDayAsDate(Date earliestDay) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(earliestDay);
		this.earliestDay = calendar;
	}

	public Map<String, Calendar> getLastRetrieved() {
		return lastRetrieved;
	}

	public void setLastRetrieved(Map<String, Calendar> lastRetrieved) {
		this.lastRetrieved = lastRetrieved;
	}
	
}
