# pricing-service
This repository consists of two services which is deployed as verticles. 

 1. **Pricing-service** - This verticle built based on vertx-web exposes REST APIs for countries, customers and service_plans. 
 2.  **Price-updater** - This verticle runs periodically and updates the customers price to match the service plans price provided it matches the rules.

## Build and Deployment steps
### Database 

 1. Install mysql database and create a database. 
 2. Run the db/dbschema.sql to create tables - country, service_plan_info and customer. 
 3. If sample data is required, then execute the scripts in the following order 
	 -- db/country_201807141344.sql
	 -- db/service_plan_info_201807141348.sql
	 -- db/customer_201807141400.sql
 4. Update the **config.properties** in src/main/resources to reflect your database details 

 ### Build jar 
```
git clone repo
cd pricing-service
./gradlew build
```
### Execute pricing-service
```
java -jar build/libs/pricing-service-1.0-all.jar rest-api
```
you can access the apis by going to http://localhost:8080. If you like to change the port, pls change **app.port** in config.properties
### Execute price-updater
 ```
 java -jar build/libs/pricing-service-1.0-all.jar price-updater
 ```
 If you want to change how frequently you want this verticle to be executed, then you can update the **priceupdater.job.time** in config.properties. 
You can also update **updater.workload.size** in config.properties to increase/decrease the number of customers to be processed in a worked thread.  

Refer the design document(/design-doc/Pricing-service.docx) for more details on these services and APIs. 