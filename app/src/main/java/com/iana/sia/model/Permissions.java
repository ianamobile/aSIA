package com.iana.sia.model;

public class Permissions {
	
	private String iniIntrchng;
	private String iniIntrchngAndApprove;
	
	public String getIniIntrchng() {
		return iniIntrchng;
	}
	public void setIniIntrchng(String iniIntrchng) {
		this.iniIntrchng = iniIntrchng;
	}
	public String getIniIntrchngAndApprove() {
		return iniIntrchngAndApprove;
	}
	public void setIniIntrchngAndApprove(String iniIntrchngAndApprove) {
		this.iniIntrchngAndApprove = iniIntrchngAndApprove;
	}
	
	@Override
	public String toString() {
		return "Permissions [iniIntrchng=" + iniIntrchng
				+ ", iniIntrchngAndApprove=" + iniIntrchngAndApprove + "]";
	}

	
	
}
