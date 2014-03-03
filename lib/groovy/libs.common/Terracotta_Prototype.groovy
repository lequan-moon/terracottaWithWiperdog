import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger
import org.quartz.TriggerBuilder;
import org.quartz.DateBuilder.IntervalUnit;

import java.util.HashSet
import java.util.Properties

import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.DirectSchedulerFactory
import org.quartz.spi.ThreadPool
import org.quartz.spi.JobStore
import org.quartz.Job

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.SimpleScheduleBuilder;
import org.codehaus.groovy.tools.RootLoader

class Terracotta_Prototype{
	def context
	def rootloader
	
	def Terracotta_Prototype(){
		try{
			println "----------------------------------------------"
			println "----------Terracotta + OGSI + Groovy----------"
			println "----------------------------------------------"
			final String JOB = "JOB"
			final String FETCHACTION = "FETCHACTION"
			
			// ------Init Quarzt scheduler programmatically------
			def sf = new StdSchedulerFactory()
			def schedProp = new Properties()
			schedProp.setProperty("org.quartz.scheduler.instanceName", "TestScheduler") 
			schedProp.setProperty("org.quartz.scheduler.instanceId", "groovy_instance") 
			schedProp.setProperty("org.quartz.scheduler.skipUpdateCheck", "true") 
			schedProp.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool") 
			schedProp.setProperty("org.quartz.threadPool.threadCount", "1") 
			schedProp.setProperty("org.quartz.threadPool.threadPriority", "5") 
			schedProp.setProperty("org.quartz.jobStore.misfireThreshold", "60000") 
			schedProp.setProperty("org.quartz.jobStore.class", "org.terracotta.quartz.TerracottaJobStore") 
			schedProp.setProperty("org.quartz.jobStore.tcConfigUrl", "10.0.0.107:9510") 
			sf.initialize(schedProp) 
			def sched = sf.getScheduler()
			// ------/Init Quarzt scheduler programmatically------
						
			JobDetail job = JobBuilder.newJob(AJob1.class).withIdentity("job1").storeDurably(true).build()
			sched.addJob(job, true);
			def trigger = TriggerBuilder.newTrigger().forJob(job).startNow().withSchedule(new SimpleScheduleBuilder().repeatForever().withIntervalInSeconds(10)).build();
			sched.scheduleJob(trigger)
			println "--Start scheduler--"
			sched.start();
		} catch(Exception ex){
			// println ex
			ex.printStackTrace();
		}
	}
}

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
class AJob1 implements Job{
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		println "Executing job..."
	}
}