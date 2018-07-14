package com.bk.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.model.Country;
import com.bk.model.Queries;
import com.bk.util.Utils;

import io.vertx.core.json.JsonObject;

public class CountryMgmt {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryMgmt.class);
	
	public Country getCountryByCode( String code) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		Country c = new Country();
		try{

			con = Utils.getConnection();
			statement = con.prepareStatement(Queries.GET_COUNTRY_BY_CODE);
			statement.setString(1, code);
			LOGGER.debug("statement - "+statement);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return null;
			while (rs.next()) {
				
				c.setName(rs.getString("name"));
				c.setCode(rs.getString("code"));
				c.setCurrency(rs.getString("currency"));
				c.setId(rs.getInt("id"));

			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}

	public Country getCountry( int countryId) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		Country c = new Country();
		try{
			con = Utils.getConnection();
			statement = con.prepareStatement(Queries.GET_COUNTRY);
			statement.setInt(1, countryId);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return null;
			while (rs.next()) {
				
				c.setName(rs.getString("name"));
				c.setCode(rs.getString("code"));
				c.setCurrency(rs.getString("currency"));
				c.setId(countryId);

			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}

	public Country createCountry( JsonObject country) throws SQLException {

		Connection con = null;
		PreparedStatement statement = null;
		Country c = null;
		try{
			con = Utils.getConnection();
			statement = con.prepareStatement(Queries.CREATE_COUNTRY);
			statement.setString(1, country.getString("name"));
			statement.setString(2, country.getString("code"));
			statement.setString(3, country.getString("currency"));
			
			statement.executeUpdate();
			
			c = getCountryByCode(country.getString("code"));

		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}


	public Country updateCountry( int countryId, JsonObject country) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		Country c = null;
		try{
			con = Utils.getConnection();
			statement = con.prepareStatement(Queries.UPDATE_COUNTRY);
			statement.setString(1, country.getString("name"));
			statement.setString(2, country.getString("code"));
			statement.setString(3, country.getString("currency"));
			statement.setInt(4, countryId);
			
			statement.executeUpdate();
			
			c = getCountry(countryId);

		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}
	
	public Country deleteCountry( int countryId) throws SQLException {
		Connection con = null;
		PreparedStatement statement = null;
		Country c = null;
		try{
			con = Utils.getConnection();
			c = getCountry(countryId);
			
			statement = con.prepareStatement(Queries.DELETE_COUNTRY);
			statement.setInt(1, countryId);
			
			statement.executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return c;
	}

	public List<Country> getCountries() throws SQLException {
		List<Country> countries =new ArrayList<Country>();
		Connection con = null;
		PreparedStatement statement = null;
		Country c = null;
		try{
			con = Utils.getConnection();
			statement = con.prepareStatement(Queries.GET_COUNTRIES);
			LOGGER.debug("statement - "+statement);
			ResultSet rs = statement.executeQuery();
			if(rs == null) return null;
			while (rs.next()) {
				c = new Country();
				c.setName(rs.getString("name"));
				c.setCode(rs.getString("code"));
				c.setCurrency(rs.getString("currency"));
				c.setId(rs.getInt("id"));
				countries.add(c);
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			Utils.close(con, statement);
		}
		return countries;

	}


}
