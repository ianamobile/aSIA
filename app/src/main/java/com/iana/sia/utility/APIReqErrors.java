package com.iana.sia.utility;

import java.util.List;

public class APIReqErrors {

	private List<Errors> errors;

	public List<Errors> getErrors() {
		return errors;
	}

	public void setErrors(List<Errors> errors) {
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "APIReqErrors [errors=" + errors + "]";
	}

}
