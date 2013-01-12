package nl.limesco.cserv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import nl.limesco.cserv.invoice.api.BatchInvoicingService;
import nl.limesco.cserv.lib.quartz.annotations.StartNow;
import nl.limesco.cserv.lib.quartz.annotations.simple.RepeatCount;
import nl.limesco.cserv.lib.quartz.annotations.simple.RepeatInterval;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@StartNow
@RepeatInterval(1)
@RepeatCount(0)
public class Perform implements Job {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private volatile BatchInvoicingService service;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(DAY_FORMAT.parse("12-01-2013"));
		} catch (ParseException e) {
			throw new JobExecutionException(e);
		}
//		service.runBatch(cal);
	}

}
