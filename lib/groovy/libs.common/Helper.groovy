import org.quartz.JobBuilder;
import org.quartz.JobDetail
import org.quartz.JobKey
import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger
import org.quartz.TriggerBuilder;
import org.quartz.DateBuilder.IntervalUnit;

class Helper {
	def sched
	def shell
	
	def Helper(){
	
	}
	
	def Helper(groovyShell, scheduler){
		sched = scheduler
		shell = groovyShell
	}
	
	def parseJobFile(jobFile){
		String jobName
		Map dataMap = [:]
		GroovyShell shell = new GroovyShell()
		GroovyClassLoader loader = shell.classLoader
		// loader.clearCache()
		// println "--LoadedClasses:" + loader.getLoadedClasses()
		def o = loader.parseClass(jobFile).newInstance()
		o.run()
		def binding = o.getBinding()
		binding.setVariable("externalVariable", 12345)
	
		if(binding.hasVariable("JOB")){
			jobName = binding["JOB"].name
			dataMap["JOB"] = jobName
		}else{
			jobName = jobFile.getName().replace(".job", "")
			dataMap["JOB"] = jobName
		}
		if(binding.hasVariable("FETCHACTION")){
			dataMap["FETCHACTION"] = binding["FETCHACTION"]
		}
		return dataMap
	}
	
	def processJob(file){
		println "--process job file"
		def jobProp = parseJobFile(file)
		def jobClass = shell.getClassLoader().loadClass("CustomJob")
		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobProp["JOB"]).storeDurably(true).build()
		sched.addJob(job, true);
		println "--Add job successfully"
	}
	
	def processTrigger(file){
		println "--process trigger file"
		def trigger
		file.eachLine{line->
			def oTrg = shell.evaluate("[" + line + "]")
			println "---" + oTrg
			if(oTrg != null && oTrg.job != null && oTrg.schedule != null){
				def jobKey = new JobKey(oTrg.job)
				if(sched.checkExists(jobKey)){
					// Job exists
					println "---job exists"
					if("delete".equals(oTrg.schedule)){
						println "----delete $jobKey :" + sched.deleteJob(jobKey)
					}else{
						println "----Create new trigger and schedule it"
						trigger = createTrigger(oTrg)
						println "----" + trigger
						// sched.scheduleJob(trigger)
					}
				}else{
					// Job doesn't exist
					// Implemets code for waiting trigger
					println "---job doesn't exist"
				}
			}
		}
		if(trigger != null){
			if(!sched.checkExists(trigger.getKey())){
				println "---Trigger doesn't exist"
				sched.scheduleJob(trigger)
			}else{
				println "---Trigger exists"
				sched.unscheduleJob(trigger.getKey())
				sched.scheduleJob(trigger)
			}
		}else{
			
		}
		println "--Add trigger successfully"
	}
	
	def createTrigger(oTrg){
		// println "-----oTrg.schedule class:" + oTrg.schedule.class.getName()
		def trigger = TriggerBuilder
							.newTrigger()
							.withIdentity(oTrg.job)
							.forJob(oTrg.job)
							.startNow()
							.withSchedule(new SimpleScheduleBuilder()
														.repeatForever()
														.withIntervalInSeconds(10))
							.build();
		return trigger
	}
}
