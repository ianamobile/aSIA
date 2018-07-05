package com.iana.sia.model;

public class NotifAvailSearch {

	private String containerNumber;
	private String mcScac;
	private String epScac;
	private String fromDate;
	private String toDate;
    private Integer offset;
    private Integer limit;

	public String getContainerNumber() {
		return containerNumber;
	}

	public void setContainerNumber(String containerNumber) {
		this.containerNumber = containerNumber;
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

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "NotifAvailSearch{" +
                "containerNumber='" + containerNumber + '\'' +
                ", mcScac='" + mcScac + '\'' +
                ", epScac='" + epScac + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
