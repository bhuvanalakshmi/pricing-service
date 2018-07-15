package com.bk.model;

public interface Queries {
	
	String GET_COUNTRIES = "select name, code, currency, id from country";
	String GET_COUNTRY = "select name, code, currency, id  from country where id = ?";
	String GET_COUNTRY_BY_CODE = "select name, code, currency, id  from country where code = ?";
	String CREATE_COUNTRY = "insert into country(name, code, currency) values(?, ?, ?)";
	String UPDATE_COUNTRY = "update country set name = ?, code = ?, currency = ? where id = ?";
	String DELETE_COUNTRY = "delete from country where id = ?";
	
	String GET_CUSTOMERS = "select name, email, plan_type, price, country_id, signup_timestamp, next_billing_timestamp, id from customer order by id";
	String GET_CUSTOMERS_BY_COUNTRY_PLAN_PRICE = "select name, email, plan_type, price, country_id, signup_timestamp, next_billing_timestamp, id from customer where country_id = ? AND plan_type = ?  AND price <> ? order by id";
	
	String GET_COUNT_OF_CUSTOMERS_BY_COUNTRY_PLAN_PRICE = "select count(*) as noofcustomers from customer where country_id = ? AND plan_type = ?  AND price <> ? order by id";


	String GET_CUSTOMER = "select name, email, plan_type, price, country_id, signup_timestamp, next_billing_timestamp, id from customer where id = ?";
	String GET_CUSTOMER_BY_EMAIL = "select name, email, plan_type, price, country_id, signup_timestamp, next_billing_timestamp, id from customer where email = ?";
	String CREATE_CUSTOMER = "insert into customer(name, email, plan_type, price, country_id, signup_timestamp, next_billing_timestamp) values(?, ?, ?, ?, ?, ?, ?)";
	String UPDATE_CUSTOMER = "update customer set name = ?, email = ?, plan_type = ?, price = ?, country_id = ?, signup_timestamp = ?, next_billing_timestamp = ?  where id = ?";
	String DELETE_CUSTOMER= "delete from customer where id = ?";
	
	String GET_PLAN_INFO = "select plan_type, country_id, price, id, rules from service_plan_info where country_id = ? and plan_type = ?";
	String GET_PLANS = "select plan_type, country_id, price, id, rules from service_plan_info"; 
	String CREATE_PLAN = "insert into service_plan_info(plan_type, country_id, price, price_update_completed, rules) values(?, ?, ?, ?, ?)";
	String UPDATE_PLAN = "update service_plan_info set price = ?, price_update_completed = ?, rules = ? where plan_type = ? and country_id = ?";
	

}
