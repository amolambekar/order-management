# order-management

Travis CI status: [![Build Status](https://travis-ci.com/amolambekar/order-management.svg?branch=master)](https://travis-ci.com/amolambekar/order-management)

order-management  system manages order books. 
An order book is an electronic list of orders for a specific financial instrument. 
A financial instrument is identified by unique instrument id. 

The system allows user to:
1. open and to close an order book for an instrument
2. obtain statistics about the amount of orders in each book, demand, the biggest order and the smallest order, the earliest order entry, the last order entry, limit break down (a table with limit prices and demand per limit price). Demand = accumulated order quantity.
3. obtain statistics about the amount of valid/invalid orders in each book, valid/invalid demand, the biggest order and the smallest order, the earliest order entry, the last order entry, limit break down (a table with limit prices and demand per limit price), accumulated execution quantity and execution price.
4. get, for the given order id, validity status, execution quantity and order’s price, and execution price.

# Implementation Details

The aim of this application is to add orders and executions for those orders to orderBook. 
Synchronization to apply one execution at one time is acheived by applying JVM level lock using ReentrantLock APi.
Reentrant lock perform better when there is high thread contention and fair sharing of resources is required.
OrderBookService uses a ConcurrentHashMap to store and retrieve lock object based on OrderBook ID.
The locks are added to concurrentHashMap while adding order-book.
Thus each order-book has one lock associated with it and the executions can be added by obtaining the lock associated with orderbook's orderBookId.

An alternate approach is to apply a JPA pessimistic Write lock on OrderBook while retrieving order-book based on orderbookId.
PESSIMISTIC_WRITE lock guarantees that besides dirty and non-repeatable reads are impossible you can update data without obtaining additional locks(and possible deadlocks while waiting for exclusive lock).

The application uses following technology stack :
Java 1.8, springBoot, Spring Data, SPring REST, SPring Actuator, Swagger UI

Build Information:

run mvn clean install.

application can be run from command line using command

java -jar target/ordermanagement-0.0.1-SNAPSHOT.jar




#Junit Covergae

![Alt text](/src/test/resources/junit_coverage.gif?raw=true "Junit Coverage")


# Application Metrix

Basic application metrix can be  accessed using Actuator URLs

http://localhost:8080/health

http://localhost:8080/info

http://localhost:8080/metrics

http://localhost:8080/trace


Spring Boot Actuator provides dependency management and auto-configuration for Micrometer, an application metrics facade that supports numerous monitoring systems, including:

AppOptics
Atlas
Datadog
Dynatrace
Elastic
JMX

# Future Scalability 

The application exposes stateless APIs for creating orders,orderbook and adding exeuctions.
The application uses Java Lock API to synchronize access to Orderbook. These locks can be added to Hazelcast or Reddi cache and be accessed in code by getting it from one fo such disributed cache to make sure that synchronization works when multiple instances of the application are running.

The application is solely focusing on Orderbook managemnet and can be deployed as micro-service and is already following some of the 12 factor app principles such as port binding and logging.concurrency and codebase.
Further enhancement cane be done by creating infrastructure services such as API gateway, service discovery and config server and configuring the order-management application to use these services.
The application can be configured to create docker image to make is easily deployable across different platforms.

#TODO
1. use message properties instead of hardcoded messages.
2. Use spring validations for input parameters
3. current junit tests cater to basic application requiremment, additional test can be written to cover more scenarios and exceptional flows










