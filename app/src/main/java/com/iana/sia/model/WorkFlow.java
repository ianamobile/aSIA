package com.iana.sia.model;

public class WorkFlow {
	
	private Integer wfId;
	private Integer irId;
	private Integer pId;
	private String approvedBy;
	private String wfSeq;
	private String wfSeqType;
	private String action;
	private String status;
	private String approvedDate;
	private String displayColor;

	public Integer getWfId() {
		return wfId;
	}

	public void setWfId(Integer wfId) {
		this.wfId = wfId;
	}

	public Integer getIrId() {
		return irId;
	}

	public void setIrId(Integer irId) {
		this.irId = irId;
	}

	public Integer getpId() {
		return pId;
	}

	public void setpId(Integer pId) {
		this.pId = pId;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getWfSeq() {
		return wfSeq;
	}

	public void setWfSeq(String wfSeq) {
		this.wfSeq = wfSeq;
	}

	public String getWfSeqType() {
		return wfSeqType;
	}

	public void setWfSeqType(String wfSeqType) {
		this.wfSeqType = wfSeqType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getDisplayColor() {
		return displayColor;
	}

	public void setDisplayColor(String displayColor) {
		this.displayColor = displayColor;
	}

    @Override
    public String toString() {
        return "WorkFlow{" +
                "wfId=" + wfId +
                ", irId=" + irId +
                ", pId=" + pId +
                ", approvedBy='" + approvedBy + '\'' +
                ", wfSeq='" + wfSeq + '\'' +
                ", wfSeqType='" + wfSeqType + '\'' +
                ", action='" + action + '\'' +
                ", status='" + status + '\'' +
                ", approvedDate='" + approvedDate + '\'' +
                ", displayColor='" + displayColor + '\'' +
                '}';
    }
}
