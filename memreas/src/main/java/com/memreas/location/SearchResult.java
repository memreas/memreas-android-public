package com.memreas.location;

public class SearchResult {
	private String reference;
	private String address;

	public SearchResult(String reference, String address) {
		super();
		this.reference = reference;
		this.address = address;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return address;
	}
}
