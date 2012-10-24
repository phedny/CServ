package nl.limesco.cserv.cdr.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class JobStateServiceTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private static final SimpleDateFormat DAY_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private JobStateServiceImpl jobStateService;
	
	@Before
	public void setUp() {
		jobStateService = new JobStateServiceImpl();
	}
	
	@Test
	public void allDaysMustBeRetrievedWhenNoDaysHaveBeenRetrieved() throws Exception {
		final JobState jobState = new JobState();
		jobState.setEarliestDay(calendar("05-01-2012"));
		
		Iterator<Calendar> days = jobStateService.getDaysToUpdate(jobState, calendar("08-01-2012")).iterator();
		assertEquals(calendar("05-01-2012"), days.next());
		assertEquals(calendar("06-01-2012"), days.next());
		assertEquals(calendar("07-01-2012"), days.next());
		assertFalse(days.hasNext());
	}
	
	@Test
	public void onlyTodayMustBeRetrievedIfAllDaysHaveJustBeenRetrieved() throws Exception {
		final JobState jobState = new JobState();
		jobState.setEarliestDay(calendar("05-01-2012"));
		
		final Map<String, Calendar> lastRetrieved = Maps.newHashMap();
		lastRetrieved.put("20120105", calendar("07-01-2012"));
		lastRetrieved.put("20120106", calendar("07-01-2012"));
		lastRetrieved.put("20120107", calendar("07-01-2012"));
		jobState.setLastRetrieved(lastRetrieved);
		
		Iterator<Calendar> days = jobStateService.getDaysToUpdate(jobState, calendar("08-01-2012")).iterator();
		assertEquals(calendar("07-01-2012"), days.next());
		assertFalse(days.hasNext());
	}

	@Test
	public void retrieveOneDayAtLastRetrievalOfTheDay() throws Exception {
		final JobState jobState = new JobState();
		jobState.setEarliestDay(calendar("03-01-2012"));
		
		final Map<String, Calendar> lastRetrieved = Maps.newHashMap();
		lastRetrieved.put("20120103", calendar("05-01-2012", "00:04:14"));
		lastRetrieved.put("20120104", calendar("06-01-2012", "00:04:14"));
		lastRetrieved.put("20120105", calendar("07-01-2012", "00:04:14"));
		lastRetrieved.put("20120106", calendar("07-01-2012", "00:04:14"));
		lastRetrieved.put("20120107", calendar("07-01-2012", "00:52:14"));
		jobState.setLastRetrieved(lastRetrieved);
		
		Iterator<Calendar> days = jobStateService.getDaysToUpdate(jobState, calendar("08-01-2012")).iterator();
		assertEquals(calendar("07-01-2012"), days.next());
		assertFalse(days.hasNext());
	}

	@Test
	public void retrieveThreeDaysAtFirstRetrievalOfTheDay() throws Exception {
		final JobState jobState = new JobState();
		jobState.setEarliestDay(calendar("03-01-2012"));
		
		final Map<String, Calendar> lastRetrieved = Maps.newHashMap();
		lastRetrieved.put("20120103", calendar("05-01-2012", "00:04:14"));
		lastRetrieved.put("20120104", calendar("06-01-2012", "00:04:14"));
		lastRetrieved.put("20120105", calendar("07-01-2012", "00:04:14"));
		lastRetrieved.put("20120106", calendar("07-01-2012", "00:04:14"));
		lastRetrieved.put("20120107", calendar("07-01-2012", "00:58:14"));
		jobState.setLastRetrieved(lastRetrieved);
		
		Iterator<Calendar> days = jobStateService.getDaysToUpdate(jobState, calendar("09-01-2012")).iterator();
		assertEquals(calendar("06-01-2012"), days.next());
		assertEquals(calendar("07-01-2012"), days.next());
		assertEquals(calendar("08-01-2012"), days.next());
		assertFalse(days.hasNext());
	}

	@Test
	public void retrieveOneDayAtSecondRetrievalOfTheDay() throws Exception {
		final JobState jobState = new JobState();
		jobState.setEarliestDay(calendar("03-01-2012"));
		
		final Map<String, Calendar> lastRetrieved = Maps.newHashMap();
		lastRetrieved.put("20120103", calendar("05-01-2012", "00:04:14"));
		lastRetrieved.put("20120104", calendar("06-01-2012", "00:04:14"));
		lastRetrieved.put("20120105", calendar("07-01-2012", "00:04:14"));
		lastRetrieved.put("20120106", calendar("08-01-2012", "00:04:14"));
		lastRetrieved.put("20120107", calendar("08-01-2012", "00:04:14"));
		lastRetrieved.put("20120108", calendar("08-01-2012", "00:04:14"));
		jobState.setLastRetrieved(lastRetrieved);
		
		Iterator<Calendar> days = jobStateService.getDaysToUpdate(jobState, calendar("09-01-2012")).iterator();
		assertEquals(calendar("08-01-2012"), days.next());
		assertFalse(days.hasNext());
	}

	private Calendar calendar(String day) throws ParseException {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DAY_FORMAT.parse(day));
		return calendar;
	}

	private Calendar calendar(String day, String time) throws ParseException {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DAY_TIME_FORMAT.parse(day + " " + time));
		return calendar;
	}
	
}
