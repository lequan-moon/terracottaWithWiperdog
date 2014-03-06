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
import static org.quartz.TriggerBuilder.newTrigger
import static org.quartz.CronScheduleBuilder.cronSchedule;
import groovy.sql.Sql
import groovy.json.*
import org.codehaus.jackson.*

class Helper {
	def sched
	def shell
	def listOrphaneTrg = []
	def persistentDataMap = [:]
	def prevOUTPUTMap = [:]
	def lastExecutionMap = [:]
	def binding
	
	def Helper(){
	
	}
	
	def Helper(groovyShell, scheduler){
		sched = scheduler
		shell = groovyShell
	}
	
	def parseJobFile(jobFile){
		String jobName
		Map dataMap = [:]
		// loader.clearCache()
		// println "--LoadedClasses:" + loader.getLoadedClasses()
		binding = getBinding(jobFile)
		def dbconfig = getDBConfig()
		
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
		if(binding.hasVariable("QUERY")){
			dataMap["QUERY"] = binding["QUERY"]
		}
		if(binding.hasVariable("DBTYPE")){
			def conf = dbconfig[binding["DBTYPE"]]
			def sql = Sql.newInstance(conf['dbconnstr'], conf['user'], conf['password'], conf["driver"])
			binding.setVariable("sql", sql)
			dataMap['sql'] = sql
		}
		if(binding.hasVariable("ACCUMULATE")){
			dataMap["ACCUMULATE"] = binding["ACCUMULATE"]
		}
		if(binding.hasVariable("GROUPKEY")){
			dataMap["GROUPKEY"] = binding["GROUPKEY"]
		}
		def PERSISTENTDATA_File = new File("../tmp/monitorjobdata/PersistentData/" + jobName + ".txt")
		def prevOUTPUT_File = new File("../tmp/monitorjobdata/PrevOUTPUT/" + jobName + ".txt")
		def lastExecution_File = new File("../tmp/monitorjobdata/LastExecution/" + jobName + ".txt")
		binding = initPrevOutput(binding, prevOUTPUT_File)
		binding = initPERSISTENTDATA(binding, PERSISTENTDATA_File)
		binding = initLastexecution(binding, lastExecution_File)
		return dataMap
	}
	
	def initPrevOutput(binding, prevOUTPUT_File){
		def prevOutput = loadData(prevOUTPUT_File)
		binding.setVariable('prevOUTPUT', prevOutput)
		return binding
	}
	
	def initPERSISTENTDATA(binding, PERSISTENTDATA_File){
		def PERSISTENTDATA = loadData(PERSISTENTDATA_File)
		binding.setVariable('PERSISTENTDATA', PERSISTENTDATA)
		return binding
	}
	
	def initLastexecution(binding, lastExecution_File){
		def lastExecution = loadData(lastExecution_File, true)
		binding.setVariable('lastexecution', lastExecution)
		return binding
	}
	
	
	/**
	 * Load data PERSISTENTDATA and PrevOUTPUT
	 * @param dataFile File's path to store data
	 * @param dataMap DataMap to store data on memory
	 * @param jobName Job's name
	 * @return Data was read from file and set into dataMap
	 */
	def loadData(dataFile, isLastexecution = false) {
		def slurper = new JsonSlurper()
		def data = [:]
		// data = dataMap[jobName]
		// if (data == null){
			// check data file exist
			if (dataFile.exists()) {
				// if don't have Lastexecution
				if(!isLastexecution){
					// Get data of current job and set variable into objJob
					def line = dataFile.getText()
					if(!line.isEmpty()) {
						data = slurper.parseText(line)
						// dataMap[jobName] = data
					}
				} else {
					// have Lastexecution, Get lastexecution and interval of the current job
					def line = null
					dataFile.withReader { line = it.readLine() }
					if(!line.isEmpty()) {
						data = Integer.parseInt(line)
						// dataMap[jobName] = data
					}
				}
			}else if(!isLastexecution) {
				data = [:]
			}
		// } 
		return data
	}
	
	def getBinding(jobFile){
		GroovyShell shell = new GroovyShell()
		GroovyClassLoader loader = shell.classLoader
		def o = loader.parseClass(jobFile).newInstance()
		o.run()
		def binding = o.getBinding()
		return binding
	}
	
	/**
	 * Process to load and store Data
	 * @param binding Job's binding
	 * @param PERSISTENTDATA_File File's path of PERSISTENTDATA
	 * @param prevOUTPUT_File File's path of prevOUTPUT
	 * @param lastExecution_File File's path of lastExecution
	 */
	void initMonitoringJobData(binding, PERSISTENTDATA_File, prevOUTPUT_File, lastExecution_File){
		def jobName = getJobName()
		// Process to load and store PERSISTENTDATA
		// Get PERSISTENTDATA based on jobName
		def PERSISTENTDATA = [:]
		PERSISTENTDATA = loadData(PERSISTENTDATA_File, persistentDataMap, jobName)
		binding.setVariable("PERSISTENTDATA", PERSISTENTDATA)
		//End process to load and store PERSISTENTDATA
		
		// Process to load and store prevoutput
		// Get PrevOUTPUT of current job and set variable into objJob
		def prevOUTPUT = [:] 
		prevOUTPUT = loadData(prevOUTPUT_File, prevOUTPUTMap, jobName)
		binding.setVariable('prevOUTPUT', prevOUTPUT)
		//End process to load and store prevoutput
		
		// Process to load and store lastExecution
		// Get lastexecution and interval of the current job
		def lastExecution = loadData(lastExecution_File, lastExecutionMap, jobName, true);
		binding.setVariable('lastexecution', lastExecution)
	}
	
	/**
	 * Load data PERSISTENTDATA and PrevOUTPUT
	 * @param dataFile File's path to store data
	 * @param dataMap DataMap to store data on memory
	 * @param jobName Job's name
	 * @return Data was read from file and set into dataMap
	 */
	def loadData(dataFile, dataMap, jobName, isLastexecution = false) {
		def slurper = new JsonSlurper()
		def data = [:]
		data = dataMap[jobName]
		if (data == null){
			// check data file exist
			if (dataFile.exists()) {
				// if don't have Lastexecution
				if(!isLastexecution){
					// Get data of current job and set variable into objJob
					def line = dataFile.getText()
					if(!line.isEmpty()) {
						data = slurper.parseText(line)
						dataMap[jobName] = data
					}
				} else {
					// have Lastexecution, Get lastexecution and interval of the current job
					def line = null
					dataFile.withReader { line = it.readLine() }
					if(!line.isEmpty()) {
						data = Integer.parseInt(line)
						dataMap[jobName] = data
					}
				}
			}else if(!isLastexecution) {
				data = [:]
			}
		} 
		return data
	}
	
	def processJob(file){
		Thread.currentThread().contextClassLoader = shell.getClassLoader()
		// println "--process job file"
		def jobProp = getBinding(file)
		
		def jobClass = shell.getClassLoader().loadClass("CustomJob")
		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobProp["JOB"].name).storeDurably(true).build()
		// JobDetail job = JobBuilder.newJob(CustomJob.class).withIdentity(jobProp["JOB"]).storeDurably(true).build()
		sched.addJob(job, true);
		if(listOrphaneTrg != null && listOrphaneTrg.size() > 0){
			// println "---Process waitting trg"
			def listTrgDel = []
			listOrphaneTrg.each{trg->
				def trig = createTrigger(trg)
				def jobKey = new JobKey(trig.getKey().getName())
				if(sched.checkExists(jobKey)){
					sched.scheduleJob(trig)
				}
				listTrgDel.add(trg)
			}
			listOrphaneTrg.removeAll(listTrgDel)
		}
		// println "--Add job successfully"
	}
	
	def processTrigger(file){
		Thread.currentThread().contextClassLoader = shell.getClassLoader()
		// println "--process trigger file"
		def trigger
		file.eachLine{line->
			def oTrg = shell.evaluate("[" + line + "]")
			// println "---" + oTrg
			if(oTrg != null && oTrg.job != null && oTrg.schedule != null){
				def jobKey = new JobKey(oTrg.job)
				if(sched.checkExists(jobKey)){
					// Job exists
					// println "---job exists"
					if("delete".equals(oTrg.schedule)){
						sched.pauseJob(jobKey)
					}else{
						// println "----Create new trigger and schedule it"
						trigger = createTrigger(oTrg)
						// println "----" + trigger
						sched.scheduleJob(trigger)
					}
				}else{
					// Job doesn't exist
					// println "---job doesn't exist"
					if(!listOrphaneTrg.contains(oTrg)){
						listOrphaneTrg.add(oTrg)
					}
				}
			}
			if(trigger != null){
				if(!sched.checkExists(trigger.getKey())){
					// println "---Trigger doesn't exist"
					sched.scheduleJob(trigger)
				}else{
					// println "---Trigger exists"
					sched.unscheduleJob(trigger.getKey())
					sched.scheduleJob(trigger)
				}
			}
		}
		// println "--Process trigger successfully"
	}
	
	def createTrigger(oTrg){
		def trigger
		// println "-----oTrg.schedule class:" + oTrg.schedule.class.getName()
		def schedule = oTrg.schedule
		if(schedule.endsWith("i")){
			def interval = Integer.parseInt(schedule.substring(0, schedule.lastIndexOf('i')))
			trigger = newTrigger()
						.withIdentity(oTrg.job)
						.forJob(oTrg.job)
						.startNow()
						.withSchedule(new SimpleScheduleBuilder()
													.repeatForever()
													.withIntervalInSeconds(interval))
						.build();
		} else if(schedule == "now" || schedule == "NOW"){
			trigger = newTrigger()
						.withIdentity(oTrg.job)
						.forJob(oTrg.job)
						.startNow()
						.build();
		} else if(schedule ==~ /[^ ]+[ ]+[^ ]+[ ]+[^ ]+[ ]+[^ ]+[ ]+[^ ]+[ ]+[^ ]+/ ||
			schedule ==~ /[^ ]+[ ]+[^ ]+[ ]+[^ ]+[ ]+[^ ]+[ ]+[^ ]+[ ]+[^ ]+[ ]+[^ ]+/ ){
			trigger = newTrigger()
						.withIdentity(oTrg.job)
						.forJob(oTrg.job)
						.withSchedule(
							cronSchedule(schedule)
						)
						.withDescription(schedule)
						.build();
		} else {
			trigger = newTrigger()
						.withIdentity(oTrg.job)
						.forJob(oTrg.job)
						.startAt(futureDate(Integer.parseInt(schedule), IntervalUnit.MILLISECOND))
						.build();
		}
		
		return trigger
	}
	
	def getDBConfig(){
		GroovyShell shell = new GroovyShell()
		GroovyClassLoader loader = shell.classLoader
		def o = shell.evaluate(new File("../var/conf/dbconnect.cfg"))
		return o
		// def binding = o.getBinding()
		// def sql = Sql.newInstance(connstr, dbuser, decryptedPassword, strDriver)
		// return sql
	}
}
