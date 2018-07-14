package com.bk.model;

public enum ServicePlanType {

	basic, 
	normal, 
	premium; 

	public static ServicePlanType fromValue(String planType){
		if(ServicePlanType.basic.name().equalsIgnoreCase(planType))
			return ServicePlanType.basic;
		else if(ServicePlanType.normal.name().equalsIgnoreCase(planType))
			return ServicePlanType.normal;
		else if(ServicePlanType.premium.name().equalsIgnoreCase(planType))
			return ServicePlanType.premium;
		else
			return null;
	}
}
