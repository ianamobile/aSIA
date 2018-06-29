package com.iana.sia.model;

public class Profile {
	
	private Integer sipId;
	private String companyName;
	private String scac;
	private String uiiaAcctNo;
	private String uiiaUserType;
	private String userName;
	private String pwd;
	private String fName;
	private String mName;
	private String lName;
	private String email;
	private String status;
	private String phone;
	private String fax;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String zip;
	private String state;
	
	private String roleName;
	private String iddPin;
	private String drvLicNo;
	private String drvLicState;
	private String memType;
	private boolean configDetailsSetFlag;

	public Integer getSipId() {
		return sipId;
	}

	public void setSipId(Integer sipId) {
		this.sipId = sipId;
	}

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

	public String getUiiaAcctNo() {
		return uiiaAcctNo;
	}

	public void setUiiaAcctNo(String uiiaAcctNo) {
		this.uiiaAcctNo = uiiaAcctNo;
	}

	public String getUiiaUserType() {
		return uiiaUserType;
	}

	public void setUiiaUserType(String uiiaUserType) {
		this.uiiaUserType = uiiaUserType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public String getDrvLicNo() {
		return drvLicNo;
	}

	public void setDrvLicNo(String drvLicNo) {
		this.drvLicNo = drvLicNo;
	}

	public String getDrvLicState() {
		return drvLicState;
	}

	public void setDrvLicState(String drvLicState) {
		this.drvLicState = drvLicState;
	}

	public String getMemType() {
		return memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public boolean isConfigDetailsSetFlag() {
		return configDetailsSetFlag;
	}

	public void setConfigDetailsSetFlag(boolean configDetailsSetFlag) {
		this.configDetailsSetFlag = configDetailsSetFlag;
	}

	@Override
	public String toString() {
		return "Profile{" +
				"sipId=" + sipId +
				", companyName='" + companyName + '\'' +
				", scac='" + scac + '\'' +
				", uiiaAcctNo='" + uiiaAcctNo + '\'' +
				", uiiaUserType='" + uiiaUserType + '\'' +
				", userName='" + userName + '\'' +
				", pwd='" + pwd + '\'' +
				", fName='" + fName + '\'' +
				", mName='" + mName + '\'' +
				", lName='" + lName + '\'' +
				", email='" + email + '\'' +
				", status='" + status + '\'' +
				", phone='" + phone + '\'' +
				", fax='" + fax + '\'' +
				", addressLine1='" + addressLine1 + '\'' +
				", addressLine2='" + addressLine2 + '\'' +
				", city='" + city + '\'' +
				", zip='" + zip + '\'' +
				", state='" + state + '\'' +
				", roleName='" + roleName + '\'' +
				", iddPin='" + iddPin + '\'' +
				", drvLicNo='" + drvLicNo + '\'' +
				", drvLicState='" + drvLicState + '\'' +
				", memType='" + memType + '\'' +
				", configDetailsSetFlag=" + configDetailsSetFlag +
				'}';
	}
}




