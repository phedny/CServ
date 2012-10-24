package nl.limesco.cserv.cdr.job;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrRetriever;
import nl.limesco.cserv.cdr.api.CdrService;
import nl.limesco.cserv.lib.quartz.annotations.StartNow;
import nl.limesco.cserv.lib.quartz.annotations.cron.Cron;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@StartNow
@Cron("14 4/6 * * * ?") // Run every six minutes
public class CdrRetrievalJob implements Job {
	
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};
	
	private volatile LogService logService;

	private volatile JobStateService jobStateService;
	
	private volatile CdrService cdrService;
	
	private final Map<String, CdrRetriever> retrievers = Maps.newHashMap();
	
	private final ReadWriteLock retrieversLock = new ReentrantReadWriteLock();
	
	public void retrieverAdded(ServiceReference ref, CdrRetriever retriever) {
		final String source = (String) ref.getProperty("source");
		if (source == null) {
			return;
		}
		
		final Lock lock = retrieversLock.writeLock();
		lock.lock();
		try {
			if (retrievers.containsKey(source)) {
				logService.log(LogService.LOG_WARNING, "Retriever with duplicate source detected: " + source);
			}
			retrievers.put(source, retriever);
		} finally {
			lock.unlock();
		}
	}
	
	public void retrievedRemoved(ServiceReference ref, CdrRetriever retriever) {
		final String source = (String) ref.getProperty("source");
		if (source == null) {
			return;
		}
		
		final Lock lock = retrieversLock.writeLock();
		lock.lock();
		try {
			if (retrievers.get(source) == retriever) {
				retrievers.remove(source);
			}
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		final Set<String> handledSources = Sets.newHashSet();
		final Lock lock = retrieversLock.readLock();
		
		while (true) {
			final String source;
			final CdrRetriever retriever;
			
			lock.lock();
			try {
				final SetView<String> set = Sets.difference(retrievers.keySet(), handledSources);
				if (set.isEmpty()) {
					return;
				} else {
					source = set.iterator().next();
					handledSources.add(source);
					retriever = retrievers.get(source);
				}
			} finally {
				lock.unlock();
			}
			
			retrieveFromSource(source, retriever);
		}
	}

	private void retrieveFromSource(String source, CdrRetriever retriever) {
		for (Calendar day : jobStateService.getDaysToUpdateForSource(source)) {
			final Calendar retrievalStarted = Calendar.getInstance();
			final String formattedDay = DAY_FORMAT.format(day.getTime());
			logService.log(LogService.LOG_INFO, "Retrieving CDRs from " + source + " for day " + formattedDay);
			try {
				for (Cdr cdr : retriever.retrieveCdrsForDay(day)) {
					cdrService.storeCdr(cdr);
				}
				
				jobStateService.updateJobState(source, day, retrievalStarted);
			} catch (IOException e) {
				logService.log(LogService.LOG_WARNING, "Failed to retrieve CDRs from " + source + " for day " + formattedDay, e);
			}
		}
	}

}
