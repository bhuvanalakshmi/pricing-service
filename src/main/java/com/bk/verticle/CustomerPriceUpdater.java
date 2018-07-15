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
import com.bk.service.dao.CustomerMgmt;
import com.bk.service.dao.ServicePlanPriceMgmt;
import com.bk.util.Utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class CustomerPriceUpdater extends AbstractVerticle{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPriceUpdater.class);
	
	CustomerMgmt customerMgmt = new CustomerMgmt();
	ServicePlanPriceMgmt servicePlanMgmt = new ServicePlanPriceMgmt();
	
	@Override
	public void start() throws Exception {
		LOGGER.debug("start method of CustomerPriceUpdater");
		
		ServicePlanType planType =  ServicePlanType.fromValue(config().getString("planType"));
		int countryId =  config().getInteger("country_id");
		
		Integer startingOffset = config().getInteger("offset");
		Integer num_of_records = config().getInteger("num_of_records");
		

		ServicePlanInfo plan = servicePlanMgmt.getServicePlanInfo(countryId, planType);
		
		//Get customers that belong to a service plan and country which doesn't match the service's plan current price. Billing date for these customers have to be current or in the past 
		List<Customer> customers = customerMgmt.getCustomersForPriceUpdate(plan.getCountry().getId(), plan.getPlanType(), plan.getPrice(), true, startingOffset, num_of_records);
		if(customers!=null && !customers.isEmpty()){
			//For each customer, check if price needs to be updated or not based on the service plan rules 
			checkAndUpdateCustomers(plan, customers);
		}
		//check if there are any pending customers
		customers = customerMgmt.getCustomersForPriceUpdate(plan.getCountry().getId(), plan.getPlanType(), plan.getPrice(), false, null, null);
		// if all the customers are updated then update service plan's priceUpdateCompleted flag
		if(customers == null || customers.isEmpty()){
			resetServicePlanFlag(plan);
		}
	}
	
	/**
	 * For each customer, check if price needs to be updated or not based on the service plan rules 
	 * @param plan
	 * @param customers
	 * @throws SQLException
	 */
	private void checkAndUpdateCustomers(ServicePlanInfo plan, List<Customer> customers) throws SQLException {
		Rules rules = new Rules();
		// get rule names from the service plan and register the rules
		Utils.registerRules(rules, plan.getRules());

		RulesEngine rulesEngine = new DefaultRulesEngine();

		for(Customer c : customers){
			float oldPrice = c.getPrice();
			Facts facts = new Facts();
			facts.put("customer", c);
			facts.put("price", plan.getPrice());

			// fire rules on known facts
			if(plan.getRules() != null)
				rulesEngine.fire(rules, facts);
			else
				c.setPrice(plan.getPrice());

			if(oldPrice!=c.getPrice()){
				JsonObject json = JsonObject.mapFrom(c);
				json.put("country_id", c.getCountry().getId());
				customerMgmt.updateCustomer(c.getId(), json);
			}
		}
	}

	/**
	 * Update the priceCompleted flag for the Service Plan
	 * @param plan - ServicePlan
	 * @throws SQLException
	 */
	private void resetServicePlanFlag(ServicePlanInfo plan) throws SQLException {
		JsonObject json = JsonObject.mapFrom(plan);
		json.put("country_id", plan.getCountry().getId());
		servicePlanMgmt.createOrUpdateServicePlanInfo(json, true);
	}

}
