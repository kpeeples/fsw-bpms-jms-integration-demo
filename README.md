<<<<<<< HEAD
# BPM Integration Suite Demo

## Subject
This is part of the "BPM Integration" approach with JBoss BPMS and JBoss FSW.
It contains the services model and java code for end-to-end integration between
both software stacks.
The goal is to provide a high-performance service integration plattform for
business processes developed by either business users or developers.


## Architecture
![Product Architecture](http://code.o511.de/img/RH-BPMIS.svg)


## Current Status
Working wrapper for the tutorial implementation on  [jboss.org](https://docs.jboss.org/jbpm/v6.0/userguide/jBPMRemoteAPI.html#d0e12146) or [access.redhat.com](https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_BPM_Suite/6.0/html/Development_Guide/Example_JMS_Usage.html).

Example request for the switchyard-service (e.g. http://localhost:8080/processAPI/ProcessService):
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bpm="http://redhat.com/BPMIS">
   <soapenv:Header/>
   <soapenv:Body>
      <bpm:startProcess>
         <processContext>
            <deploymentId>customer:evaluation</deploymentId>
            <processId>customer.evaluation</processId>
            <user>
               <bpm:name>kai</bpm:name>
               <password>change12_me!</password>
            </user>
         </processContext>
      </bpm:startProcess>
   </soapenv:Body>
</soapenv:Envelope>
<!-- see ProcessService.wsdl in main/resources/ -->
```

Sadly the implementation is (for now) not working in a FSW deployment because of
classloading errors and classloading isolation problems.


## TODOs
+ Get the JMS implementation working (class loading errors)
+ Setup a docker container
+ Build BPMS process to show the FSW integration
  + based on
  [our insurance showcase](https://github.com/PatrickSteiner/BPM_FSW_Docker/tree/master/HEISE_BPM_Image)
+ Build the REST implementation
+ Build process monitoring frontend for maintanance users
  + Integrate as JON-Plugin
=======
fsw-bpms-jms-integration-demo
=============================
>>>>>>> 174576ebf17a95df5f60c37f7c13dc0c295b9c75
