package com.bk.verticle;

import java.sql.SQLException;
import java.util.List;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.Customer;
import com.bk.model.ServicePlanInfo;
import com.bk.model.ServicePlanType;
import com.bk.rules.NewCustomerRule;
import com.bk.service.dao.CountryMgmt;
import com.bk.service.dao.CustomerMgmt;
import com.bk.service.dao.ServicePlanPriceMgmt;
import com.bk.util.Utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;

public class CustomerPriceUpdater extends AbstractVerticle{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPriceUpdater.class);
	
	ServicePlanPriceMgmt planPriceMgmt = new ServicePlanPriceMgmt();
	CustomerMgmt customerMgmt = new CustomerMgmt();
	CountryMgmt countryMgmt = new CountryMgmt();

	public static void main(String[] args) {
		ClusterManager clusterManager = Utils.getIgniteClusterManagerConfig();
		Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(clusterManager), ar -> {
			if (ar.failed()) {
				System.err.println("Cannot create vert.x instance : " + ar.cause());
			} else {
				Vertx vertx = ar.result();
				for(ServicePlanType type : ServicePlanType.values()){
					JsonObject config = new JsonObject();
					config.put("planType",type.name());
					LOGGER.debug("config -- "+config);
					vertx.deployVerticle(CustomerPriceUpdater.class.getName(), new DeploymentOptions().setConfig(config));
				}
				
				//vertx.deployVerticle(CustomerPriceUpdater.class.getName());
			}
		});
	}

	@Override
	public void start() throws Exception {
		
		vertx.setPeriodic(30*1000, x -> {
			ServicePlanType planType = ServicePlanType.fromValue(config().getString("planType"));
			
			LOGGER.debug("Inside start "+ planType);
			
			// get service plans that needs 
			try {
				List<ServicePlanInfo> plans = planPriceMgmt.getServicePlans(true, planType);
				if(plans== null || plans.isEmpty()) return;
				for(ServicePlanInfo plan : plans){
					List<Customer> customers = customerMgmt.getCustomersForPriceUpdate(plan.getCountry().getId(), plan.getPlanType(), plan.getPrice(), true);
					
					 // define rules
			        Rules rules = new Rules();
			        Utils.registerRules(rules, plan.getRules());

			        // fire rules on known facts
			        RulesEngine rulesEngine = new DefaultRulesEngine();
			        
					
					for(Customer c : customers){
						
				        Facts facts = new Facts();
				        facts.put("customer", c);
				        facts.put("price", plan.getPrice());
				        
				        rulesEngine.fire(rules, facts);
				       
						
				        JsonObject json = JsonObject.mapFrom(c);
						json.put("country_id", c.getCountry().getId());
						
						customerMgmt.updateCustomer(c.getId(), json);
					}
					customers = customerMgmt.getCustomersForPriceUpdate(plan.getCountry().getId(), plan.getPlanType(), plan.getPrice(), false);
					// if all the customers are updated then update service plan's priceUpdateCompleted flag
					if(customers == null || customers.isEmpty()){
						resetServicePlanFlag(plan);
					}
						
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}

		});
	}

	private void resetServicePlanFlag(ServicePlanInfo plan) throws SQLException {
		JsonObject json = JsonObject.mapFrom(plan);
		json.put("country_id", plan.getCountry().getId());
		planPriceMgmt.createOrUpdateServicePlanInfo(json, true);
	}
}
