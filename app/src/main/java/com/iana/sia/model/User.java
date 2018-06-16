package com.iana.sia.model;

public class User {

    public String accessToken;
	public String userName;
	public String password;
	public String role;
	public String roleName;
	public String iddPin;
	public String driverLicenseNumber;
	public String driverLicenseState;
	public String originFrom;
	public String scac;
	public String companyName;
	public String memType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getIddPin() {
		return iddPin;
	}

	public void setIddPin(String iddPin) {
		this.iddPin = iddPin;
	}

	public String getDriverLicenseNumber() {
		return driverLicenseNumber;
	}

	public void setDriverLicenseNumber(String driverLicenseNumber) {
		this.driverLicenseNumber = driverLicenseNumber;
	}

	public String getDriverLicenseState() {
		return driverLicenseState;
	}

	public void setDriverLicenseState(String driverLicenseState) {
		this.driverLicenseState = driverLicenseState;
	}

	public String getOriginFrom() {
		return originFrom;
	}

	public void setOriginFrom(String originFrom) {
		this.originFrom = originFrom;
	}

	public String getScac() {
		return scac;
	}

	public void setScac(String scac) {
		this.scac = scac;
	}

	public String getMemType() {
		return memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		return "User{" +
				"accessToken='" + accessToken + '\'' +
				", userName='" + userName + '\'' +
				", password='" + password + '\'' +
				", role='" + role + '\'' +
				", roleName='" + roleName + '\'' +
				", iddPin='" + iddPin + '\'' +
				", driverLicenseNumber='" + driverLicenseNumber + '\'' +
				", driverLicenseState='" + driverLicenseState + '\'' +
				", originFrom='" + originFrom + '\'' +
				", scac='" + scac + '\'' +
				", companyName='" + companyName + '\'' +
				", memType='" + memType + '\'' +
				'}';
	}
}
