package com.bk.model;

public class Customer {
	
	private int id;
	private String name;
	private String email;
	private ServicePlanType planType;
	private float price;
	private Country country;
	private long signUpTimestamp;
	private long nextBillingTimestamp;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public ServicePlanType getPlanType() {
		return planType;
	}
	public void setPlanType(ServicePlanType planType) {
		this.planType = planType;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getSignUpTimestamp() {
		return signUpTimestamp;
	}
	public void setSignUpTimestamp(long signUpTimestamp) {
		this.signUpTimestamp = signUpTimestamp;
	}
	public long getNextBillingTimestamp() {
		return nextBillingTimestamp;
	}
	public void setNextBillingTimestamp(long nextBillingTimestamp) {
		this.nextBillingTimestamp = nextBillingTimestamp;
	}
	
	

}
