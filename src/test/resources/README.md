Make sure to check configurations.properties file

For Local Run - Make sure to set the same to true

Prerequisites - Maven and JDK 11 should be installed

Steps :

1. Clone and Authenticate using

2Run using

To run the functional tests : (Make sure to take latest pull)

git checkout master

mvn clean install surefire:test -Dsurefire.suiteXmlFiles=<fileDirectoryWithName>.xml

To run the performance tests :

mvn clean test-compile gatling:test -Dgatling.simulationClass=perf.gatling.tests.<directoryOfProject>.<simulationClass>

