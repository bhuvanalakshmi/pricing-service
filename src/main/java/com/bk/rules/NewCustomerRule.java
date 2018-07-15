package com.bk.rules;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.Customer;
import com.bk.verticle.PriceUpdater;

@Rule(name = "new_customer_rule", description = "If customer sign up date is less than six 6 months, price will not be increased" )
public class NewCustomerRule {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewCustomerRule.class);

	@Condition
	public boolean isNotNewCustomer(@Fact("customer") Customer customer) {

		long diffInseconds =  ((System.currentTimeMillis()/1000) - customer.getSignUpTimestamp());
		long diffInhrs = diffInseconds/(3600*24);
		long numOfdaysSubscribed = diffInhrs/24;

		LOGGER.debug("number of days subscribed -->"+numOfdaysSubscribed);
		return numOfdaysSubscribed >= 180;
	}

	@Action
	public void updatePrice(@Fact("customer") Customer customer, @Fact("price") float newPrice) {
		LOGGER.debug("Price Will be updated");
		customer.setPrice(newPrice);
	}

}
