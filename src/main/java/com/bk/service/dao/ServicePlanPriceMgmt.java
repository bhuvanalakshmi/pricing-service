package com.bk.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.Queries;
import com.bk.model.ServicePlanInfo;
import com.bk.model.ServicePlanType;
import com.bk.util.Utils;

import io.vertx.core.json.JsonObject;

public class ServicePlanPriceMgmt {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicePlanPriceMgmt.class);
	
	CountryMgmt countryMgmt = new CountryMgmt();
	
	public ServicePlanInfo getServicePlanInfo(int countryId, ServicePlanType planType) throws SQLException {
		return getServicePlanInfo(countryId, planType, false);
	}

	public ServicePlanInfo getServicePlanInfo(int countryId, ServicePlanType planType, boolean fetchforPriceUpdate) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		ServicePlanInfo planInfo = null;
		try{
			con = Utils.getConnection();
			String sql = Queries.GET_PLAN_INFO; 
			if(fetchforPriceUpdate)
				sql = sql + " and price_update_completed is TRUE";
			statement = con.prepareStatement(Queries.GET_PLAN_INFO);
			statement.setInt(1, countryId);
			statement.setString(2, planType.name());
			ResultSet rs = statement.executeQuery();
			if(rs == null ) return null;
			while (rs.next()) {
				//plan_type, country_id, price, id
				planInfo = new ServicePlanInfo();
				planInfo.setPlanType(ServicePlanType.fromValue(rs.getString("plan_type")));
				planInfo.setId(rs.getInt("id"));
				planInfo.setPrice(rs.getFloat("price"));
				planInfo.setRules(rs.getString("rules"));
				planInfo.setCountry(countryMgmt.getCountry(countryId));
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return planInfo;
	}
	
	
	public ServicePlanInfo createOrUpdateServicePlanInfo(JsonObject planInfoJson) throws SQLException {
		return createOrUpdateServicePlanInfo(planInfoJson, false);
	}


	public ServicePlanInfo createOrUpdateServicePlanInfo(JsonObject planInfoJson, boolean priceUpdated) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		ServicePlanInfo planInfo = null;
		try{
			int countryId = planInfoJson.getInteger("country_id");
			ServicePlanType planType = ServicePlanType.fromValue(planInfoJson.getString("planType"));
			planInfo =  getServicePlanInfo(countryId, planType);
			con = Utils.getConnection();
			if(planInfo == null){
				statement = con.prepareStatement(Queries.CREATE_PLAN);
				statement.setString(1, planInfoJson.getString("planType"));
				statement.setInt(2, countryId);
				statement.setFloat(3, planInfoJson.getFloat("price"));
				statement.setBoolean(4, priceUpdated);
				statement.setString(5,  planInfoJson.getString("rules"));
				
				
			}else {
				statement = con.prepareStatement(Queries.UPDATE_PLAN);
				statement.setFloat(1, planInfoJson.getFloat("price"));
				statement.setBoolean(2, priceUpdated);
				statement.setString(3,  planInfoJson.getString("rules"));
				statement.setString(4, planInfoJson.getString("planType"));
				statement.setInt(5, countryId);
			}
			
			statement.executeUpdate();
			planInfo =  getServicePlanInfo(countryId, planType);

		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return planInfo;
	}
	
	public List<ServicePlanInfo> getServicePlans() throws SQLException {
		return getServicePlans(false, null);
	}
	

	public List<ServicePlanInfo> getServicePlans(boolean fetchforPriceUpdate, ServicePlanType planType) throws SQLException {
		List<ServicePlanInfo> plans =new ArrayList<ServicePlanInfo>();
		Connection con = null;
		PreparedStatement statement = null;
		ServicePlanInfo planInfo = null;
		try{
			con = Utils.getConnection();
			String sql = Queries.GET_PLANS; 
			if(fetchforPriceUpdate)
				sql = sql + " where price_update_completed is not TRUE";
			if(planType!=null)
				sql = sql + " AND plan_type = '"+planType.name()+"'";
			
			statement = con.prepareStatement(sql);
			LOGGER.debug("statement -- "+statement);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return null;
			while (rs.next()) {
				planInfo = new ServicePlanInfo();
				planInfo.setPlanType(ServicePlanType.fromValue(rs.getString("plan_type")));
				planInfo.setId(rs.getInt("id"));
				planInfo.setPrice(rs.getFloat("price"));
				planInfo.setCountry(countryMgmt.getCountry(rs.getInt("country_id")));
				planInfo.setRules(rs.getString("rules"));
				plans.add(planInfo);
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return plans;

	}
}
