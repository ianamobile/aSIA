package com.iana.sia.model;

public class Company {

	private String companyName;
	private String scac;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getScac() {
		return scac;
	}

	public void setScac(String scac) {
		this.scac = scac;
	}

	@Override
	public String toString() {
		return "Company{" +
				"companyName='" + companyName + '\'' +
				", scac='" + scac + '\'' +
				'}';
	}
}
