package nl.limesco.cserv.cdr.job;

import java.util.Calendar;

public interface JobStateService {

	JobState getJobStateForSource(String source);
	
	Iterable<Calendar> getDaysToUpdateForSource(String source);
	
	void updateJobState(String source, Calendar day, Calendar retrieved);
	
}
