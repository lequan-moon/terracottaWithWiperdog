@echo off
setlocal
SET INSTALL_DIR=%~dp0
SET GET_WIPERDOG="FALSE"
SET INSTALL_WIPERDOG="FALSE"
SET WITH_JOB_MANAGER="FALSE"
SET RUN_WIPERDOG="FALSE"
SET TC_URL=localhost:9510

if [%1]==[] goto help

for %%i IN (%*) DO (
	if "%%i"=="/h" goto help
	if "%%i"=="/gw" SET GET_WIPERDOG="TRUE"
	if "%%i"=="/iw" SET INSTALL_WIPERDOG="TRUE"
	if "%%i"=="/rw" SET RUN_WIPERDOG="TRUE"
	if "%%i"=="/wjm" SET WITH_JOB_MANAGER="TRUE"
)

if %GET_WIPERDOG%=="TRUE" (
	:: Getting wiperdog from maven
	echo "Getting wiperdog from maven. It could takes minutes..."
	echo "When maven is done, press any key..."
	cd "%INSTALL_DIR%"
	start call mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:get -Dartifact=org.wiperdog:wiperdog-assembly:0.2.4:jar:win -Ddest="%INSTALL_DIR%/wiperdog-assembly.jar" -Dmdep.useBaseVersion=true
	pause>null
)

if %INSTALL_WIPERDOG%=="TRUE" (
	java -jar "%INSTALL_DIR%/wiperdog-assembly.jar" -d "%INSTALL_DIR%/wiperdog" -j 13111 -m "localhost" -p 27017 -n "wiperdog" -s no
)

if %WITH_JOB_MANAGER%=="TRUE" (
	ren ""%INSTALL_DIR%""\\wiperdog\\bin\\ListBundle.csv ListBundle.csv_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\bin\ListBundle.csv wiperdog\\bin /Y
	ren ""%INSTALL_DIR%""\\wiperdog\\bin\\startGroovy startGroovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\bin\startGroovy wiperdog\\bin /Y
	ren ""%INSTALL_DIR%""\\wiperdog\\bin\\startGroovy.bat startGroovy.bat_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\bin\startGroovy.bat wiperdog\\bin /Y
	
	ren ""%INSTALL_DIR%""\\wiperdog\\etc\\boot.groovy boot.groovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\etc\boot.groovy wiperdog\\etc /Y
	ren ""%INSTALL_DIR%""\\wiperdog\\etc\\config.properties config.properties_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\etc\config.properties wiperdog\\etc /Y
	REM xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\etc\quartz.properties wiperdog\\etc /Y
	
	ren ""%INSTALL_DIR%"\\lib\\groovy\\libs.common\\MonitorJobConfigLoader.groovy MonitorJobConfigLoader.groovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\lib\groovy\libs.common\MonitorJobConfigLoader.groovy wiperdog\\lib\\groovy\\libs.common /Y
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\lib\groovy\libs.common\Terracotta_Prototype.groovy wiperdog\\lib\\groovy\\libs.common /Y
	
	ren ""%INSTALL_DIR%"\\lib\\groovy\\libs.target\\DefaultJobCaller.groovy DefaultJobCaller.groovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\lib\groovy\libs.target\DefaultJobCaller.groovy wiperdog\\lib\\groovy\\libs.target /Y
	ren ""%INSTALL_DIR%"\\lib\\groovy\\libs.target\\DefaultSender.groovy DefaultSender.groovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\lib\groovy\libs.target\DefaultSender.groovy wiperdog\\lib\\groovy\\libs.target /Y
	ren ""%INSTALL_DIR%"\\lib\\groovy\\libs.target\\GroovyScheduledJob.groovy GroovyScheduledJob.groovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\lib\groovy\libs.target\GroovyScheduledJob.groovy wiperdog\\lib\\groovy\\libs.target /Y
	ren ""%INSTALL_DIR%"\\lib\\groovy\\libs.target\\JobDsl.groovy JobDsl.groovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\lib\groovy\libs.target\JobDsl.groovy wiperdog\\lib\\groovy\\libs.target /Y
	
	ren ""%INSTALL_DIR%"\\lib\\java\\bundle\\org.wiperdog.jobmanager-0.2.1.jar org.wiperdog.jobmanager-0.2.1.jar_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\lib\java\bundle\org.wiperdog.jobmanager-0.2.1.jar wiperdog\\lib\\java\\bundle /Y
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\lib\java\bundle\quartz-2.2.1.jar wiperdog\\lib\\java\\bundle /Y
	
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\var\job\job1.job wiperdog\\var\\job /Y
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseJobManager\var\job\trigger.trg wiperdog\\var\\job /Y
)

if %WITH_JOB_MANAGER%=="FALSE" (
	ren ""%INSTALL_DIR%""\\wiperdog\\bin\\ListBundle.csv ListBundle.csv_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\bin\ListBundle.csv wiperdog\\bin /Y
	ren ""%INSTALL_DIR%""\\wiperdog\\bin\\startGroovy startGroovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\bin\startGroovy wiperdog\\bin /Y
	ren ""%INSTALL_DIR%""\\wiperdog\\bin\\startGroovy.bat startGroovy.bat_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\bin\startGroovy.bat wiperdog\\bin /Y
	
	ren ""%INSTALL_DIR%""\\wiperdog\\etc\\boot.groovy boot.groovy_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\etc\boot.groovy wiperdog\\etc /Y
	ren ""%INSTALL_DIR%""\\wiperdog\\etc\\config.properties config.properties_bak
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\etc\config.properties wiperdog\\etc /Y
	REM xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\etc\quartz.properties wiperdog\\etc /Y
	
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\lib\groovy\libs.common\Terracotta_Prototype.groovy wiperdog\\lib\\groovy\\libs.common /Y
	
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\lib\groovy\libs.target\CustomJob.groovy wiperdog\\lib\\groovy\\libs.target /Y
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\lib\groovy\libs.target\Helper.groovy wiperdog\\lib\\groovy\\libs.target /Y
	
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\lib\java\bundle\quartz-2.2.1.jar wiperdog\\lib\\java\\bundle /Y
	
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\var\conf\dbconnect.cfg wiperdog\\\var\\conf /Y
	
	if not exist "%INSTALL_DIR%"\wiperdog\var\job.old mkdir "%INSTALL_DIR%"\wiperdog\var\job.old
	move "%INSTALL_DIR%"\wiperdog\var\job\* "%INSTALL_DIR%"\wiperdog\var\job.old 2>NUL
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\var\job\job1.job wiperdog\\var\\job /Y
	xcopy "%INSTALL_DIR%"\terracottaWithWiperdogUseGroovyScript\var\job\a.trg wiperdog\\var\\job /Y
)

if %RUN_WIPERDOG%=="TRUE" (
	set /P input=Terracotta server address:
)
if %RUN_WIPERDOG%=="TRUE" (
	move /y "%INSTALL_DIR%\\wiperdog\\etc\\quartz.properties" "%INSTALL_DIR%\\wiperdog\\etc\\quartz.properties_bak"
	echo org.quartz.scheduler.instanceName:TestScheduler >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	echo org.quartz.scheduler.instanceId:groovy_instance >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	echo org.quartz.scheduler.skipUpdateCheck:true >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	echo org.quartz.threadPool.class:org.quartz.simpl.SimpleThreadPool >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	echo org.quartz.threadPool.threadCount:1 >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	echo org.quartz.threadPool.threadPriority:5 >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	echo org.quartz.jobStore.misfireThreshold:60000 >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	echo org.quartz.jobStore.class:org.terracotta.quartz.TerracottaJobStore >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	if [%input%]==[] (
		echo org.quartz.jobStore.tcConfigUrl:%TC_URL% >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	) else (
		echo org.quartz.jobStore.tcConfigUrl:%input% >> %INSTALL_DIR%/wiperdog/etc/quartz.properties
	)
	"%INSTALL_DIR%\\wiperdog\\bin\\startwiperdog.bat"
)

:help
echo This is a reproduce wiperdog with terracotta script.
echo startWiperdogWithTerracotta.bat [options]
echo options : 
echo - /gw : Get wiperdog installer from maven
echo - /iw : Install wiperdog from the installer
echo - /wjm : Use JobManager bundle in the wiperdog (If not, use JobManger embered in Groovy script)
echo - /rw : Run wiperdog
echo - /h : Open help

endlocal