package com.bk.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.jeasy.rules.api.Rules;

import com.bk.rules.LoyalUSCustomerRule;
import com.bk.rules.NewCustomerRule;

import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

public class Utils {
	
	public static void registerRules(Rules rules, String ruleNames){
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

		ipFinder.setAddresses(Arrays.asList("localhost:47500..47509"));

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

		String url = "jdbc:mysql://pricing-service.ctvtfblvqq38.us-east-1.rds.amazonaws.com:3306/psdb";
		String user = "bkadapak";
		String password = "hello123";
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = DriverManager.getConnection(url+"?user="+user+"&password="+password);

		return connection;
	}
}
