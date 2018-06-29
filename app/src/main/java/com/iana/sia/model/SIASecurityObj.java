package com.iana.sia.model;

public class SIASecurityObj {

	private String uiiaAcctNo;
	private String scac;
	private String companyName;
	private String userName;
	private String roleName;
	private Long sipId;
	private String fName;
	private String mName;
	private String lName;
	private String status;
	private String memType;
	
	/* IDD User Login Fields */
	private String iddPin;
	private String drvLicenseNo;
	private String drvLicenseState;
	
	//@JsonIgnore
	private String ipAddress;
	private String accessToken; // It is used in login API - when user logged in first time.
	private boolean configDetailsSetFlag; //required in case of EP users. 

	// secondary users permission for accessing initiate interchange request & operation
	private Permissions permissions;

	public String getUiiaAcctNo() {
		return uiiaAcctNo;
	}

	public void setUiiaAcctNo(String uiiaAcctNo) {
		this.uiiaAcctNo = uiiaAcctNo;
	}

	public String getScac() {
		return scac;
	}

	public void setScac(String scac) {
		this.scac = scac;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Long getSipId() {
		return sipId;
	}

	public void setSipId(Long sipId) {
		this.sipId = sipId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMemType() {
		return memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public String getIddPin() {
		return iddPin;
	}

	public void setIddPin(String iddPin) {
		this.iddPin = iddPin;
	}

	public String getDrvLicenseNo() {
		return drvLicenseNo;
	}

	public void setDrvLicenseNo(String drvLicenseNo) {
		this.drvLicenseNo = drvLicenseNo;
	}

	public String getDrvLicenseState() {
		return drvLicenseState;
	}

	public void setDrvLicenseState(String drvLicenseState) {
		this.drvLicenseState = drvLicenseState;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public boolean isConfigDetailsSetFlag() {
		return configDetailsSetFlag;
	}

	public void setConfigDetailsSetFlag(boolean configDetailsSetFlag) {
		this.configDetailsSetFlag = configDetailsSetFlag;
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return "SIASecurityObj{" +
				"uiiaAcctNo='" + uiiaAcctNo + '\'' +
				", scac='" + scac + '\'' +
				", companyName='" + companyName + '\'' +
				", userName='" + userName + '\'' +
				", roleName='" + roleName + '\'' +
				", sipId=" + sipId +
				", fName='" + fName + '\'' +
				", mName='" + mName + '\'' +
				", lName='" + lName + '\'' +
				", status='" + status + '\'' +
				", memType='" + memType + '\'' +
				", iddPin='" + iddPin + '\'' +
				", drvLicenseNo='" + drvLicenseNo + '\'' +
				", drvLicenseState='" + drvLicenseState + '\'' +
				", ipAddress='" + ipAddress + '\'' +
				", accessToken='" + accessToken + '\'' +
				", configDetailsSetFlag=" + configDetailsSetFlag +
				", permissions=" + permissions +
				'}';
	}
}
