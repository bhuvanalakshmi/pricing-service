package com.bk.rules;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.Customer;

@Rule(name = "loyal_US_customer", description = "If customer is from US and his sign up date is more than 5 years, price will be 2 dollars less than new price or current price whichever is greater" )
public class LoyalUSCustomerRule {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoyalUSCustomerRule.class);

	@Condition
	public boolean isLoyalUSCustomer(@Fact("customer") Customer customer) {

		long diffInseconds =  ((System.currentTimeMillis()/1000) - customer.getSignUpTimestamp());
		long numOfdaysSubscribed = diffInseconds/(3600*24);
		long numofYrs =  numOfdaysSubscribed/365;

		LOGGER.debug("number of years subscribed -->"+numofYrs);
		return numofYrs >= 5 && "US".equalsIgnoreCase(customer.getCountry().getCode());
	}

	@Action
	public void updatePrice(@Fact("customer") Customer customer, @Fact("price") float newPrice) {
		float priceToBeUpdated = (newPrice-2>customer.getPrice())?newPrice-2:customer.getPrice();
		LOGGER.debug("Price Will be updated to "+priceToBeUpdated);
		customer.setPrice(priceToBeUpdated);
	}

}
