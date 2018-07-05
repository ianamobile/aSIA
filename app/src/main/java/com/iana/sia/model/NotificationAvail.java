package com.iana.sia.model;

public class NotificationAvail {
	
	private Integer naId;
	private Integer sipId;
	
	private String mcCompanyName;
	private String epCompanyName;
	
	private String mcScac;
	private String epScac;
	private String loadStatus;
	private String contNum;
	
	private String contSize;
	private String contType;
	private String chassisSize;
	private String chassisType;

	private String chassisNum;
	private String gensetNum;
	
	private String equipLocSplcCode;
	private String equipLocIanaCode;
	private String equipLocNm;
	private String equipLocAddr;
	private String equipLocCity;
	private String equipLocState;
	private String equipLocZip;
	
	private String originLocSplcCode;
	private String originLocIanaCode;
	private String originLocNm;
	private String originLocAddr;
	private String originLocCity;
	private String originLocState;
	private String originLocZip;
	private String iepScac;
	
	private String accessToken;
	
	private String createdDate;
	
	private String showDeleteBtn ="N";

	public Integer getNaId() {
		return naId;
	}

	public void setNaId(Integer naId) {
		this.naId = naId;
	}

	public Integer getSipId() {
		return sipId;
	}

	public void setSipId(Integer sipId) {
		this.sipId = sipId;
	}

	public String getMcCompanyName() {
		return mcCompanyName;
	}

	public void setMcCompanyName(String mcCompanyName) {
		this.mcCompanyName = mcCompanyName;
	}

	public String getEpCompanyName() {
		return epCompanyName;
	}

	public void setEpCompanyName(String epCompanyName) {
		this.epCompanyName = epCompanyName;
	}

	public String getMcScac() {
		return mcScac;
	}

	public void setMcScac(String mcScac) {
		this.mcScac = mcScac;
	}

	public String getEpScac() {
		return epScac;
	}

	public void setEpScac(String epScac) {
		this.epScac = epScac;
	}

	public String getLoadStatus() {
		return loadStatus;
	}

	public void setLoadStatus(String loadStatus) {
		this.loadStatus = loadStatus;
	}

	public String getContNum() {
		return contNum;
	}

	public void setContNum(String contNum) {
		this.contNum = contNum;
	}

	public String getContSize() {
		return contSize;
	}

	public void setContSize(String contSize) {
		this.contSize = contSize;
	}

	public String getContType() {
		return contType;
	}

	public void setContType(String contType) {
		this.contType = contType;
	}

	public String getChassisSize() {
		return chassisSize;
	}

	public void setChassisSize(String chassisSize) {
		this.chassisSize = chassisSize;
	}

	public String getChassisType() {
		return chassisType;
	}

	public void setChassisType(String chassisType) {
		this.chassisType = chassisType;
	}

	public String getChassisNum() {
		return chassisNum;
	}

	public void setChassisNum(String chassisNum) {
		this.chassisNum = chassisNum;
	}

	public String getGensetNum() {
		return gensetNum;
	}

	public void setGensetNum(String gensetNum) {
		this.gensetNum = gensetNum;
	}

	public String getEquipLocSplcCode() {
		return equipLocSplcCode;
	}

	public void setEquipLocSplcCode(String equipLocSplcCode) {
		this.equipLocSplcCode = equipLocSplcCode;
	}

	public String getEquipLocIanaCode() {
		return equipLocIanaCode;
	}

	public void setEquipLocIanaCode(String equipLocIanaCode) {
		this.equipLocIanaCode = equipLocIanaCode;
	}

	public String getEquipLocNm() {
		return equipLocNm;
	}

	public void setEquipLocNm(String equipLocNm) {
		this.equipLocNm = equipLocNm;
	}

	public String getEquipLocAddr() {
		return equipLocAddr;
	}

	public void setEquipLocAddr(String equipLocAddr) {
		this.equipLocAddr = equipLocAddr;
	}

	public String getEquipLocCity() {
		return equipLocCity;
	}

	public void setEquipLocCity(String equipLocCity) {
		this.equipLocCity = equipLocCity;
	}

	public String getEquipLocState() {
		return equipLocState;
	}

	public void setEquipLocState(String equipLocState) {
		this.equipLocState = equipLocState;
	}

	public String getEquipLocZip() {
		return equipLocZip;
	}

	public void setEquipLocZip(String equipLocZip) {
		this.equipLocZip = equipLocZip;
	}

	public String getOriginLocSplcCode() {
		return originLocSplcCode;
	}

	public void setOriginLocSplcCode(String originLocSplcCode) {
		this.originLocSplcCode = originLocSplcCode;
	}

	public String getOriginLocIanaCode() {
		return originLocIanaCode;
	}

	public void setOriginLocIanaCode(String originLocIanaCode) {
		this.originLocIanaCode = originLocIanaCode;
	}

	public String getOriginLocNm() {
		return originLocNm;
	}

	public void setOriginLocNm(String originLocNm) {
		this.originLocNm = originLocNm;
	}

	public String getOriginLocAddr() {
		return originLocAddr;
	}

	public void setOriginLocAddr(String originLocAddr) {
		this.originLocAddr = originLocAddr;
	}

	public String getOriginLocCity() {
		return originLocCity;
	}

	public void setOriginLocCity(String originLocCity) {
		this.originLocCity = originLocCity;
	}

	public String getOriginLocState() {
		return originLocState;
	}

	public void setOriginLocState(String originLocState) {
		this.originLocState = originLocState;
	}

	public String getOriginLocZip() {
		return originLocZip;
	}

	public void setOriginLocZip(String originLocZip) {
		this.originLocZip = originLocZip;
	}

	public String getIepScac() {
		return iepScac;
	}

	public void setIepScac(String iepScac) {
		this.iepScac = iepScac;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getShowDeleteBtn() {
		return showDeleteBtn;
	}

	public void setShowDeleteBtn(String showDeleteBtn) {
		this.showDeleteBtn = showDeleteBtn;
	}


	@Override
	public String toString() {
		return "NotificationAvail{" +
				"naId=" + naId +
				", sipId=" + sipId +
				", mcCompanyName='" + mcCompanyName + '\'' +
				", epCompanyName='" + epCompanyName + '\'' +
				", mcScac='" + mcScac + '\'' +
				", epScac='" + epScac + '\'' +
				", loadStatus='" + loadStatus + '\'' +
				", contNum='" + contNum + '\'' +
				", contSize='" + contSize + '\'' +
				", contType='" + contType + '\'' +
				", chassisSize='" + chassisSize + '\'' +
				", chassisType='" + chassisType + '\'' +
				", chassisNum='" + chassisNum + '\'' +
				", gensetNum='" + gensetNum + '\'' +
				", equipLocSplcCode='" + equipLocSplcCode + '\'' +
				", equipLocIanaCode='" + equipLocIanaCode + '\'' +
				", equipLocNm='" + equipLocNm + '\'' +
				", equipLocAddr='" + equipLocAddr + '\'' +
				", equipLocCity='" + equipLocCity + '\'' +
				", equipLocState='" + equipLocState + '\'' +
				", equipLocZip='" + equipLocZip + '\'' +
				", originLocSplcCode='" + originLocSplcCode + '\'' +
				", originLocIanaCode='" + originLocIanaCode + '\'' +
				", originLocNm='" + originLocNm + '\'' +
				", originLocAddr='" + originLocAddr + '\'' +
				", originLocCity='" + originLocCity + '\'' +
				", originLocState='" + originLocState + '\'' +
				", originLocZip='" + originLocZip + '\'' +
				", iepScac='" + iepScac + '\'' +
				", accessToken='" + accessToken + '\'' +
				", createdDate='" + createdDate + '\'' +
				", showDeleteBtn='" + showDeleteBtn + '\'' +
				'}';
	}
}
