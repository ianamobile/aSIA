package com.iana.sia.utility;

public class Errors {

	private Integer transNum;
	private String errorCategory;
	private String errorMessage;
	
	public Errors(Integer transNum , String errorCategory, String errorMessage){
		this.transNum = transNum;
		this.errorCategory = errorCategory;
		this.errorMessage = errorMessage;
	}
	
	public Errors(String errorCategory, String errorMessage){
		this.errorCategory = errorCategory;
		this.errorMessage = errorMessage;
	}
	
	public Integer getTransNum() {
		return transNum;
	}
	public void setTransNum(Integer transNum) {
		this.transNum = transNum;
	}
	public String getErrorCategory() {
		return errorCategory;
	}
	public void setErrorCategory(String errorCategory) {
		this.errorCategory = errorCategory;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	@Override
	public String toString() {
		return "Errors [transNum=" + transNum + ", errorCategory="
				+ errorCategory + ", errorMessage=" + errorMessage + "]";
	}
	

}
