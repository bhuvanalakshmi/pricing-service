package com.bk.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.jeasy.rules.api.Rules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bk.rules.LoyalUSCustomerRule;
import com.bk.rules.NewCustomerRule;

import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

public class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	public static void registerRules(Rules rules, String ruleNames){
		if(ruleNames == null) return;
		String[] ruleArr = ruleNames.split(",");
		for(String ruleName : ruleArr){
			if("new_customer_rule".equalsIgnoreCase(ruleName))
				rules.register(new NewCustomerRule());
			else if("loyal_US_customer".equalsIgnoreCase(ruleName))
				rules.register(new LoyalUSCustomerRule());
		}
	}

	public static ClusterManager getIgniteClusterManagerConfig() {
		TcpDiscoverySpi spi = new TcpDiscoverySpi();

		TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();

		ipFinder.setAddresses(Arrays.asList(getProperties().getProperty("ignite.ipaddresses")));

		spi.setIpFinder(ipFinder);

		IgniteConfiguration cfg = new IgniteConfiguration();

		cfg.setDiscoverySpi(spi);
		ClusterManager clusterManager = new IgniteClusterManager(cfg);
		return clusterManager;
	}

	public static void close(Connection con, PreparedStatement statement) throws SQLException {
		if (statement != null) {
			statement.close();
		}

		if (con != null) {
			con.close();
		}
	}

	public static Connection getConnection() throws SQLException, ClassNotFoundException{

		Properties props = getProperties();

		Class.forName(props.getProperty("db.driver"));


		Connection connection = DriverManager.getConnection("jdbc:mysql://"+props.getProperty("db.host")+":"+
				props.getProperty("db.port")+"/"+
				props.getProperty("db.name")+
				"?user="+props.getProperty("db.user")+
				"&password="+props.getProperty("db.password"));
		return connection;
	}


	public static Properties getProperties(){
		Properties prop = new Properties();
		InputStream input = null;
		try {

			input = Utils.class.getClassLoader().getResourceAsStream("config.properties");
			// load a properties file
			prop.load(input);


		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return prop;
	}
}
