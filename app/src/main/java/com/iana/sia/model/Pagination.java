package com.iana.sia.model;


public class Pagination {

	private int size;
	private int totalElements;
	private int totalPages;
	private int currentPage;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public String toString() {
		return "Pagination{" +
				"size=" + size +
				", totalElements=" + totalElements +
				", totalPages=" + totalPages +
				", currentPage=" + currentPage +
				'}';
	}
}
