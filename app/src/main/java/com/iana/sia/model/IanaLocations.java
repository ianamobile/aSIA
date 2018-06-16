package com.iana.sia.model;

public class IanaLocations {

	private String state;
	private String city;
	private String zip;
	private String locName;
	private String addr;
	private String ianaCode;
	private String splcCode;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getLocName() {
		return locName;
	}

	public void setLocName(String locName) {
		this.locName = locName;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getIanaCode() {
		return ianaCode;
	}

	public void setIanaCode(String ianaCode) {
		this.ianaCode = ianaCode;
	}

	public String getSplcCode() {
		return splcCode;
	}

	public void setSplcCode(String splcCode) {
		this.splcCode = splcCode;
	}

	@Override
	public String toString() {
		return "IanaLocations{" +
				"state='" + state + '\'' +
				", city='" + city + '\'' +
				", zip='" + zip + '\'' +
				", locName='" + locName + '\'' +
				", addr='" + addr + '\'' +
				", ianaCode='" + ianaCode + '\'' +
				", splcCode='" + splcCode + '\'' +
				'}';
	}
}
