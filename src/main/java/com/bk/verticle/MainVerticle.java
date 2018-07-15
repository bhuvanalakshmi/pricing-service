package com.bk.verticle;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.ServicePlanType;
import com.bk.util.Utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;

public class MainVerticle extends AbstractVerticle{

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

	public static void main(String[] args) {

		if(args.length != 1){
			throw new IllegalArgumentException("Please provide vertical name to be started - rest-api or price-updater");
		}

		LOGGER.debug("arguments -- "+Arrays.asList(args));
		String verticleName = args[0];
		if(!isValidVerticleName( verticleName))
			throw new IllegalArgumentException("Invalid verticle name "+ verticleName);

		ClusterManager clusterManager = Utils.getIgniteClusterManagerConfig();
		Vertx.clusteredVertx(new VertxOptions().setClustered(true).setClusterManager(clusterManager), ar -> {
			if (ar.failed()) {
				LOGGER.error("Cannot create vert.x instance : " + ar.cause());
			} else {
				Vertx vertx = ar.result();
				if("rest-api".equalsIgnoreCase(verticleName)){
					vertx.deployVerticle(PricingService.class.getName());
					LOGGER.info("Deployed Pricing service verticle  ");
				}
				else if("price-updater".equalsIgnoreCase(verticleName)){
					for(ServicePlanType type : ServicePlanType.values()){
						JsonObject config = new JsonObject();
						config.put("planType",type.name());
						LOGGER.debug("config -- "+config);
						vertx.deployVerticle(PriceUpdater.class.getName(), new DeploymentOptions().setConfig(config));
						LOGGER.info("Deployed PriceUpdater verticle for plan type : "+type);
					}
					
				}


			}
		});

	}
	
	public static boolean isValidVerticleName(String verticleName){
		if("rest-api".equalsIgnoreCase(verticleName))
			return true;
		else if("price-updater".equalsIgnoreCase(verticleName))
			return true;
		else 
			return false;
	}

}
