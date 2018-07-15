package com.bk.service.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.Customer;
import com.bk.model.Queries;
import com.bk.model.ServicePlanType;
import com.bk.util.Utils;

import io.vertx.core.json.JsonObject;

public class CustomerMgmt {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerMgmt.class);

	CountryMgmt countryMgmt = new CountryMgmt();
	ServicePlanPriceMgmt servicePlanMgmt = new ServicePlanPriceMgmt();

	//name, email, plan_type, price, country_id, signup_date, next_billing_date, id 
	public Customer getCustomerByEmail( String email) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		Customer c = new Customer();
		try{

			con = Utils.getConnection();
			statement = con.prepareStatement(Queries.GET_CUSTOMER_BY_EMAIL);
			statement.setString(1, email);
			LOGGER.debug("statement --"+statement);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return null;
			while (rs.next()) {

				c.setName(rs.getString("name"));
				c.setEmail(rs.getString("email"));
				c.setPlanType(ServicePlanType.fromValue(rs.getString("plan_type")));
				c.setId(rs.getInt("id"));
				c.setPrice(rs.getFloat("price"));
				c.setCountry(countryMgmt.getCountry(rs.getInt("country_id")));
				c.setSignUpTimestamp(rs.getBigDecimal("signup_timestamp").longValue());
				c.setNextBillingTimestamp(rs.getBigDecimal("next_billing_timestamp").longValue());

			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}

	public Customer getCustomer( int customerId) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		Customer c = new Customer();
		try{
			con = Utils.getConnection();
			statement = con.prepareStatement(Queries.GET_CUSTOMER);
			statement.setInt(1, customerId);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return null;
			while (rs.next()) {

				c.setName(rs.getString("name"));
				c.setEmail(rs.getString("email"));
				c.setPlanType(ServicePlanType.fromValue(rs.getString("plan_type")));
				c.setId(rs.getInt("id"));
				c.setPrice(rs.getFloat("price"));
				c.setCountry(countryMgmt.getCountry(rs.getInt("country_id")));
				c.setSignUpTimestamp(rs.getBigDecimal("signup_timestamp").longValue());
				c.setNextBillingTimestamp(rs.getBigDecimal("next_billing_timestamp").longValue());
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}

	public Customer createCustomer( JsonObject customer) throws SQLException {

		Connection con = null;
		PreparedStatement statement = null;
		Customer c = null;
		try{
			con = Utils.getConnection();
			
			int countryId = customer.getInteger("country_id");
			ServicePlanType planType = ServicePlanType.fromValue(customer.getString("planType"));
			float price = servicePlanMgmt.getServicePlanInfo(countryId, planType).getPrice();
			
			//name, email, plan_type, price, country_id, signup_date, next_billing_date, id 
			statement = con.prepareStatement(Queries.CREATE_CUSTOMER);
			statement.setString(1, customer.getString("name"));
			statement.setString(2, customer.getString("email"));
			statement.setString(3, planType.name());
			statement.setFloat(4, price);
			statement.setInt(5, countryId);
			statement.setBigDecimal(6, new BigDecimal(customer.getLong("signUpTimestamp")));
			//Add 30 days
			statement.setBigDecimal(7, getNextBillingDate(customer));

			statement.executeUpdate();

			c = getCustomerByEmail(customer.getString("email"));

		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}


	public Customer updateCustomer( int customerId, JsonObject customer) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		Customer c = null;
		try{
			con = Utils.getConnection();
			
			int countryId = customer.getInteger("country_id");
			ServicePlanType planType = ServicePlanType.fromValue(customer.getString("planType"));
			float price = servicePlanMgmt.getServicePlanInfo(countryId, planType).getPrice();
			
			statement = con.prepareStatement(Queries.UPDATE_CUSTOMER);
			statement.setString(1, customer.getString("name"));
			statement.setString(2, customer.getString("email"));
			statement.setString(3, planType.name());
			statement.setFloat(4, price);
			statement.setInt(5, countryId);
			statement.setBigDecimal(6, new BigDecimal(customer.getLong("signUpTimestamp")));
			statement.setBigDecimal(7, getNextBillingDate(customer));
			statement.setInt(8, customerId);
			LOGGER.debug("Statement - "+statement);
			statement.executeUpdate();

			c = getCustomer(customerId);

		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}

	private BigDecimal getNextBillingDate(JsonObject customer) {
		//Add 30 days to sign up
		return new BigDecimal(customer.getLong("signUpTimestamp")+ (30 * 24 * 60 * 60));
	}

	public Customer deleteCustomer( int customerId) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		Customer c = null;
		try{
			con = Utils.getConnection();
			c = getCustomer(customerId);

			statement = con.prepareStatement(Queries.DELETE_CUSTOMER);
			statement.setInt(1, customerId);

			statement.executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}

	public List<Customer> getCustomers() throws SQLException {
		List<Customer> customers =new ArrayList<Customer>();
		Connection con = null;
		PreparedStatement statement = null;
		Customer c = null;
		try{
			con = Utils.getConnection();
			statement = con.prepareStatement(Queries.GET_CUSTOMERS);
			LOGGER.debug("statement -- "+statement);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return null;
			while (rs.next()) {
				c = new Customer();
				c.setName(rs.getString("name"));
				c.setEmail(rs.getString("email"));
				c.setPlanType(ServicePlanType.fromValue(rs.getString("plan_type")));
				c.setId(rs.getInt("id"));
				c.setPrice(rs.getFloat("price"));
				c.setCountry(countryMgmt.getCountry(rs.getInt("country_id")));
				c.setSignUpTimestamp(rs.getBigDecimal("signup_timestamp").longValue());
				c.setNextBillingTimestamp(rs.getBigDecimal("next_billing_timestamp").longValue());
				customers.add(c);
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return customers;

	}
	
	public int getCustomersCountForPriceUpdate(int countryId, ServicePlanType planType,  float price, boolean filterByTimestamp)throws SQLException {
		int count = 0;
		Connection con = null;
		PreparedStatement statement = null;
		try{
			con = Utils.getConnection();
			String sql = Queries.GET_COUNT_OF_CUSTOMERS_BY_COUNTRY_PLAN_PRICE;
			if(filterByTimestamp)
				sql = sql + " AND next_billing_timestamp <= " + new Timestamp(System.currentTimeMillis()/1000).getTime() ; 
			
			statement = con.prepareStatement(sql);
			statement.setInt(1, countryId);
			statement.setString(2, planType.name());
			statement.setFloat(3, price);
			LOGGER.debug("statement "+statement);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return count;
			while (rs.next()) {
				count = rs.getInt("noofcustomers");
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return count;
	}


	public List<Customer> getCustomersForPriceUpdate(int countryId, ServicePlanType planType,  float price, boolean filterByTimestamp, Integer offset, Integer numOfRecords) throws SQLException {
		List<Customer> customers =new ArrayList<Customer>();
		Connection con = null;
		PreparedStatement statement = null;
		Customer c = null;
		StringBuilder sql = new StringBuilder();
		try{
			con = Utils.getConnection();
			sql.append(Queries.GET_CUSTOMERS_BY_COUNTRY_PLAN_PRICE);
			if(filterByTimestamp)
				sql.append(" AND next_billing_timestamp <= ").append( new Timestamp(System.currentTimeMillis()/1000).getTime()) ; 
			
			if(offset != null && numOfRecords != null)
				sql.append(" LIMIT ").append(offset.intValue()).append(", ").append(numOfRecords);
			
			statement = con.prepareStatement(sql.toString());
			statement.setInt(1, countryId);
			statement.setString(2, planType.name());
			statement.setFloat(3, price);
			LOGGER.debug("statement "+statement);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return null;
			while (rs.next()) {
				c = new Customer();
				c.setName(rs.getString("name"));
				c.setEmail(rs.getString("email"));
				c.setPlanType(ServicePlanType.fromValue(rs.getString("plan_type")));
				c.setId(rs.getInt("id"));
				c.setPrice(rs.getFloat("price"));
				c.setCountry(countryMgmt.getCountry(rs.getInt("country_id")));
				c.setSignUpTimestamp(rs.getBigDecimal("signup_timestamp").longValue());
				c.setNextBillingTimestamp(rs.getBigDecimal("next_billing_timestamp").longValue());
				customers.add(c);
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return customers;

	}




}
