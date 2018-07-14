package com.bk.model;

public class ServicePlanInfo {
	
	private int id;
	private ServicePlanType planType; 
	private Country country; 
	private float price;
	private String rules;
	public ServicePlanType getPlanType() {
		return planType;
	}
	public void setPlanType(ServicePlanType planType) {
		this.planType = planType;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRules() {
		return rules;
	}
	public void setRules(String rules) {
		this.rules = rules;
	}
	
	

}
