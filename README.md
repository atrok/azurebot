# Azure bot

Azure Bot plugin for Genesys Bot gateway

1. Architecture

Bot Gateway is a part of Genesys Digital Message Server. It provides an API to develop pluged-in modules to interface thirdparty NLUs (Google, Azure, AWS)
This particular bot is developed to interface with the bot application deployed in Azure cloud via DirectLine channel.

It serves as a proxy between Genesys Widget chat client and Azure bot, and provides seemless transformation of native Microsoft Bot Framework Activities to Genesys Widget messages. 
Only text messages are supported as a part of this implementation.

##Implementation hints

Issues with Maven:
------------------
1. 501 HTTPS Required.
https://stackoverflow.com/questions/59763531/maven-dependencies-are-failing-with-a-501-error

upon installation of archetype update pom.xml with next configuration

	    <pluginRepositories>
        <pluginRepository>
            <id>central</id>
            <name>Central Repository</name>
            <url>https://repo.maven.apache.org/maven2</url>
            <layout>default</layout>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <releases>
                <updatePolicy>never</updatePolicy>
            </releases>
        </pluginRepository>
    </pluginRepositories>
    <repositories>
        <repository>
            <id>central</id>
            <name>Central Repository</name>
            <url>https://repo.maven.apache.org/maven2</url>
            <layout>default</layout>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
	

	
2. Received fatal alert: protocol_version
to enable TLS1.2:

set MAVEN_OPTS="-Dhttps.protocols=TLSv1.2" 


Development issues:
--------------------
1. Websocket requires secure connection 

to enable certificates needs to be added to the local truststore

https://www.microsoft.com/pki/mscorp/cps/default.htm

DigiCert Baltimore Root
d4de20d05e66fc53fe1a50882c78db2852cae474

Microsoft TLS CA 2
54:d9:d2:02:39:08:0c:32:31:6e:d9:ff:98:0a:48:98:8f:4a:df:2d

>keytool -import -trustcacerts -file "ms_tls_ca_2.cer"  -alias ms_tls_ca_2 -keystore $JAVA_HOME/lib/security/cacerts
>keytool -list -keystore cacerts


