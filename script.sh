#!/bin/sh


sudo /opt/si2/virtualip.sh eth0

echo "export J2EE_HOME=/usr/local/glassfish-4.1.1/glassfish" >> .bashrc
echo "export PATH=/usr/local/glassfish-4.1.1/glassfish/bin:$PATH" >> .bashrc



# en la VM 
# asadmin start-domain domain1
