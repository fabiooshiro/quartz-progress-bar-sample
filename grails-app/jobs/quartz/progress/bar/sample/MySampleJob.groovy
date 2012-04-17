package quartz.progress.bar.sample

public class MySampleJob {
    
	static triggers = {
        cron name: 'MySampleProgressJob', cronExpression: "1/120 * * * * ?"
    }
    
	def group = 'MySampleJobs'
	
	def grailsApplication

	def execute(context) {
		def progressData = context.mergedJobDataMap.get("quartzProgressData")
        progressData.total = 331
		331.times{
            sleep(512)
            progressData.step = it + 1
            progressData.msg = "Step ${progressData.step} of ${progressData.total}"
            println "Step ${progressData.step} of ${progressData.total}"
        }
        progressData.msg = "Done..."
	}
}
