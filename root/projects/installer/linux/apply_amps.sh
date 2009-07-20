#!/bin/sh
# -------
# Script for apply AMPs to installed WAR
# -------
export CATALINA_HOME=tomcat
echo "This script will apply all the AMPs in ./amps to the alfresco.war file in $CATALINA_HOME\webapps"
echo "Press control-c to stop this script . . ."
echo "Press any other key to continue . . ."
read RESP
java -jar bin/alfresco-mmt.jar install ./amps $CATALINA_HOME/webapps/alfresco.war -directory
java -jar bin/alfresco-mmt.jar list $CATALINA_HOME/webapps/alfresco.war
echo "About to clean out tomcat/webapps/alfresco directory and temporary files..."
echo "Press control-c to stop this script . . ."
echo "Press any other key to continue . . ."
read DUMMY
rm -rf $CATALINA_HOME/webapps/alfresco
sh ./bin/clean_tomcat.sh
