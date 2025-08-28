# InfiniteLocus-UDC
# qa-automation

All in one - Automation Testing Framework

Make sure to check configurations.properties file

For Local Run - Make sure to set the same to true

Prerequisites - Maven and JDK 11 should be installed

Steps :

1. Clone and Authenticate

2. Run using

To run the functional tests : (Make sure to take latest pull)

git checkout master

mvn clean install surefire:test -Dsurefire.suiteXmlFiles=Testng/api_<project>.xml


**To run the performance tests :**

Clone https://github.com/gatling/gatling-demostore
Change Target Language in pom to Java 11

Run below command to locally host test application : 

mvn spring-boot:run

Run Performance Test Using Below Command :

mvn clean test-compile gatling:test -Dgatling.simulationClass=perf.demo.SimulationDemoStoreUserJourneysRestructured -DUSERS=2 -DRAMP_DURATION=5 -DTEST_DURATION=10

