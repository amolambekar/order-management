# order-management

order-management  system manages order books. 
An order book is an electronic list of orders for a specific financial instrument. 
A financial instrument is identified by unique instrument id. 

The system allows user to:
1. open and to close an order book for an instrument
2. obtain statistics about the amount of orders in each book, demand, the biggest order and the smallest order, the earliest order entry, the last order entry, limit break down (a table with limit prices and demand per limit price). Demand = accumulated order quantity.
3. obtain statistics about the amount of valid/invalid orders in each book, valid/invalid demand, the biggest order and the smallest order, the earliest order entry, the last order entry, limit break down (a table with limit prices and demand per limit price), accumulated execution quantity and execution price.
4. get, for the given order id, validity status, execution quantity and order’s price, and execution price.

The application uses following technology stack :
Java 1.8, springBoot, Spring Data, SPring REST, SPring Actuator, Swagger UI

Build Information:

run mvn clean install.

application can be run from command line using command

java -jar target/ordermanagement-0.0.1-SNAPSHOT.jar

swagger UI can be accessed using URL :http://localhost:8080/swagger-ui.html

# Future Scalability 

The application exposes stateless APIs for creating orders,orderbook and adding exeuctions.
The application uses Java Lock API to synchronize access to Orderbook. These locks can be added to Hazelcast or Reddi cache and be accessed in code by getting it from one fo such disributed cache to make sure that synchronization works when multiple instances of the application are running.

The application is solely focusing on Orderbook managemnet and can be deployed as micro-service and is already following some of the 12 factor app principles such as port binding and logging.concurrency and codebase.
Further enhancement cane be done by creating infrastructure services such as API gateway, service discovery and config server and configuring the order-management application to use these services.
The application can be configured to create docker image to make is easily deployable across different platforms.








