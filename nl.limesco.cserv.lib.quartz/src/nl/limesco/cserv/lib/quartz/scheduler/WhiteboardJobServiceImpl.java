package nl.limesco.cserv.lib.quartz.scheduler;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.limesco.cserv.lib.quartz.annotations.Description;
import nl.limesco.cserv.lib.quartz.annotations.ModifiedByCalendar;
import nl.limesco.cserv.lib.quartz.annotations.Priority;
import nl.limesco.cserv.lib.quartz.annotations.RequestRecovery;
import nl.limesco.cserv.lib.quartz.annotations.StartNow;
import nl.limesco.cserv.lib.quartz.annotations.cron.Cron;
import nl.limesco.cserv.lib.quartz.annotations.dailytimeinterval.DaysOfTheWeek;
import nl.limesco.cserv.lib.quartz.annotations.dailytimeinterval.EndingDailyAt;
import nl.limesco.cserv.lib.quartz.annotations.dailytimeinterval.EveryDay;
import nl.limesco.cserv.lib.quartz.annotations.dailytimeinterval.Interval;
import nl.limesco.cserv.lib.quartz.annotations.dailytimeinterval.MondayThroughFriday;
import nl.limesco.cserv.lib.quartz.annotations.dailytimeinterval.SaturdayAndSunday;
import nl.limesco.cserv.lib.quartz.annotations.dailytimeinterval.StartingDailyAt;
import nl.limesco.cserv.lib.quartz.annotations.simple.RepeatCount;
import nl.limesco.cserv.lib.quartz.annotations.simple.RepeatForever;
import nl.limesco.cserv.lib.quartz.annotations.simple.RepeatInterval;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class WhiteboardJobServiceImpl {
    
    private volatile Scheduler m_scheduler;
    
    private volatile LogService m_logService;
    
    private final Map<Job, JobDetail> m_jobs = new HashMap<Job, JobDetail>();
    
    public void jobAdded(ServiceReference ref, Job job) {
        final Class<? extends Job> jobClass = job.getClass();
        
        final JobBuilder jobBuilder = JobBuilder.newJob();
        final TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        
        if (buildJobAndTrigger(jobBuilder, triggerBuilder, jobClass)) {
            final String group = "bundle" + ref.getBundle().getBundleId();
            final String name = jobClass.getName();
            jobBuilder.withIdentity(name, group);
            jobBuilder.ofType(jobClass);
            
            final JobDetail jobDetail = jobBuilder.build();
            final Trigger trigger = triggerBuilder.build();
            try {
                synchronized(m_jobs) {
                    m_scheduler.scheduleJob(jobDetail, trigger);
                    m_jobs.put(job, jobDetail);
                }
            } catch (SchedulerException e) {
                m_logService.log(LogService.LOG_WARNING, "Failed to schedule job", e);
            }
        }
    }
    
    public void jobRemoved(Job job) {
        synchronized(m_jobs) {
            final JobDetail jobDetail = m_jobs.get(job);
            try {
                m_scheduler.deleteJob(jobDetail.getKey());
                m_jobs.remove(job);
            } catch (SchedulerException e) {
                m_logService.log(LogService.LOG_WARNING, "Failed to delete job", e);
            }
        }
    }
    
    public void stop() {
        synchronized(m_jobs) {
            for (JobDetail job : m_jobs.values()) {
                try {
                    m_scheduler.deleteJob(job.getKey());
                } catch (SchedulerException e) {
                    m_logService.log(LogService.LOG_WARNING, "Failed to delete job", e);
                }
            }
        }
    }

    private boolean buildJobAndTrigger(JobBuilder jobBuilder, TriggerBuilder<Trigger> triggerBuilder, AnnotatedElement element) {
        final Description description = element.getAnnotation(Description.class);
        if (description != null) {
            jobBuilder.withDescription(description.value());
        }
        
        final RequestRecovery requestRecovery = element.getAnnotation(RequestRecovery.class);
        if (requestRecovery != null) {
            jobBuilder.requestRecovery(requestRecovery.value());
        }
        
        final ModifiedByCalendar modifiedByCalendar = element.getAnnotation(ModifiedByCalendar.class);
        if (modifiedByCalendar != null) {
            triggerBuilder.modifiedByCalendar(modifiedByCalendar.value());
        }
        
        final Priority priority = element.getAnnotation(Priority.class);
        if (priority != null) {
            triggerBuilder.withPriority(priority.value());
        }
        
        final StartNow startNow = element.getAnnotation(StartNow.class);
        if (startNow != null) {
            triggerBuilder.startNow();
        }
        
        final ScheduleBuilder<? extends Trigger> schedule = getScheduleFor(element);
        if (schedule != null) {
            triggerBuilder.withSchedule(schedule);
            return true;
        } else {
            return false;
        }
    }

    private ScheduleBuilder<? extends Trigger> getScheduleFor(AnnotatedElement element) {
        final Cron cron = element.getAnnotation(Cron.class);
        final RepeatInterval repeatInterval = element.getAnnotation(RepeatInterval.class);
        final Interval interval = element.getAnnotation(Interval.class);
        
        if (cron != null && repeatInterval == null && interval == null) {
            return CronScheduleBuilder.cronSchedule(cron.value());
        } else if (repeatInterval != null && interval == null && cron == null) {
            return getSimpleScheduleFor(element, repeatInterval);
        } else if (interval != null && cron != null && repeatInterval != null) {
            return getDailyTimeIntervalScheduleFor(element, interval);
        } else {
            return null;
        }
    }

    private ScheduleBuilder<? extends Trigger> getSimpleScheduleFor(AnnotatedElement element,
        final RepeatInterval repeatInterval) {
        final SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule();
        schedule.withIntervalInMilliseconds(repeatInterval.value() * repeatInterval.period());
        
        final RepeatCount repeatCount = element.getAnnotation(RepeatCount.class);
        if (repeatCount != null) {
            schedule.withRepeatCount(repeatCount.value());
        }
        
        final RepeatForever repeatForever = element.getAnnotation(RepeatForever.class);
        if (repeatForever != null) {
            schedule.repeatForever();
        }
        
        return schedule;
    }

    private ScheduleBuilder<? extends Trigger> getDailyTimeIntervalScheduleFor(AnnotatedElement element,
        final Interval interval) {
        final DailyTimeIntervalScheduleBuilder schedule = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule();
        schedule.withIntervalInSeconds(interval.value() * interval.period());
        
        final DaysOfTheWeek daysOfTheWeek = element.getAnnotation(DaysOfTheWeek.class);
        if (daysOfTheWeek != null) {
            final Set<Integer> days = new HashSet<Integer>();
            for (int day : daysOfTheWeek.value()) {
                days.add(day);
            }
            schedule.onDaysOfTheWeek(days);
        }
        
        final EveryDay everyDay = element.getAnnotation(EveryDay.class);
        if (everyDay != null) {
            schedule.onEveryDay();
        }
        
        final MondayThroughFriday mondayThroughFriday = element.getAnnotation(MondayThroughFriday.class);
        if (mondayThroughFriday != null) {
            schedule.onMondayThroughFriday();
        }
        
        final SaturdayAndSunday saturdayAndSunday = element.getAnnotation(SaturdayAndSunday.class);
        if (saturdayAndSunday != null) {
            schedule.onSaturdayAndSunday();
        }
        
        final StartingDailyAt startingDailyAt = element.getAnnotation(StartingDailyAt.class);
        if (startingDailyAt != null) {
            final TimeOfDay timeOfDay = new TimeOfDay(startingDailyAt.hour(), startingDailyAt.minute(), startingDailyAt.second());
            schedule.startingDailyAt(timeOfDay);
        }
        
        final EndingDailyAt endingDailyAt = element.getAnnotation(EndingDailyAt.class);
        if (endingDailyAt != null) {
            final TimeOfDay timeOfDay = new TimeOfDay(endingDailyAt.hour(), endingDailyAt.minute(), endingDailyAt.second());
            schedule.endingDailyAt(timeOfDay);
        }
        
        return schedule;
    }

}
