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
import org.wiperdog.directorywatcher.Listener

class Terracotta_Prototype implements Listener{
	def context
	def shell
	def dir
	def interval = 5000
	def sched
	def helper
	
	def Terracotta_Prototype(shell1, ctx){
		context = ctx
		shell = shell1
		MonitorJobConfigLoader configLoader = new MonitorJobConfigLoader(context)
		def properties = configLoader.getProperties();
		dir = properties.get(ResourceConstants.JOB_DIRECTORY)
		shell.getClassLoader().loadClass("DefaultSender")
		shell.getClassLoader().loadClass("MathFuncUltilities")
		shell.getClassLoader().loadClass("CustomJob")
		try{
			println "----------------------------------------------"
			println "----------Terracotta + OGSI + Groovy----------"
			println "----------------------------------------------"
			final String JOB = "JOB"
			final String FETCHACTION = "FETCHACTION"
			
			// ------Init Quarzt scheduler programmatically------
			def sf = new StdSchedulerFactory()
			// def schedProp = new Properties()
			// schedProp.setProperty("org.quartz.scheduler.instanceName", "TestScheduler") 
			// schedProp.setProperty("org.quartz.scheduler.instanceId", "groovy_instance") 
			// schedProp.setProperty("org.quartz.scheduler.skipUpdateCheck", "true") 
			// schedProp.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool") 
			// schedProp.setProperty("org.quartz.threadPool.threadCount", "1") 
			// schedProp.setProperty("org.quartz.threadPool.threadPriority", "5") 
			// schedProp.setProperty("org.quartz.jobStore.misfireThreshold", "60000") 
			// schedProp.setProperty("org.quartz.jobStore.class", "org.terracotta.quartz.TerracottaJobStore") 
			// schedProp.setProperty("org.quartz.jobStore.tcConfigUrl", "10.0.0.107:9510") 
			// schedProp.setProperty("org.quartz.scheduler.classLoadHelper.class", "org.quartz.simpl.LoadingLoaderClassLoadHelper")
			// sf.initialize(schedProp) 
			sched = sf.getScheduler()
			// ------/Init Quarzt scheduler programmatically------
			Thread.currentThread().contextClassLoader = shell.getClassLoader()
			println "--Start scheduler--"
			sched.start();
			
			def helperCls = shell.getClassLoader().loadClass("Helper")
			helper = helperCls.newInstance(shell, sched)
			// helper = new Helper(shell, sched)
		} catch(Exception ex){
			// println ex
			ex.printStackTrace();
		}
		
		
	}
	public boolean notifyAdded(File target) throws IOException {
		return processFile(target);
	}

	public boolean notifyDeleted(File target) throws IOException {
		return false;
	}

	public boolean notifyModified(File target) throws IOException {
		return processFile(target);
	}
	
	public boolean filterFile(File file) {
		return file.getName().endsWith(".job") || file.getName().endsWith(".cls") || file.getName().endsWith(".trg") || file.getName().endsWith(".instances");
	}

	public String getDirectory() {
		return dir;
	}
	
	public long getInterval() {
		return interval;
	}
	
	def processFile(file){
		try{
			if(file.getName().endsWith(".job")){
				helper.processJob(file)
			}
			if(file.getName().endsWith(".trg")){
				helper.processTrigger(file)
			}
		}catch(ex){
			ex.printStackTrace()
		}
		return true
	}
}