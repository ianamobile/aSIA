package com.iana.sia.model;

public class UIIAExhibit {

	private Integer ueId;
	private String item;
	private String itemNo;
	private String itemDesc;

	public Integer getUeId() {
		return ueId;
	}

	public void setUeId(Integer ueId) {
		this.ueId = ueId;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	@Override
	public String toString() {
		return "UIIAExhibit{" +
				"ueId=" + ueId +
				", item='" + item + '\'' +
				", itemNo='" + itemNo + '\'' +
				", itemDesc='" + itemDesc + '\'' +
				'}';
	}
}
