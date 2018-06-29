package com.iana.sia.model;

import java.util.List;

public class EPUsersByTPUResult {

	 private List<Profile> epUserList;
	 private Pagination page;

	public List<Profile> getEpUserList() {
		return epUserList;
	}

	public void setEpUserList(List<Profile> epUserList) {
		this.epUserList = epUserList;
	}

	public Pagination getPage() {
		return page;
	}

	public void setPage(Pagination page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "EPUsersByTPUResult{" +
				"epUserList=" + epUserList +
				", page=" + page +
				'}';
	}
}
