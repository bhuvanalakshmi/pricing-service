package com.bk.verticle;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.Country;
import com.bk.model.Customer;
import com.bk.model.ObjectType;
import com.bk.model.ServicePlanInfo;
import com.bk.model.ServicePlanType;
import com.bk.service.dao.CountryMgmt;
import com.bk.service.dao.CustomerMgmt;
import com.bk.service.dao.ServicePlanPriceMgmt;
import com.bk.util.Utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class PricingService extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PricingService.class);

	CountryMgmt countryMgmt;
	CustomerMgmt customerMgmt;
	ServicePlanPriceMgmt servicePlanPriceMgmt;

	public static void main(String[] args) {

		ClusterManager clusterManager = Utils.getIgniteClusterManagerConfig();
		Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(clusterManager), ar -> {
			if (ar.failed()) {
				LOGGER.error("Cannot create vert.x instance : " + ar.cause());
			} else {
				Vertx vertx = ar.result();
				vertx.deployVerticle(PricingService.class.getName());
				LOGGER.debug("Deployed Pricing service verticle  ");
			}
		});
	}

	


	@Override
	public void start() {

		countryMgmt = new CountryMgmt();
		customerMgmt = new CustomerMgmt();
		servicePlanPriceMgmt = new ServicePlanPriceMgmt();

		Router router = Router.router(vertx);

		router.route().handler(BodyHandler.create());

		// Country APIs
		addCountryAPIs(router);
		
		// Customer APIs
		addCustomerAPIs(router);
		
		//Service Plan Price APIs 
		addServicePlanAPIs(router);



		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

	private void addServicePlanAPIs(Router router) {
		router.get("/servicePlans/:planType/country/:countryId").handler(routingContext -> {
			Integer countryId = Integer.parseInt(routingContext.request().getParam("countryId"));
			ServicePlanType planType = ServicePlanType.fromValue(routingContext.request().getParam("planType"));
			HttpServerResponse response = routingContext.response();
			if(planType == null){
				sendError(400, response, ObjectType.ServicePlanPrice.name(), null);
			}
			else {
				ServicePlanInfo planInfo;
				try {
					planInfo = servicePlanPriceMgmt.getServicePlanInfo(countryId, planType);
					sendResponse(countryId, response, planInfo, ObjectType.ServicePlanPrice);
				} catch (SQLException e) {
					e.printStackTrace();
					sendError(500, response, ObjectType.ServicePlanPrice.name(), null);
				}
				
			}
		});


		router.put("/servicePlans").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			JsonObject servicePlan = routingContext.getBodyAsJson();
			ServicePlanInfo planInfo;
			try {
				planInfo = servicePlanPriceMgmt.createOrUpdateServicePlanInfo(servicePlan);
				sendResponse(planInfo.getId(), response, planInfo, ObjectType.ServicePlanPrice);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.ServicePlanPrice.name(), null);
			}
			
			
		});


		router.get("/servicePlans").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			List<ServicePlanInfo> plans;
			try {
				plans = servicePlanPriceMgmt.getServicePlans();
				sendResponse(response, plans);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.ServicePlanPrice.name(), null);
			}
			
		});
	}

	private void addCountryAPIs(Router router) {
		router.get("/countries/:id").handler(routingContext -> {
			Integer countryId = Integer.parseInt(routingContext.request().getParam("id"));
			HttpServerResponse response = routingContext.response();
			Country country;
			try {
				country = countryMgmt.getCountry(countryId);
				sendResponse(countryId, response, country, ObjectType.Country);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.Country.name(), null);
			}
		});

		router.post("/countries").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			JsonObject country = routingContext.getBodyAsJson();

			if (country == null) {
				sendError(400, response, ObjectType.Country.name(), null);
			} else {
				Country c;
				try {
					c = countryMgmt.createCountry(country);

					sendResponse(c.getId(), response, c, ObjectType.Country);
				} catch (SQLException e) {
					e.printStackTrace();
					sendError(500, response, ObjectType.Country.name(), null);
				}
			}
		});
		router.put("/countries/:id").handler(routingContext -> {
			Integer countryId = Integer.parseInt(routingContext.request().getParam("id"));
			HttpServerResponse response = routingContext.response();
			JsonObject country = routingContext.getBodyAsJson();
			Country c;
			try {
				c = countryMgmt.updateCountry(countryId, country);
				sendResponse(c.getId(), response, c, ObjectType.Country);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.Country.name(), null);
			}
		});

		router.delete("/countries/:id").handler(routingContext -> {
			Integer countryId = Integer.parseInt(routingContext.request().getParam("id"));
			HttpServerResponse response = routingContext.response();
			Country c = null;
			try {
				c = countryMgmt.deleteCountry(countryId);
				sendResponse(c.getId(), response, c, ObjectType.Country);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.Country.name(), null);
			}
		});

		router.get("/countries").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			List<Country> countries;
			try {
				countries = countryMgmt.getCountries();
				sendResponse(response, countries);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sendError(500, response, ObjectType.Country.name(), null);
			}

		});
	}


	private void addCustomerAPIs(Router router) {
		router.get("/customers/:id").handler(routingContext -> {
			Integer customerId = Integer.parseInt(routingContext.request().getParam("id"));
			HttpServerResponse response = routingContext.response();
			Customer customer;
			try {
				customer = customerMgmt.getCustomer(customerId);
				sendResponse(customerId, response, customer, ObjectType.Customer);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.Customer.name(), null);
			}
		});

		router.post("/customers").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			JsonObject customer = routingContext.getBodyAsJson();

			if (customer == null) {
				sendError(400, response, ObjectType.Customer.name(), null);
			} else {
				Customer c;
				try {
					c = customerMgmt.createCustomer(customer);

					sendResponse(c.getId(), response, c, ObjectType.Customer);
				} catch (SQLException e) {
					e.printStackTrace();
					sendError(500, response, ObjectType.Customer.name(), null);
				}
			}
		});
		router.put("/customers/:id").handler(routingContext -> {
			Integer customerId = Integer.parseInt(routingContext.request().getParam("id"));
			HttpServerResponse response = routingContext.response();
			JsonObject customer = routingContext.getBodyAsJson();
			Customer c;
			try {
				c = customerMgmt.updateCustomer(customerId, customer);
				sendResponse(c.getId(), response, c, ObjectType.Customer);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.Customer.name(), null);
			}
		});

		router.delete("/customers/:id").handler(routingContext -> {
			Integer customerId = Integer.parseInt(routingContext.request().getParam("id"));
			HttpServerResponse response = routingContext.response();
			Customer c = null;
			try {
				c = customerMgmt.deleteCustomer(customerId);
				sendResponse(c.getId(), response, c, ObjectType.Customer);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.Customer.name(), null);
			}
		});

		router.get("/customers").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			List<Customer> customers;
			try {
				customers = customerMgmt.getCustomers();
				sendResponse(response, customers);
			} catch (SQLException e) {
				e.printStackTrace();
				sendError(500, response, ObjectType.Customer.name(), null);
			}

		});
	}


	private void sendResponse(HttpServerResponse response, List objs) {
		response.putHeader("content-type", "application/json").end(new JsonArray(objs).encodePrettily());

	}

	private void sendResponse(Integer id, HttpServerResponse response, Object obj, ObjectType type) {
		if (obj == null) {
			sendError(404, response,type.name(), id);
		} else {
			response.putHeader("content-type", "application/json").end(JsonObject.mapFrom(obj).encodePrettily());
		}
	}

	private void sendError(int statusCode, HttpServerResponse response, String type, Integer id) {
		if(statusCode == 404)
			response.setStatusMessage(type + " with id "+ id + "is not found");
		else if(statusCode == 400)
			response.setStatusMessage(type + " request is invalid");
		else if(statusCode == 500)
			response.setStatusMessage("Internal error occured");
		response.setStatusCode(statusCode).end();
	}







}
