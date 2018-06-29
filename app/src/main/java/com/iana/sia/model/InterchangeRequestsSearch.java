package com.iana.sia.model;

public class InterchangeRequestsSearch {

	private String scac;
	private String bookingNum;
	private String contNum;
	private String status;
	private String fromDate;
	private String toDate;

	public String getScac() {
		return scac;
	}

	public void setScac(String scac) {
		this.scac = scac;
	}

	public String getBookingNum() {
		return bookingNum;
	}

	public void setBookingNum(String bookingNum) {
		this.bookingNum = bookingNum;
	}

	public String getContNum() {
		return contNum;
	}

	public void setContNum(String contNum) {
		this.contNum = contNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		return "InterchangeRequestsSearch{" +
				"scac='" + scac + '\'' +
				", bookingNum='" + bookingNum + '\'' +
				", contNum='" + contNum + '\'' +
				", status='" + status + '\'' +
				", fromDate='" + fromDate + '\'' +
				", toDate='" + toDate + '\'' +
				'}';
	}
}
