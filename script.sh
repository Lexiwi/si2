#!/bin/sh

export J2EE_HOME=/usr/local/glassfish-4.1.1/glassfish
sudo /opt/si2/virtualip.sh eth0

export PATH=/usr/local/glassfish-4.1.1/glassfish/bin:$PATH



# en la VM 
# asadmin start-domain domain1
