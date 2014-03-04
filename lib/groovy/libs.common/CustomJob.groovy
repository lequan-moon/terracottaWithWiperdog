import org.quartz.Job
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;


class CustomJob implements Serializable, Job{
	static final long serialVersionUID = 1L;
	String jobName;
	Closure fetchAction;

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try{
			String jobName = arg0.getJobDetail().getKey().getName()
			Helper helper = new Helper()
			Map parsedData = helper.parseJobFile(new File("../var/job/${jobName}.job"))
			JobDataMap dataMap = arg0.getJobDetail().getJobDataMap()
			this.jobName = parsedData["JOB"]
			this.fetchAction = parsedData["FETCHACTION"]
			def result = execute()
			dataMap.data = result
		}catch(ex){
			ex.printStackTrace()
		}
	}
	
	def execute(){
		def result
		if(fetchAction != null){
			result = fetchAction.call()
		}
		println result
		return result
	}

}
