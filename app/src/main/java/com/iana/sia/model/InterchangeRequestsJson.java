package com.iana.sia.model;

import com.iana.sia.utility.GlobalVariables;

import java.util.List;
import java.util.Map;

public class InterchangeRequestsJson {

	private String memType;
	private String accessToken;
	private String sqlQuery;
	private InterchangeRequests interchangeRequests;
	private List<Map<String,Object>> uiiaExhibitDataList;
	private List<WorkFlow> workFlowList;
	private WorkFlow inProcessWf; 
	private Integer startRecord;
	private Integer endRecord;
	private String startDate;
	private String endDate;
	private String uiiaExhibitStr;
	private Integer naId;
	private boolean loggedInUserEligibleForApproval = false;
	private String isEPUser = GlobalVariables.N;
	private String uiiaExhibitInputReq = GlobalVariables.N;
	private String tpuEpScac;
	private String ipAddress;
	private String showReinstateButton="N"; // whether reinstate button displayed or not
	
	/* Added new fields for mobile developers */
	private String showButtons 	  = GlobalVariables.N;
	private String showCancelButtons = GlobalVariables.N;
	private String holdButtonShow = GlobalVariables.N;


	public String getMemType() {
		return memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public InterchangeRequests getInterchangeRequests() {
		return interchangeRequests;
	}

	public void setInterchangeRequests(InterchangeRequests interchangeRequests) {
		this.interchangeRequests = interchangeRequests;
	}

	public List<Map<String, Object>> getUiiaExhibitDataList() {
		return uiiaExhibitDataList;
	}

	public void setUiiaExhibitDataList(List<Map<String, Object>> uiiaExhibitDataList) {
		this.uiiaExhibitDataList = uiiaExhibitDataList;
	}

	public List<WorkFlow> getWorkFlowList() {
		return workFlowList;
	}

	public void setWorkFlowList(List<WorkFlow> workFlowList) {
		this.workFlowList = workFlowList;
	}

	public WorkFlow getInProcessWf() {
		return inProcessWf;
	}

	public void setInProcessWf(WorkFlow inProcessWf) {
		this.inProcessWf = inProcessWf;
	}

	public Integer getStartRecord() {
		return startRecord;
	}

	public void setStartRecord(Integer startRecord) {
		this.startRecord = startRecord;
	}

	public Integer getEndRecord() {
		return endRecord;
	}

	public void setEndRecord(Integer endRecord) {
		this.endRecord = endRecord;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getUiiaExhibitStr() {
		return uiiaExhibitStr;
	}

	public void setUiiaExhibitStr(String uiiaExhibitStr) {
		this.uiiaExhibitStr = uiiaExhibitStr;
	}

	public Integer getNaId() {
		return naId;
	}

	public void setNaId(Integer naId) {
		this.naId = naId;
	}

	public boolean isLoggedInUserEligibleForApproval() {
		return loggedInUserEligibleForApproval;
	}

	public void setLoggedInUserEligibleForApproval(boolean loggedInUserEligibleForApproval) {
		this.loggedInUserEligibleForApproval = loggedInUserEligibleForApproval;
	}

	public String getIsEPUser() {
		return isEPUser;
	}

	public void setIsEPUser(String isEPUser) {
		this.isEPUser = isEPUser;
	}

	public String getUiiaExhibitInputReq() {
		return uiiaExhibitInputReq;
	}

	public void setUiiaExhibitInputReq(String uiiaExhibitInputReq) {
		this.uiiaExhibitInputReq = uiiaExhibitInputReq;
	}

	public String getTpuEpScac() {
		return tpuEpScac;
	}

	public void setTpuEpScac(String tpuEpScac) {
		this.tpuEpScac = tpuEpScac;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getShowReinstateButton() {
		return showReinstateButton;
	}

	public void setShowReinstateButton(String showReinstateButton) {
		this.showReinstateButton = showReinstateButton;
	}

	public String getShowButtons() {
		return showButtons;
	}

	public void setShowButtons(String showButtons) {
		this.showButtons = showButtons;
	}

	public String getShowCancelButtons() {
		return showCancelButtons;
	}

	public void setShowCancelButtons(String showCancelButtons) {
		this.showCancelButtons = showCancelButtons;
	}

	public String getHoldButtonShow() {
		return holdButtonShow;
	}

	public void setHoldButtonShow(String holdButtonShow) {
		this.holdButtonShow = holdButtonShow;
	}

	@Override
	public String toString() {
		return "InterchangeRequestsJson{" +
				"memType='" + memType + '\'' +
				", accessToken='" + accessToken + '\'' +
				", sqlQuery='" + sqlQuery + '\'' +
				", interchangeRequests=" + interchangeRequests +
				", uiiaExhibitDataList=" + uiiaExhibitDataList +
				", workFlowList=" + workFlowList +
				", inProcessWf=" + inProcessWf +
				", startRecord=" + startRecord +
				", endRecord=" + endRecord +
				", startDate='" + startDate + '\'' +
				", endDate='" + endDate + '\'' +
				", uiiaExhibitStr='" + uiiaExhibitStr + '\'' +
				", naId=" + naId +
				", loggedInUserEligibleForApproval=" + loggedInUserEligibleForApproval +
				", isEPUser='" + isEPUser + '\'' +
				", uiiaExhibitInputReq='" + uiiaExhibitInputReq + '\'' +
				", tpuEpScac='" + tpuEpScac + '\'' +
				", ipAddress='" + ipAddress + '\'' +
				", showReinstateButton='" + showReinstateButton + '\'' +
				", showButtons='" + showButtons + '\'' +
				", showCancelButtons='" + showCancelButtons + '\'' +
				", holdButtonShow='" + holdButtonShow + '\'' +
				'}';
	}
}
