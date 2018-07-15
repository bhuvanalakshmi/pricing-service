package com.bk.verticle;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.ServicePlanInfo;
import com.bk.model.ServicePlanType;
import com.bk.service.dao.CountryMgmt;
import com.bk.service.dao.CustomerMgmt;
import com.bk.service.dao.ServicePlanPriceMgmt;
import com.bk.util.Utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class PriceUpdater extends AbstractVerticle{

	private static final Logger LOGGER = LoggerFactory.getLogger(PriceUpdater.class);

	ServicePlanPriceMgmt planPriceMgmt = new ServicePlanPriceMgmt();
	CustomerMgmt customerMgmt = new CustomerMgmt();
	CountryMgmt countryMgmt = new CountryMgmt();


	@Override
	public void start() throws Exception {

		int delayTimeInSecs = Integer.parseInt(Utils.getProperties().getProperty("priceupdater.job.time", "30"));
		int customersChunkSize = Integer.parseInt(Utils.getProperties().getProperty("updater.workload.size"));

		vertx.setPeriodic(delayTimeInSecs*1000, x -> {
			ServicePlanType planType = ServicePlanType.fromValue(config().getString("planType"));

			LOGGER.debug("Inside start "+ planType);

			// get service plans that needs 
			try {
				// get service plans for a specific plantype for which there a still few customers for whom the price hasnt been updated
				List<ServicePlanInfo> plans = planPriceMgmt.getServicePlans(true, planType);
				if(plans== null || plans.isEmpty()) return;
				for(ServicePlanInfo plan : plans){
					//Get count of customers that belong to a service plan and country which doesn't match the service's plan current price. Billing date for these customers have to be current or in the past 

					int customersCount = customerMgmt.getCustomersCountForPriceUpdate(plan.getCountry().getId(), planType, plan.getPrice(), true);

					LOGGER.debug("Customers count "+ customersCount);
					LOGGER.debug("Customers xx "+ customersCount);

					//TODO prepare config for each verticle and deploy the verticle
					int temp = 0;
					while(temp < customersCount){
						JsonObject config = new JsonObject();
						config.put("planType",plan.getPlanType().name());
						config.put("country_id",plan.getCountry().getId());
						config.put("startingOffset", temp);
						config.put("num_of_records", customersChunkSize);

						LOGGER.debug("config -- "+config);
						vertx.deployVerticle(CustomerPriceUpdater.class.getName(), new DeploymentOptions().setWorker(true).setConfig(config));
						LOGGER.info("Deployed CustomerPriceUpdater verticle with config : "+ config);

						temp = temp + customersChunkSize + 1;
					}



				} // end of each plan

			} catch (SQLException e) {
				e.printStackTrace();
			}

		});
	}

}
