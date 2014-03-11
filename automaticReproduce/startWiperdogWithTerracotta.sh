self="$0"
while [ -h "$self" ]; do
	res=`ls -ld "$self"`
	ref=`expr "$res" : '.*-> \(.*\)$'`
	if expr "$ref" : '/.*' > /dev/null; then
		self="$ref"
	else
		self="`dirname \"$self\"`/$ref"
	fi
done

dir=`dirname "$self"`
CUR_DIR=`cd "$dir/" && pwd`

function usage
(
    echo This is a reproduce wiperdog with terracotta script.
	echo startWiperdogWithTerracotta.bat [options]
	echo options : 
	echo - /gw : Get wiperdog installer from maven
	echo - /iw : Install wiperdog from the installer
	echo - /wjm : Use JobManager bundle in the wiperdog (If not, use JobManger embered in Groovy script)
	echo - /rw : Run wiperdog
	echo - /h : Open help
)

if [ $# -eq 0 ]
  then
    usage
	exit
fi

# Default value of variables
GET_WIPERDOG="FALSE"
INSTALL_WIPERDOG="FALSE"
WITH_JOB_MANAGER="FALSE"
RUN_WIPERDOG="FALSE"

# Get input parameters
while [ "$1" != "" ]; do
	case $1 in 
		/gw) GET_WIPERDOG="TRUE"
		;;
		/iw) INSTALL_WIPERDOG="TRUE"
		;;
		/rw) RUN_WIPERDOG="TRUE"
		;;
		/wjm) WITH_JOB_MANAGER="TRUE"
		;;
		/h | /help)	usage
				exit
		;;
		* )	usage
			exit 1
	esac
	shift
done

# GET WIPERDOG FROM MAVEN BY mvn COMMAND
if [ $GET_WIPERDOG = "TRUE" ]; then
	echo "Getting wiperdog from maven. It could takes minutes..."
	mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:get -Dartifact=org.wiperdog:wiperdog-assembly:0.2.4:jar:unix -Ddest=$CUR_DIR/wiperdog-assembly.jar -Dmdep.useBaseVersion=true
fi 

# INSTALL WIPERDOG
if [ $INSTALL_WIPERDOG = "TRUE" ]; then
	java -jar wiperdog-assembly.jar -d $CUR_DIR/wiperdog -j 13111 -m "localhost" -p 27017 -n "wiperdog" -mp "" -s no

	dos2unix wiperdog/bin/*
fi

# INSTALL WIPERDOG WITHOUT JOB MANAGER
if [ $WITH_JOB_MANAGER = "FALSE" ]; then
	echo "NO JOB MANAGER"
	mv wiperdog/bin/ListBundle.csv wiperdog/bin/ListBundle.csv_bak
	cp terracottaWithWiperdogUseGroovyScript/bin/ListBundle.csv wiperdog/bin/ListBundle.csv
	mv wiperdog/bin/startGroovy wiperdog/bin/startGroovy_bak
	cp terracottaWithWiperdogUseGroovyScript/bin/startGroovy wiperdog/bin/startGroovy
	mv wiperdog/bin/startGroovy.bat wiperdog/bin/startGroovy.bat_bak
	cp terracottaWithWiperdogUseGroovyScript/bin/startGroovy.bat wiperdog/bin/startGroovy.bat

	mv wiperdog/etc/boot.groovy wiperdog/etc/boot.groovy_bak
	cp terracottaWithWiperdogUseGroovyScript/etc/boot.groovy wiperdog/etc/boot.groovy
	mv wiperdog/etc/config.properties wiperdog/etc/config.properties_bak
	cp terracottaWithWiperdogUseGroovyScript/etc/config.properties wiperdog/etc/config.properties
	# cp terracottaWithWiperdogUseGroovyScript/etc/quartz.properties wiperdog/etc/quartz.properties

	cp terracottaWithWiperdogUseGroovyScript/lib/groovy/libs.common/Terracotta_Prototype.groovy wiperdog/lib/groovy/libs.common/Terracotta_Prototype.groovy

	cp terracottaWithWiperdogUseGroovyScript/lib/groovy/libs.target/CustomJob.groovy wiperdog/lib/groovy/libs.target/CustomJob.groovy
	cp terracottaWithWiperdogUseGroovyScript/lib/groovy/libs.target/Helper.groovy wiperdog/lib/groovy/libs.target/Helper.groovy

	cp terracottaWithWiperdogUseGroovyScript/lib/java/bundle/quartz-2.2.1.jar wiperdog/lib/java/bundle/quartz-2.2.1.jar

	cp terracottaWithWiperdogUseGroovyScript/var/conf/dbconnect.cfg wiperdog/var/conf/dbconnect.cfg
	
	rm -f wiperdog/var/job/*.*
	cp terracottaWithWiperdogUseGroovyScript/var/job/a.trg wiperdog/var/job/a.trg
	cp terracottaWithWiperdogUseGroovyScript/var/job/job1.job wiperdog/var/job/job1.job
fi

# INSTALL WIPERDOG WITH JOB MANAGER
if [ $WITH_JOB_MANAGER = "TRUE" ]; then
	echo "WITH JOB MANAGER"
	mv wiperdog/bin/ListBundle.csv wiperdog/bin/ListBundle.csv_bak
	cp terracottaWithWiperdogUseJobManager/bin/ListBundle.csv wiperdog/bin/ListBundle.csv
	mv wiperdog/bin/startGroovy wiperdog/bin/startGroovy_bak
	cp terracottaWithWiperdogUseJobManager/bin/startGroovy wiperdog/bin/startGroovy
	mv wiperdog/bin/startGroovy.bat wiperdog/bin/startGroovy.bat_bak
	cp terracottaWithWiperdogUseJobManager/bin/startGroovy.bat wiperdog/bin/startGroovy.bat

	mv wiperdog/etc/boot.groovy wiperdog/etc/boot.groovy_bak
	cp terracottaWithWiperdogUseJobManager/etc/boot.groovy wiperdog/etc/boot.groovy
	mv wiperdog/etc/config.properties wiperdog/etc/config.properties_bak
	cp terracottaWithWiperdogUseJobManager/etc/config.properties wiperdog/etc/config.properties
	# cp terracottaWithWiperdogUseJobManager/etc/quartz.properties wiperdog/etc/quartz.properties

	mv wiperdog/lib/groovy/libs.common/MonitorJobConfigLoader.groovy wiperdog/lib/groovy/libs.common/MonitorJobConfigLoader.groovy_bak
	cp terracottaWithWiperdogUseJobManager/lib/groovy/libs.common/MonitorJobConfigLoader.groovy wiperdog/lib/groovy/libs.common/MonitorJobConfigLoader.groovy
	cp terracottaWithWiperdogUseJobManager/lib/groovy/libs.common/Terracotta_Prototype.groovy wiperdog/lib/groovy/libs.common/Terracotta_Prototype.groovy

	mv wiperdog/lib/groovy/libs.target/DefaultJobCaller.groovy wiperdog/lib/groovy/libs.target/DefaultJobCaller.groovy_bak
	cp terracottaWithWiperdogUseJobManager/lib/groovy/libs.target/DefaultJobCaller.groovy wiperdog/lib/groovy/libs.target/DefaultJobCaller.groovy
	mv wiperdog/lib/groovy/libs.target/DefaultSender.groovy wiperdog/lib/groovy/libs.target/DefaultSender.groovy_bak
	cp terracottaWithWiperdogUseJobManager/lib/groovy/libs.target/DefaultSender.groovy wiperdog/lib/groovy/libs.target/DefaultSender.groovy
	mv wiperdog/lib/groovy/libs.target/GroovyScheduledJob.groovy wiperdog/lib/groovy/libs.target/GroovyScheduledJob.groovy_bak
	cp terracottaWithWiperdogUseJobManager/lib/groovy/libs.target/GroovyScheduledJob.groovy wiperdog/lib/groovy/libs.target/GroovyScheduledJob.groovy
	mv wiperdog/lib/groovy/libs.target/JobDsl.groovy wiperdog/lib/groovy/libs.target/JobDsl.groovy_bak
	cp terracottaWithWiperdogUseJobManager/lib/groovy/libs.target/JobDsl.groovy wiperdog/lib/groovy/libs.target/JobDsl.groovy

	mv wiperdog/lib/java/bundle/org.wiperdog.jobmanager-0.2.1.jar wiperdog/lib/java/bundle/org.wiperdog.jobmanager-0.2.1.jar_bak
	cp terracottaWithWiperdogUseJobManager/lib/java/bundle/org.wiperdog.jobmanager-0.2.1.jar wiperdog/lib/java/bundle/org.wiperdog.jobmanager-0.2.1.jar
	cp terracottaWithWiperdogUseJobManager/lib/java/bundle/quartz-2.2.1.jar wiperdog/lib/java/bundle/quartz-2.2.1.jar

	cp terracottaWithWiperdogUseJobManager/var/job/job1.job wiperdog/var/job/job1.job
	cp terracottaWithWiperdogUseJobManager/var/job/trigger.trg wiperdog/var/job/trigger.trg
fi

# START WIPERDOG
if [ $RUN_WIPERDOG = "TRUE" ]; then

	echo "Terracotta server address:"
	read TC_URL
	TC_URL=TC_URL | tr -d ' '
	if [ "$TC_URL" = "" ]; then
		TC_URL=localhost:9510
	fi
	
	mv -f wiperdog/etc/quartz.properties wiperdog/etc/quartz.properties_bak
	echo org.quartz.scheduler.instanceName:TestScheduler >> wiperdog/etc/quartz.properties
	echo org.quartz.scheduler.instanceId:groovy_instance >> wiperdog/etc/quartz.properties
	echo org.quartz.scheduler.skipUpdateCheck:true >> wiperdog/etc/quartz.properties
	echo org.quartz.threadPool.class:org.quartz.simpl.SimpleThreadPool >> wiperdog/etc/quartz.properties
	echo org.quartz.threadPool.threadCount:1 >> wiperdog/etc/quartz.properties
	echo org.quartz.threadPool.threadPriority:5 >> wiperdog/etc/quartz.properties
	echo org.quartz.jobStore.misfireThreshold:60000 >> wiperdog/etc/quartz.properties
	echo org.quartz.jobStore.class:org.terracotta.quartz.TerracottaJobStore >> wiperdog/etc/quartz.properties
	echo org.quartz.jobStore.tcConfigUrl:$TC_URL >> wiperdog/etc/quartz.properties
	./wiperdog/bin/startWiperdog.sh
fi
