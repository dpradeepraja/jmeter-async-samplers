# jmeter-async-samplers
Credit: pleutres/jmeter-asynchronous-http for the core idea and nanohttp impl

## Quick description
Jmeter plugin to validate working asynchronous HTTP APIs utilizing JSON & XML
1. Multiple async APIs can be triggered and waited upon
1. Configurable JSON Path based assertions on the callback received to determine success/failure
1. Configurable individual Timeout to validate spec of API

The use case is the following : 

A (Jmeter + jmeter-asynchronous-http plugin)   
B (the HTTP server to be tested)  

## Use case

1) A : NotificationReceiverCreation sampler starts and notify NanoHTTP that it waits a response
2) A : send a request to B (with the sampler you want)
3) A : NotificationReceiverWait sampler starts and wait
4) B : send a answer to B (on NanoHTTP)
5) A : NanoHTTP notify JMeter NotificationReceiverWait sampler that response is arrived
6) A : NotificationReceiverWait Sample is closed, the time is recorded 

## Pre-requisites
- Build the project and copy httpAsyncSamplers-1.0.jar into lib/ext folder of JMeter 5.x
- Source dependencies from mvn and copy nanohttpd-2.3.0.jar, jdom2-2.0.4.jar into lib folder of JMeter 5.x

## Usage
- NotificationReceiverCreation
![ReceiverCreation](/images/ReceiverCreation.PNG)
FUNCTIONAL_ID: Unique String value, should be different for each request. Examples "Id1","1"
CALLBACK_URI: relative path of callback url which service under test shall call. Examples "/Notify","/WaitForCompletion"
CALLBACK_METHOD: HTTP method used. Examples "POST" "GET"
DATA_PATH: Optional JSON path Parameter, when set will be used to extract value from incoming message body. Examples "$.name"
EXPECTED_VALUE: Optional data validation that shall be compared with extracted value from incoming message body. Examples "John"

- NotificationReceiverWait
![ReceiverWait](/images/ReceiverWait.PNG)
FUNCTIONAL_ID: Unique String value, should match with corresponding Creation request. Examples "Id1","1"
TIME_OUT_IN_SECS: # of seconds, the sampler shall wait for the callback. Examples "60"


### Results
- NotificationReceiverWait results shall display if the receiver was setup successfully
![ReceiverCreationResult](/images/ReceiverCreationResult.PNG)

- NotificationReceiverWait results shows body from callback received.
![ReceiverWaitResult](/images/ReceiverWaitResult.PNG)
