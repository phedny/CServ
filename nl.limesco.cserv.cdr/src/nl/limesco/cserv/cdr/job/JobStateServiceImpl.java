package nl.limesco.cserv.cdr.job;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import net.vz.mongodb.jackson.JacksonDBCollection;

import org.amdatu.mongo.MongoDBService;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class JobStateServiceImpl implements JobStateService {
	
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private static final String COLLECTION = "cdrjobstate";

	private volatile MongoDBService mongoDBService;

	private JacksonDBCollection<JobState, String> collection() {
		final DBCollection dbCollection = mongoDBService.getDB().getCollection(COLLECTION);
		final JacksonDBCollection<JobState, String> collection = JacksonDBCollection.wrap(dbCollection, JobState.class, String.class);
		return collection;
	}
	
	@Override
	public JobState getJobStateForSource(String source) {
		checkNotNull(source);
		return collection().findOneById(source);
	}

	@Override
	public Iterable<Calendar> getDaysToUpdateForSource(String source) {
		final Calendar end = Calendar.getInstance();
		end.setTimeZone(TimeZone.getTimeZone("UTC"));
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);
		end.add(Calendar.DATE, 1); // end is day of tomorrow, so today is included
		return getDaysToUpdate(getJobStateForSource(source), end);
	}

	public Iterable<Calendar> getDaysToUpdate(JobState jobState, Calendar end) {
		final List<Calendar> days = Lists.newLinkedList();
		if (jobState == null || jobState.getEarliestDay() == null) {
			return Collections.emptyList();
			
		}
		for (Calendar day = jobState.getEarliestDay(); day.before(end); day.add(Calendar.DATE, 1)) {
			final String formattedDay = DAY_FORMAT.format(day.getTime());
			if (jobState.getLastRetrieved() != null && jobState.getLastRetrieved().containsKey(formattedDay)) {
				final Calendar lastRetrieved = jobState.getLastRetrieved().get(formattedDay);
				final Calendar endOfDay = (Calendar) day.clone(); endOfDay.add(Calendar.DATE, 1);
				
				if (lastRetrieved.before(endOfDay)) {
					days.add((Calendar) day.clone());
				} else {
					final Calendar endOfNextDay = (Calendar) day.clone(); endOfNextDay.add(Calendar.DATE, 2);
					if (lastRetrieved.before(endOfNextDay) && end.after(endOfNextDay)) {
						days.add((Calendar) day.clone());
					}
				}
			} else {
				days.add((Calendar) day.clone());
			}
		}
		return days;
	}

	@Override
	public void updateJobState(String source, Calendar day, Calendar retrieved) {
		checkNotNull(source);
		checkNotNull(day);
		checkNotNull(retrieved);
		
		final BasicDBObject query = new BasicDBObject().append("_id", source);
		final String formattedDay = DAY_FORMAT.format(day.getTime());
		final BasicDBObject update = new BasicDBObject().append("$set", new BasicDBObject("lastRetrieved." + formattedDay, retrieved.getTime()));
		
		collection().update(query, update, true /* upsert */, false);
	}

}
