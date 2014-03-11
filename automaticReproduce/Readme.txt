----------Demo tutorial---------------------------------
1. Start terracotta server:
	1.1 Checkout from SVN, update some pom files, built necessary libaries, follow this link:
		https://github.com/wiperdog/experimental/tree/master/terracotta
	1.2 Deploy:
		Go to checkouted folder of http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/deploy, run command:
		mvn exec:exec -P start-server
2. Checkout sources:
	https://github.com/dothihuong-luvina/terracottaWithWiperdog.git
3. Copy file startWiperdogWithTerracotta.bat/startWiperdogWithTerracotta.sh to terracottaWithWiperdog
4. Run file startWiperdogWithTerracotta.bat/startWiperdogWithTerracotta.sh
   Use /h for help

-----------About jar files in step 2 and how to recreate them(just saying if you want to create your own)---------------
There are 2 libraries(jar) need to be changed when using Wiperdog with terrracotta:
1. lib\java\bundle\org.wiperdog.jobmanager-0.2.1.jar (Case use jobmanager)
	Checkout : https://github.com/dothihuong-luvina/org.wiperdog.jobmanager.git
	Built by maven: mvn clean install -DskipTests
	Built jar can be located in target folder
2. lib\java\bundle\quartz-2.2.1.jar (Both cases)
	1. Checkout from SVN, update pom files, built necessary libaries, follow this link:
		https://github.com/wiperdog/experimental/tree/master/terracotta
	2. Merge necessary source, Create bundle quartz contains terracotta library:
	   How to:
		Change jar -> zip
		Extract all
		Compress .zip then change name to quartz-2.2.1.jar
	   These are needed files:
		bsh-2.0b4.jar (maven)
		build-data-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/build-data)
		common-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/common)
		commons-cli-1.1.jar (maven)
		commons-io-2.4.jar (maven)
		commons-lang-2.0.jar (maven)
		dso-cluster-api-4.1.1.jar(http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/dso-cluster-api)
		dso-common-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/dso-common)
		dso-l1-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/dso-l1)
		dso-l1-api-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/dso-l1-api)
		ehcache-core-2.8.1.jar (http://svn.terracotta.org/svn/ehcache/tags/ehcache-2.8.1/ehcache-core)
		guava-13.0.1.jar (maven)
		jmxremote_optional-tc-1.0.4.jar (maven, downloaded when building source terracotta: .m2\repository\org\terracotta\jmxremote_optional-tc\1.0.4)
		log4j-1.2.16.jar (maven)
		management-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/management)
		search-1.4.5.jar (maven, downloaded when building source terracotta : .m2\repository\com\terracottatech\search\1.4.5)
		statistics-1.0.1.jar (maven, downloaded when building source terracotta : .m2\repository\org\terracotta\internal\statistics\1.0.1)
		tcconfig-4.2.2.jar (maven, downloaded when building source terracotta : .m2\repository\org\terracotta\tcconfig\4.2.2)
		tc-l1-reconnect-properties-4.2.2.jar (maven, downloaded when building source terracotta : .m2\repository\org\terracotta\tc-l1-reconnect-properties\4.2.2)
		terracotta-toolkit-api-2.5.jar (maven, downloaded when building source terracotta : .m2\repository\org\terracotta\toolkit\terracotta-toolkit-api\2.5)
		terracotta-toolkit-api-internal-1.12.jar (maven, downloaded when building source source terracotta : .m2\repository\org\terracotta\toolkit\terracotta-toolkit-api-internal\1.12)
		terracotta-toolkit-impl-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/toolkit-impl)
		terracotta-toolkit-runtime-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/toolkit-runtime)
		toolkit-express-impl-4.1.1.jar (http://svn.terracotta.org/svn/tc/dso/tags/4.1.1/toolkit-express-impl)
		xmlbeans-2.4.0.jar (maven)
		quartz-2.2.1.jar (http://svn.terracotta.org/svn/quartz/tags/quartz-2.2.1/quartz)
		
		(Note: quartz-2.2.1.jar must be merge last)